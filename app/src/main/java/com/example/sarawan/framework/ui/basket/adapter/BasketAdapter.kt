package com.example.sarawan.framework.ui.basket.adapter

import com.example.sarawan.framework.ui.basket.ItemClickListener
import com.example.sarawan.model.data.DataModel
import com.example.sarawan.model.data.DelegatesModel.BasketFooter
import com.example.sarawan.model.data.DelegatesModel.BasketHeader
import com.example.sarawan.model.data.DelegatesModel.BasketListItem
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
            it is DataModel
        }
        notifyItemChanged(0)
    }

    fun updateFooter() {
        val footer = items.last() as BasketFooter
        val dataModelList = filterDataModel()
        footer.price = calculateSum(dataModelList)
        footer.weight = calculateWeight(dataModelList)
        notifyItemChanged(itemCount - 1)
    }
    private fun filterDataModel() : List<DataModel> = items.filterIsInstance<DataModel>()
    companion object {
        fun calculateSum(data : List<DataModel>) =
            data.map {
                it
            }.sumOf {
                it.price!!.toDouble()
            }
        fun calculateWeight(data : List<DataModel>) =
            data.map {
                it
            }.sumOf {
                it.weight!!.toDouble()
            }
    }
}