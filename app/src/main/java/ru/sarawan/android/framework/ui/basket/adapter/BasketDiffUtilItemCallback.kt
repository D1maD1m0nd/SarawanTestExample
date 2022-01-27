package ru.sarawan.android.framework.ui.basket.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.sarawan.android.model.data.delegatesModel.BasketListItem

open class BasketDiffUtilItemCallback : DiffUtil.ItemCallback<BasketListItem>() {
    override fun areItemsTheSame(
        oldItem: BasketListItem,
        newItem: BasketListItem
    ): Boolean = oldItem.id == newItem.id


    override fun areContentsTheSame(
        oldItem: BasketListItem,
        newItem: BasketListItem
    ): Boolean = newItem == oldItem
}