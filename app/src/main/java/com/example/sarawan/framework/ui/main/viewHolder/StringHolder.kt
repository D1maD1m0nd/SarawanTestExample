package com.example.sarawan.framework.ui.main.viewHolder

import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.R
import com.example.sarawan.model.data.MainScreenDataModel
import com.google.android.material.textview.MaterialTextView

class StringHolder(private val view: MaterialTextView) :
    RecyclerView.ViewHolder(view) {

    private val scale: Float = view.resources.displayMetrics.density

    fun bind(data: MainScreenDataModel) {
        with(view) {
            gravity = data.gravity ?: Gravity.START
            textSize = data.fontSize ?: 0F
            text = data.itemDescription
            val color = data.backgroundColor ?: ContextCompat.getColor(
                view.context,
                R.color.top_card_background
            )
            setBackgroundColor(color)
            data.padding?.let {
                val left = convertToPixels(it[0])
                val top = convertToPixels(it[1])
                val right = convertToPixels(it[2])
                val bottom = convertToPixels(it[3])
                setPadding(left, top, right, bottom)
            }
            data.fontType?.let {
                setTypeface(typeface, it)
            }
            setTextColor(resources.getColor(data.textColor ?: R.color.card_text_color, null))
        }
    }

    private fun convertToPixels(sizeInDp: Int) = (sizeInDp * scale + 0.5f).toInt()
}