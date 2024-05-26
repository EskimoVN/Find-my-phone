package com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class LazyLoadingViewPager extends ViewPager {
    private static final int INVALID_DATA = -1;

    private boolean mIsDragging = false;
    private VelocityTracker mVelocityTracker;
    private int mActivePointerId = INVALID_DATA;
    private float mInitialMotionX;
    private boolean mHasCenterDraggableParent = false;
    private boolean mSwipingEnabled = true;
    private boolean mDisableSmoothScroll = false;

    public LazyLoadingViewPager(@NonNull Context context) {
        this(context, null);
    }

    public LazyLoadingViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mIsDragging = state == SCROLL_STATE_DRAGGING;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mSwipingEnabled) {
            return false;
        }

        int action = ev.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mInitialMotionX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mHasCenterDraggableParent && getCurrentItem() == 0) {
                    if (ev.getX() - mInitialMotionX > 0 && !mIsDragging) {
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resetTouch();
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mSwipingEnabled) {
            return false;
        }

        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mInitialMotionX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mHasCenterDraggableParent && getCurrentItem() == 0) {
                    if (ev.getX() - mInitialMotionX > 0 && !mIsDragging) {
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                try {
                    PagerAdapter adapter = getAdapter();

                    if (mIsDragging && adapter != null && adapter.getCount() > 1) {
                        VelocityTracker velocityTracker = mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity());
                        int initialVelocity = (int) velocityTracker.getXVelocity(mActivePointerId);

                        final float positionOffset;
                        final int currentPage;

                        Method getCurrentScrollPositionInfoMethod = Objects.requireNonNull(getClass().getSuperclass())
                                .getDeclaredMethod("infoForCurrentScrollPosition");
                        getCurrentScrollPositionInfoMethod.setAccessible(true);
                        Object itemInfo = getCurrentScrollPositionInfoMethod.invoke(this);
                        Field offsetField = Objects.requireNonNull(itemInfo).getClass().getDeclaredField("offset");
                        offsetField.setAccessible(true);
                        positionOffset = offsetField.getFloat(itemInfo);
                        Field positionField = itemInfo.getClass().getDeclaredField("position");
                        positionField.setAccessible(true);
                        currentPage = positionField.getInt(itemInfo);

                        final int width = getClientWidth();
                        final int scrollX = getScrollX();
                        final float marginOffset = (float) getPageMargin() / width;
                        final float pageOffset = (((float) scrollX / width) - positionOffset)
                                / (getAdapter().getPageWidth(currentPage) + marginOffset);
                        final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                        final float x = ev.getX(activePointerIndex);
                        final int totalDelta = (int) (x - mInitialMotionX);

                        Method determineTargetPageMethod = Objects.requireNonNull(getClass().getSuperclass())
                                .getDeclaredMethod("determineTargetPage", int.class, float.class, int.class, int.class);
                        determineTargetPageMethod.setAccessible(true);
                        int nextPage = (int) Objects.requireNonNull(determineTargetPageMethod
                                .invoke(this, currentPage, pageOffset, initialVelocity, totalDelta));
                        Fragment fragment = (Fragment) adapter.instantiateItem(this, nextPage);
                        if (fragment instanceof BaseLazyInflatingFragment && !((BaseLazyInflatingFragment) fragment).isInflated()) {
                            scrollToItemImmediately(nextPage, ev);
                        }
                    }
                    resetTouch();
                } catch (Exception ignored) {
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                resetTouch();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mActivePointerId = ev.getPointerId(ev.getActionIndex());
                break;
        }

        return super.onTouchEvent(ev);
    }

    private int getClientWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private void scrollToItemImmediately(int position, MotionEvent ev) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        scrollToItemImmediately(position);
        ev.setAction(MotionEvent.ACTION_CANCEL);
    }

    private void scrollToItemImmediately(int position) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        super.setCurrentItem(position, false);
        Method method = Objects.requireNonNull(getClass().getSuperclass()).getDeclaredMethod("endDrag");
        method.setAccessible(true);
        method.invoke(this);
    }

    private void resetTouch() {
        mActivePointerId = INVALID_DATA;
        mInitialMotionX = INVALID_DATA;
    }

    public void setHasCenterDraggableParent(boolean hasCenterDraggableParent) {
        mHasCenterDraggableParent = hasCenterDraggableParent;
    }

    public void setSwipingEnabled(boolean swipingEnabled, boolean disableSmoothScroll) {
        mSwipingEnabled = swipingEnabled;
        mDisableSmoothScroll = disableSmoothScroll;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return mSwipingEnabled && super.canScrollHorizontally(direction);
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (mDisableSmoothScroll) {
            try {
                scrollToItemImmediately(item);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                setCurrentItem(item);
            }
        } else {
            super.setCurrentItem(item, smoothScroll);
        }
    }
}