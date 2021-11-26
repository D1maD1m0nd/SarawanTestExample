package com.example.sarawan.framework.ui.basket.adapter

import com.example.sarawan.framework.ui.basket.ItemClickListener
import com.example.sarawan.model.data.ProductsItem
import com.example.sarawan.model.data.delegatesModel.BasketFooter
import com.example.sarawan.model.data.delegatesModel.BasketHeader
import com.example.sarawan.model.data.delegatesModel.BasketListItem
import com.example.sarawan.utils.AdapterDelegatesTypes
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter


class BasketAdapter(itemClickListener: ItemClickListener) : AsyncListDifferDelegationAdapter<BasketListItem>(BasketDiffUtilItemCallback()) {
    init {
        delegatesManager.addDelegate(AdapterDelegatesTypes.headerDelegateViewBindingViewHolder)
        delegatesManager.addDelegate(AdapterDelegatesTypes.itemDelegateViewBindingViewHolder(itemClickListener))
        delegatesManager.addDelegate(AdapterDelegatesTypes.footerDelegateViewBindingViewHolder(itemClickListener))
    }

    private fun updateHeader() {
        val header = items.first() as BasketHeader
        header.counter = items.count {
            it is ProductsItem
        }
        notifyItemChanged(FIRST_POSITION)
    }

    private fun updateFooter() {
        val footer = items.last() as BasketFooter
        val dataModelList = filterDataModel()
        footer.price = calculateSum(dataModelList)
        footer.weight = calculateWeight(dataModelList)
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