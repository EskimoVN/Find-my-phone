package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.MyApplication
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ItemPickBottomSheetBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.FlashModel
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.findphone.models.TypeFlash

class TypeFlashAdapter(
    private val itemSelected: FlashModel,
    private val listItem: List<FlashModel>,
    private val callback: TypeOfFlashBottomSheet.Callback,
) : RecyclerView.Adapter<TypeFlashAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(typeFlash: FlashModel, position: Int) {
            val binding = ItemPickBottomSheetBinding.bind(itemView)
            binding.root.setOnSafeClickListener {
                callback.onSelectedTypeGenQR(typeFlash)
            }
            binding.textViewType.text = typeFlash.name
            if (itemSelected.name == typeFlash.name) {
                binding.buttonCheck.isSelected = true
                binding.textViewType.setTextColor(
                    MyApplication.getApplication().getColor(R.color.dark_blue)
                )
            } else {
                binding.buttonCheck.isSelected = false
                binding.textViewType.setTextColor(
                    MyApplication.getApplication().getColor(R.color.black)
                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPickBottomSheetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun getItemCount() = listItem.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listItem[position], position)
    }
}