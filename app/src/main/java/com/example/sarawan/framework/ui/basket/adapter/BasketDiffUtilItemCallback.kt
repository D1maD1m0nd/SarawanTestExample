package com.example.sarawan.framework.ui.basket.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.sarawan.model.data.DelegatesModel.BasketListItem

open class BasketDiffUtilItemCallback : DiffUtil.ItemCallback<BasketListItem>() {
    override fun areItemsTheSame(
        oldItem: BasketListItem,
        newItem: BasketListItem
    ): Boolean = oldItem.id == newItem.id


    override fun areContentsTheSame(
        oldItem: BasketListItem,
        newItem: BasketListItem
    ): Boolean = oldItem == newItem
}