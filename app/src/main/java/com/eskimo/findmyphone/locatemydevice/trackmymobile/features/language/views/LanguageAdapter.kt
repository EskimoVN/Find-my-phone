package com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.eskimo.findmyphone.locatemydevice.trackmymobile.R
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.SharedPreferencesManager
import com.eskimo.findmyphone.locatemydevice.trackmymobile.common.extensions.Extensions.setOnSafeClickListener
import com.eskimo.findmyphone.locatemydevice.trackmymobile.databinding.ItemLanguageBinding
import com.eskimo.findmyphone.locatemydevice.trackmymobile.features.language.datas.Language


class LanguageAdapter(
    private val context: Context,
    private val languages: List<Language>,
    val onClickItem: (lang: Language) -> Unit,
) :
    RecyclerView.Adapter<LanguageAdapter.LanguageFirstOpenViewHolder>() {

    private var selectedItem: Language? =
        languages.find { it.code == SharedPreferencesManager.getAppLanguage() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageFirstOpenViewHolder {
        return LanguageFirstOpenViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_language, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LanguageFirstOpenViewHolder, position: Int) {
        val language = languages[position]
        holder.binData(language)
    }

    override fun getItemCount(): Int {
        return languages.size
    }

    inner class LanguageFirstOpenViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun binData(data: Language) {
            val binding = ItemLanguageBinding.bind(itemView)
            binding.tvNameLanguage.text = data.name
            binding.imgIconLanguage.setImageResource(data.idIcon)
            if (selectedItem?.code == data.code) {
                binding.root.isSelected = true
                binding.tvNameLanguage.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
                binding.tvNameLanguage.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
                binding.tvNameLanguage.typeface =
                    ResourcesCompat.getFont(context, R.font.poppins_bold);
            } else {
                binding.root.isSelected = false
                binding.tvNameLanguage.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )
                binding.tvNameLanguage.typeface =
                    ResourcesCompat.getFont(context, R.font.poppins_regular);
            }
            binding.root.setOnSafeClickListener {
                selectedItem = data
                onClickItem(selectedItem!!)
                notifyDataSetChanged()
            }
        }
    }
}