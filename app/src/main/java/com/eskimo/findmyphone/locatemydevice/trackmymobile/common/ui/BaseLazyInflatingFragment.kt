package com.eskimo.findmyphone.locatemydevice.trackmymobile.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.annotation.CallSuper
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R

abstract class BaseLazyInflatingFragment : BaseFragment() {
    private var savedInstanceState: Bundle? = null
    var isInflated = false
    private var viewStub: ViewStub? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_viewstub, container, false)
        viewStub = view.findViewById(R.id.view_stub)
        viewStub!!.layoutResource = getLayoutResource()
        this.savedInstanceState = savedInstanceState
        if (isResumed && !isInflated) {
            inflateView(view)
        }
        return view
    }

    protected abstract fun onCreateViewAfterViewStubInflated(
        inflatedView: View?,
        savedInstanceState: Bundle?,
    )

    /**
     * @param originalViewContainerWithViewStub
     */
    @CallSuper
    protected open fun afterViewStubInflated(originalViewContainerWithViewStub: View?) {
        isInflated = true
        if (originalViewContainerWithViewStub != null) {
            val pb =
                originalViewContainerWithViewStub.findViewById<View>(R.id.skeleton_layout)
            pb.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()

        if (viewStub != null && !isInflated) {
            inflateView(requireView())
        }
    }

    private fun inflateView(originalViewContainer: View) {
        val inflatedView = viewStub!!.inflate()
        afterViewStubInflated(originalViewContainer)
        onCreateViewAfterViewStubInflated(inflatedView, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isInflated = false
    }

    protected abstract fun getLayoutResource(): Int
}