package com.example.sarawan.framework.ui.main.viewHolder

import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.R
import com.example.sarawan.model.data.DataModel
import com.google.android.material.textview.MaterialTextView

class StringHolder(private val view: MaterialTextView) :
    RecyclerView.ViewHolder(view) {
    fun bind(data: DataModel) {
        with(view) {
            gravity = if (data.discount == 0) Gravity.CENTER else Gravity.START
            textSize = data.price ?: 0F
            text = data.itemDescription
            val color = data.id?.toInt() ?: ContextCompat.getColor(
                view.context,
                R.color.top_card_background
            )
            setBackgroundColor(color)
            setPadding(0, 32, 0, 14)
            setTextColor(resources.getColor(R.color.card_text_color, null))
        }
    }
}