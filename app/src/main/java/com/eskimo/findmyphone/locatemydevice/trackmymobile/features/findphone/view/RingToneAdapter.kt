package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ItemPickBottomSheetBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ItemSoundBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.RingToneModel
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.views.LanguageAdapter

class RingToneAdapter(
    private var id: Int,
    private val listItem: List<RingToneModel>,
    private val callback: Callback,
    private val context: Context,
) : RecyclerView.Adapter<RingToneAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(ringToneModel: RingToneModel, position: Int) {
            val binding = ItemSoundBinding.bind(itemView)
            binding.root.setOnSafeClickListener {
                callback.onSelectRingTone(ringToneModel)
                id = ringToneModel.id
                notifyDataSetChanged()
            }
            binding.textViewName.text = ringToneModel.name
            if (id == ringToneModel.id) {
                binding.root.isSelected = true
                binding.textViewName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
                binding.textViewName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
                binding.textViewName.typeface =
                    ResourcesCompat.getFont(context, R.font.poppins_bold)
            } else {
                binding.root.isSelected = false
                binding.textViewName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )
                binding.textViewName.typeface =
                    ResourcesCompat.getFont(context, R.font.poppins_regular)
            }
            binding.iconSound.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    ringToneModel.image
                )
            )
        }
    }

    interface Callback {
        fun onSelectRingTone(itemSelected: RingToneModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sound, parent, false)
        )
    }

    override fun getItemCount() = listItem.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listItem[position], position)
    }
}
