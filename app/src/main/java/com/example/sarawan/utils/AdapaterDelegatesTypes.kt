package com.example.sarawan.utils

import coil.load
import com.example.sarawan.R
import com.example.sarawan.databinding.BasketFooterItemBinding
import com.example.sarawan.databinding.BasketHeaderItemBinding
import com.example.sarawan.databinding.BasketItemBinding
import com.example.sarawan.framework.ui.profile.address_fragment.ProfileAddressFragment
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
                val image = item.basketProduct?.basketProduct?.images?.first()?.image ?: ""
                productImageView.load(image)

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
                    val basketId = item.basketProductId
                    basketId?.let {
                        itemClickListener.deleteItem(basketId, absoluteAdapterPosition, item)
                    }
                }

                infoContainerConstraintLayout.setOnClickListener {
                    item.basketProduct?.productStoreId?.let{
                        itemClickListener.openProductCard(it)
                    }
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
                diliveryPriceValueTextView.text = String.format("%.2f ₽", item.deliveryPrice)
                resultValuePaymentTextView.text = String.format("%.2f ₽", item.resultPrice)
                weightValueTextView.text = item.weight.toString()
                addressButton.text = item.address
                addressButton.setOnClickListener {
                    itemClickListener.showModal(ProfileAddressFragment.newInstance())
                }
            }
        }
    }
}