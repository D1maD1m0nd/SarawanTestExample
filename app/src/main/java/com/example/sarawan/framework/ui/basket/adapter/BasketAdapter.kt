package com.example.sarawan.framework.ui.basket.adapter

import com.example.sarawan.framework.ui.basket.ItemClickListener
import com.example.sarawan.model.data.DelegatesModel.BasketFooter
import com.example.sarawan.model.data.DelegatesModel.BasketHeader
import com.example.sarawan.model.data.DelegatesModel.BasketListItem
import com.example.sarawan.model.data.ProductsItem
import com.example.sarawan.utils.AdapterDelegatesTypes
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter


class BasketAdapter(itemClickListener: ItemClickListener) : AsyncListDifferDelegationAdapter<BasketListItem>(BasketDiffUtilItemCallback()) {
    init {
        delegatesManager.addDelegate(AdapterDelegatesTypes.headerDelegateViewBindingViewHolder)
        delegatesManager.addDelegate(AdapterDelegatesTypes.itemDelegateViewBindingViewHolder(itemClickListener))
        delegatesManager.addDelegate(AdapterDelegatesTypes.footerDelegateViewBindingViewHolder(itemClickListener))
    }

    fun updateHeader() {
        val header = items.first() as BasketHeader
        header.counter = items.count {
            it is ProductsItem
        }
        notifyItemChanged(FIRST_POSITION)
    }

    fun updateFooter() {
        val footer = items.last() as BasketFooter
        val dataModelList = filterDataModel()
        footer.price = calculateSum(dataModelList)
        footer.weight = calculateWeight(dataModelList)
        notifyItemChanged(itemCount - 1)
    }
    private fun filterDataModel() : List<ProductsItem> = items.filterIsInstance<ProductsItem>()
    companion object {
        private const val FIRST_POSITION = 0
        fun calculateSum(data : List<ProductsItem>) : Double = data.sumOf {
                it.product?.price!!.toDouble()
            }

        fun calculateWeight(data : List<ProductsItem>) = 0.0
    }
}