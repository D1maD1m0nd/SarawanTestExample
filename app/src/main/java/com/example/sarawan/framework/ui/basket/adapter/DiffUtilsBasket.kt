package com.example.sarawan.framework.ui.basket.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.sarawan.model.data.BasketDataModel

class DiffUtilsBasket(private val oldList: List<BasketDataModel>, private val newList: List<BasketDataModel>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }
}