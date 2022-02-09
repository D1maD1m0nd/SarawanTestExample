package ru.sarawan.android.utils

import coil.load
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.sarawan.android.R
import ru.sarawan.android.databinding.BasketFooterItemBinding
import ru.sarawan.android.databinding.BasketHeaderItemBinding
import ru.sarawan.android.databinding.BasketItemBinding
import ru.sarawan.android.model.data.ProductsItem
import ru.sarawan.android.model.data.delegatesModel.BasketFooter
import ru.sarawan.android.model.data.delegatesModel.BasketHeader
import ru.sarawan.android.model.data.delegatesModel.BasketListItem

object AdapterDelegatesTypes {
    val headerDelegateViewBindingViewHolder =
        adapterDelegateViewBinding<BasketHeader, BasketListItem, BasketHeaderItemBinding>(
            { layoutInflater, root -> BasketHeaderItemBinding.inflate(layoutInflater, root, false) }
        ) {
            bind {
                binding.counterProductTextView.text =
                    String.format("В корзине %d товара", item.counter)
            }
        }

    fun itemDelegateViewBindingViewHolder(itemClickListener: ItemClickListener) =
        adapterDelegateViewBinding<ProductsItem, BasketListItem, BasketItemBinding>(
            { layoutInflater, root -> BasketItemBinding.inflate(layoutInflater, root, false) }
        ) {
            bind {
                binding.apply {
                    item.basketProduct?.let {
                        titleProductTextView.text = it.basketProduct?.name
                        propertiesTextView.text = it.basketProduct?.unitQuantity
                        productCompanyTextView.text =
                            it.basketProduct?.information?.brand ?: "Сарафан"
                        productCountryTextView.text =
                            it.basketProduct?.information?.country ?: "Россия"
                        productShopTextView.text = it.store
                        sumTextView.text = String.format("%.2f ₽", it.price?.toDouble())
                        counterTextView.text = item.quantity.toString()
                    }
                    val image = item.basketProduct?.basketProduct?.images?.first()?.image ?: ""

                    productImageView.load(image) {
                        error(R.drawable.place_holder_image)
                        placeholder(R.drawable.place_holder_image)
                    }

                    var counter = counterTextView.text.toString().toInt()
                    plusImageButton.setOnClickListener {
                        ++counter
                        item.quantity = counter
                        itemClickListener.update(absoluteAdapterPosition)
                        counterTextView.text = counter.toString()
                    }
                    minusImageButton.setOnClickListener {
                        if (counter > 0) {
                            --counter
                            item.quantity = counter
                            if (counter == 0) {
                                item.let { productsItem ->
                                    itemClickListener.deleteItem(
                                        productsItem,
                                        absoluteAdapterPosition,
                                        item
                                    )
                                }
                            } else {
                                itemClickListener.update()
                                counterTextView.text = counter.toString()
                            }
                        }
                    }

                    trashImageButton.setOnClickListener {
                        item.let { productsItem ->
                            itemClickListener.deleteItem(
                                productsItem,
                                absoluteAdapterPosition,
                                item
                            )
                        }
                    }

                    infoContainerConstraintLayout.setOnClickListener {
                        item.basketProduct?.basketProduct?.id?.let {
                            itemClickListener.openProductCard(it.toInt())
                        }
                    }
                }
            }
        }


    fun footerDelegateViewBindingViewHolder(itemClickListener: ItemClickListener) =
        adapterDelegateViewBinding<BasketFooter, BasketListItem, BasketFooterItemBinding>(
            { layoutInflater, root -> BasketFooterItemBinding.inflate(layoutInflater, root, false) }
        ) {
            bind {
                binding.apply {
                    weightValueTextView.text = String.format("%.2f кг", item.weight)
                    costValueTextView.text = String.format("%.2f ₽", item.price)
                    clearButton.setOnClickListener {
                        itemClickListener.clear()
                    }
                    navigateOrderButton.setOnClickListener {
                        itemClickListener.openOrderCard()
                    }
                }
            }
        }
}