package ru.sarawan.android.framework.ui.basket.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.sarawan.android.model.data.delegatesModel.BasketListItem
import ru.sarawan.android.utils.AdapterDelegatesTypes
import ru.sarawan.android.utils.ItemClickListener


class BasketAdapter(itemClickListener: ItemClickListener) :
    AsyncListDifferDelegationAdapter<BasketListItem>(BasketDiffUtilItemCallback()) {
    init {
        delegatesManager.addDelegate(AdapterDelegatesTypes.headerDelegateViewBindingViewHolder)
        delegatesManager.addDelegate(
            AdapterDelegatesTypes.itemDelegateViewBindingViewHolder(
                itemClickListener
            )
        )
        delegatesManager.addDelegate(
            AdapterDelegatesTypes.footerDelegateViewBindingViewHolder(
                itemClickListener
            )
        )
    }

    fun updateHeader() {
        notifyItemChanged(FIRST_POSITION)
    }

    fun updateFooter() {
        notifyItemChanged(itemCount - 1)
    }

    fun updateHolders() {
        updateHeader()
        updateFooter()
    }

    companion object {
        private const val FIRST_POSITION = 0
    }
}