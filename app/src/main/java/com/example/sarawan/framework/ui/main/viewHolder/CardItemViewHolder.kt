package com.example.sarawan.framework.ui.main.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.example.sarawan.R
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogAdapter
import com.example.sarawan.framework.ui.main.adapter.MainRecyclerAdapter
import com.example.sarawan.model.data.MainScreenDataModel

class CardItemViewHolder(
    private val binding: ListItemCardBinding,
    private val imageLoader: ImageLoader,
    private val listener: BaseMainCatalogAdapter.OnListItemClickListener
) : RecyclerView.ViewHolder(binding.root), MainRecyclerAdapter.CancellableHolder {

    private var disposable: Disposable? = null

    fun bind(data: MainScreenDataModel) {
        if (layoutPosition != RecyclerView.NO_POSITION) {
            var quantity = data.quantity ?: 0
            data.quantity = quantity

            with(binding) {

                root.setOnClickListener {
                    listener.onItemClick(data)
                }

                if (quantity > 0) {
                    itemBuyButtonFrame.visibility = View.GONE
                    itemQuantityLayout.visibility = View.VISIBLE
                    itemQuantity.text = quantity.toString()
                } else {
                    itemBuyButtonFrame.visibility = View.VISIBLE
                    itemQuantityLayout.visibility = View.GONE
                }

                itemPrice.text = String.format("%.2f", (data.price))
                itemShopName.text = data.shop
                "${data.unitQuantity.toString()} Кг".also { itemWeight.text = it }
                itemDescription.text = data.itemDescription
                itemMinPriceText.text = data.sortText

                itemBuyButton.setOnClickListener {
                    itemBuyButtonFrame.visibility = View.GONE
                    itemQuantityLayout.visibility = View.VISIBLE
                    quantity = 1
                    data.quantity = quantity
                    itemQuantity.text = data.quantity.toString()
                    listener.onItemPriceChangeClick(data, 1, true)
                }

                itemQuantityLayout.setOnClickListener { }

                minusButton.setOnClickListener {
                    quantity -= 1
                    data.quantity = quantity
                    if (quantity <= 0) {
                        itemBuyButtonFrame.visibility = View.VISIBLE
                        itemQuantityLayout.visibility = View.GONE
                    }
                    itemQuantity.text = quantity.toString()
                    listener.onItemPriceChangeClick(data, -1, false)
                }

                plusButton.setOnClickListener {
                    quantity += 1
                    data.quantity = quantity
                    itemQuantity.text = quantity.toString()
                    listener.onItemPriceChangeClick(data, 1, false)
                }
            }

            val discount = data.discount
            if (discount != null && discount > 0) "-${discount}%".also {
                binding.discount.visibility = View.VISIBLE
                binding.discount.text = it
            }
            else binding.discount.visibility = View.GONE

            disposable = imageLoader.enqueue(
                ImageRequest.Builder(binding.root.context)
                    .data(data.pictureUrl)
                    .placeholder(R.drawable.card_placeholder)
                    .target(binding.itemImage)
                    .error(R.drawable.card_placeholder)
                    .build()
            )
        }
    }

    override fun cancelTask() {
        disposable?.dispose()
    }
}