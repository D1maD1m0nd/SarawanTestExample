package ru.sarawan.android.framework.ui.main.viewHolder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.sarawan.android.R
import ru.sarawan.android.databinding.ListItemButtonBinding
import ru.sarawan.android.model.data.CardScreenDataModel

class ButtonHolder(
    private val binding: ListItemButtonBinding,
    private val listener: ButtonMoreClickListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: CardScreenDataModel) {
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