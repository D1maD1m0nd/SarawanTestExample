package com.example.sarawan.utils

import coil.load
import com.example.sarawan.R
import com.example.sarawan.databinding.BasketFooterItemBinding
import com.example.sarawan.databinding.BasketHeaderItemBinding
import com.example.sarawan.databinding.BasketItemBinding
import com.example.sarawan.framework.ui.basket.ItemClickListener
import com.example.sarawan.framework.ui.basket.modals.DeliveryTimeFragment
import com.example.sarawan.framework.ui.basket.modals.PaymentMethodFragment
import com.example.sarawan.framework.ui.profile.ProfileAddressFragment
import com.example.sarawan.model.data.DataModel
import com.example.sarawan.model.data.DelegatesModel.BasketFooter
import com.example.sarawan.model.data.DelegatesModel.BasketHeader
import com.example.sarawan.model.data.DelegatesModel.BasketListItem
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

object AdapterDelegatesTypes {
    val headerDelegateViewBindingViewHolder = adapterDelegateViewBinding<BasketHeader, BasketListItem, BasketHeaderItemBinding>(
        { layoutInflater, root -> BasketHeaderItemBinding.inflate(layoutInflater, root, false) }
    ){
        bind {
           binding.counterProductTextView.text = String.format("В корзине %d товара", item.counter)
        }
    }

    fun itemDelegateViewBindingViewHolder(itemClickListener : ItemClickListener) = adapterDelegateViewBinding<DataModel, BasketListItem, BasketItemBinding>(
        { layoutInflater, root -> BasketItemBinding.inflate(layoutInflater, root, false) }
    ){
        bind {
            binding.apply {
                var counter = counterTextView.text.toString().toInt()
                val range = 1..99
                titleProductTextView.text = item.itemDescription
                propertiesTextView.text = item.weight.toString()
                productCompanyTextView.text = item.company
                productCountryTextView.text = item.country
                productShopTextView.text = item.shop
                sumTextView.text = String.format("%.2f ₽", item.price)
                productImageView.load(R.drawable.product_sample_img)
                plusImageButton.setOnClickListener {
                    ++counter
                    if(counter in range) {
                        counterTextView.text = counter.toString()
                    }
                }
                minusImageButton.setOnClickListener {
                    --counter
                    if(counter in range) {
                        counterTextView.text = counter.toString()
                    }
                }

                trashImageButton.setOnClickListener {
                    itemClickListener.deleteItem(item, absoluteAdapterPosition)
                }
            }
        }
    }


    fun footerDelegateViewBindingViewHolder(itemClickListener : ItemClickListener) = adapterDelegateViewBinding<BasketFooter, BasketListItem, BasketFooterItemBinding>(
        { layoutInflater, root -> BasketFooterItemBinding.inflate(layoutInflater, root, false) }
    ){
        bind {
            binding.apply {
                costValueTextView.text = String.format("%.2f ₽", item.price)
                resultValuePaymentTextView.text = String.format("%.2f ₽", item.price)
                weightValueTextView.text = item.weight.toString()
                timeDeliveryButton.setOnClickListener{
                    itemClickListener.showModal(DeliveryTimeFragment.newInstance())
                }
                addressButton.setOnClickListener {
                    itemClickListener.showModal(ProfileAddressFragment.newInstance())
                }
                paymentEventButton.setOnClickListener {
                    itemClickListener.showModal(PaymentMethodFragment.newInstance());
                }
            }
        }
    }
}