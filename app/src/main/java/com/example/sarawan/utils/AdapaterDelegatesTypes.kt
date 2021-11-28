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
import com.example.sarawan.model.data.ProductsItem
import com.example.sarawan.model.data.delegatesModel.BasketFooter
import com.example.sarawan.model.data.delegatesModel.BasketHeader
import com.example.sarawan.model.data.delegatesModel.BasketListItem
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

object AdapterDelegatesTypes {
    val headerDelegateViewBindingViewHolder = adapterDelegateViewBinding<BasketHeader, BasketListItem, BasketHeaderItemBinding>(
        { layoutInflater, root -> BasketHeaderItemBinding.inflate(layoutInflater, root, false) }
    ){
        bind {
           binding.counterProductTextView.text = String.format("В корзине %d товара", item.counter)
        }
    }

    fun itemDelegateViewBindingViewHolder(itemClickListener : ItemClickListener) = adapterDelegateViewBinding<ProductsItem, BasketListItem, BasketItemBinding>(
        { layoutInflater, root -> BasketItemBinding.inflate(layoutInflater, root, false) }
    ){
        bind {
            binding.apply {
                item.basketProduct?.let {
                    titleProductTextView.text = it.basketProduct?.name
                    propertiesTextView.text = it.basketProduct?.unitQuantity
                    productCompanyTextView.text = "Растишка"
                    productCountryTextView.text = "Россия"
                    productShopTextView.text = it.store
                    sumTextView.text = String.format("%s ₽", it.price)
                    counterTextView.text = item.quantity.toString()
                }
                productImageView.load(R.drawable.product_sample_img)

                var counter = counterTextView.text.toString().toInt()
                plusImageButton.setOnClickListener {
                    if(counter in 0..99) {
                        ++counter
                        item.quantity = counter
                        itemClickListener.update()
                        counterTextView.text = counter.toString()
                    }
                }
                minusImageButton.setOnClickListener {
                    if(counter in  1..100) {
                        --counter
                        item.quantity = counter
                        itemClickListener.update()
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