package com.example.sarawan.framework.ui.basket.adapter

import com.example.sarawan.framework.ui.basket.ItemClickListener
import com.example.sarawan.model.data.DelegatesModel.BasketListItem
import com.example.sarawan.utils.AdapterDelegatesTypes
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class BasketAdapter(itemClickListener: ItemClickListener) : AsyncListDifferDelegationAdapter<BasketListItem>(BasketDiffUtilItemCallback()) {
    init {
        delegatesManager.addDelegate(AdapterDelegatesTypes.headerDelegateViewBindingViewHolder)
        delegatesManager.addDelegate(AdapterDelegatesTypes.itemDelegateViewBindingViewHolder(itemClickListener))
        delegatesManager.addDelegate(AdapterDelegatesTypes.footerDelegateViewBindingViewHolder(itemClickListener))
    }
}