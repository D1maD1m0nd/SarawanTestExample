package com.example.sarawan.utils

import coil.load
import com.example.sarawan.R
import com.example.sarawan.databinding.BasketItemBinding
import com.example.sarawan.databinding.FooterItemBinding
import com.example.sarawan.databinding.HeaderItemBinding
import com.example.sarawan.model.data.DataModel
import com.example.sarawan.model.data.DelegatesModel.BasketFooter
import com.example.sarawan.model.data.DelegatesModel.BasketHeader
import com.example.sarawan.model.data.DelegatesModel.BasketListItem
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

object AdapterDelegatesTypes {
    val headerDelegateViewBindingViewHolder = adapterDelegateViewBinding<BasketHeader, BasketListItem, HeaderItemBinding>(
        { layoutInflater, root -> HeaderItemBinding.inflate(layoutInflater, root, false) }
    ){
        bind {
           //todo
        }
    }

    val itemDelegateViewBindingViewHolder = adapterDelegateViewBinding<DataModel, BasketListItem, BasketItemBinding>(
        { layoutInflater, root -> BasketItemBinding.inflate(layoutInflater, root, false) }
    ){
        bind {
            binding.apply {
                titleProductTextView.text = item.itemDescription
                propertiesTextView.text = item.weight
                productCompanyTextView.text = item.company
                productCountryTextView.text = item.country
                productShopTextView.text = item.shop
                sumTextView.text = item.price.toString()
                productImageView.load(R.drawable.product_sample_img)
            }

        }
    }

    val footerDelegateViewBindingViewHolder = adapterDelegateViewBinding<BasketFooter, BasketListItem, FooterItemBinding>(
        { layoutInflater, root -> FooterItemBinding.inflate(layoutInflater, root, false) }
    ){
        bind {
            //todo
        }
    }
}