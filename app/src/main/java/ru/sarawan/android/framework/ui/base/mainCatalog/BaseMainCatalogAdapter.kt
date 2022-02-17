package ru.sarawan.android.framework.ui.base.mainCatalog

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import ru.sarawan.android.model.data.CardScreenDataModel

abstract class BaseMainCatalogAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val displayData: MutableList<CardScreenDataModel> = mutableListOf()

    override fun getItemCount(): Int = displayData.size

    override fun getItemViewType(position: Int): Int {
        return displayData[position].cardType ?: -1
    }

    @SuppressLint("NotifyDataSetChanged")
    open fun clear() {
        displayData.clear()
        notifyDataSetChanged()
    }

    interface OnListItemClickListener {
        fun onItemPriceChangeClick(
            data: CardScreenDataModel,
            diff: Int,
            isNewItem: Boolean,
            callback: (isOnline: Boolean) -> Unit
        )

        fun onItemClick(data: CardScreenDataModel)
    }
}

enum class CardType(val type: Int) {
    TOP(0),
    COMMON(1),
    STRING(2),
    BUTTON(3),
    LOADING(4)
}