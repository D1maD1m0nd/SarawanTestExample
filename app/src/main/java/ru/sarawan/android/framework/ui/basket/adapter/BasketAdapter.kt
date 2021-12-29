package ru.sarawan.android.framework.ui.basket.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.sarawan.android.model.data.ProductsItem
import ru.sarawan.android.model.data.delegatesModel.BasketListItem
import ru.sarawan.android.utils.AdapterDelegatesTypes
import ru.sarawan.android.utils.ItemClickListener


class BasketAdapter(itemClickListener: ItemClickListener) : AsyncListDifferDelegationAdapter<BasketListItem>(BasketDiffUtilItemCallback()) {
    init {
        delegatesManager.addDelegate(AdapterDelegatesTypes.headerDelegateViewBindingViewHolder)
        delegatesManager.addDelegate(AdapterDelegatesTypes.itemDelegateViewBindingViewHolder(itemClickListener))
        delegatesManager.addDelegate(AdapterDelegatesTypes.footerDelegateViewBindingViewHolder(itemClickListener))
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
    private fun filterDataModel() : List<ProductsItem> = items.filterIsInstance<ProductsItem>()
    companion object {
        private const val FIRST_POSITION = 0
        fun calculateSum(data : List<ProductsItem>) : Double = data.sumOf {
                it.basketProduct?.price!!.toDouble() * it.quantity!!
            }

        fun calculateWeight(data : List<ProductsItem>) = 0.0
    }
}