package com.example.sarawan.framework.ui.main.viewHolder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.R
import com.example.sarawan.app.App
import com.example.sarawan.databinding.ListItemButtonBinding
import com.example.sarawan.model.data.MainScreenDataModel

class ButtonHolder(private val binding: ListItemButtonBinding, private val listener: ButtonMoreClickListener) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: MainScreenDataModel) {
        with(binding) {
            button.text = data.itemDescription
            root.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.top_card_background
                )
            )
            button.setOnClickListener {
                listener.onButtonClick()
            }
        }
    }
}

fun interface ButtonMoreClickListener {
    fun onButtonClick()
}