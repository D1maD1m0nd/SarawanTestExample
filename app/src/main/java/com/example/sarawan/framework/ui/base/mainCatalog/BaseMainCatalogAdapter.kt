package com.example.sarawan.framework.ui.base.mainCatalog

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.model.data.MainScreenDataModel

abstract class BaseMainCatalogAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val displayData: MutableList<MainScreenDataModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        displayData.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = displayData.size

    override fun getItemViewType(position: Int): Int {
        return displayData[position].cardType ?: -1
    }
}

enum class CardType(val type: Int) {
    TOP(0),
    COMMON(1),
    STRING(2),
    BUTTON(3),
    EMPTY(4),
    PARTNERS(5)
}