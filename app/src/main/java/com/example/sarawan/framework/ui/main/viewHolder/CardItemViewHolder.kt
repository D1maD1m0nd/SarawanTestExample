package com.example.sarawan.framework.ui.main.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogAdapter
import com.example.sarawan.framework.ui.main.adapter.MainRecyclerAdapter
import com.example.sarawan.model.data.MainScreenDataModel

abstract class CardItemViewHolder(
    private val binding: ListItemCardBinding,
    private val imageLoader: ImageLoader,
    private val listener: BaseMainCatalogAdapter.OnListItemClickListener
) : RecyclerView.ViewHolder(binding.root), MainRecyclerAdapter.CancellableHolder {

    private var disposable: Disposable? = null

    open fun bind(data: MainScreenDataModel) {
        if (layoutPosition != RecyclerView.NO_POSITION) {
            var quantity = data.quantity ?: 0
            data.quantity = quantity

            with(binding) {

                root.setOnClickListener {
                    listener.onItemClick(data)
                }

                if (quantity > 0) {
                    itemBuyButton.visibility = View.GONE
                    itemQuantityLayout.visibility = View.VISIBLE
                    itemQuantity.text = quantity.toString()
                } else {
                    itemBuyButton.visibility = View.VISIBLE
                    itemQuantityLayout.visibility = View.GONE
                }

                itemPrice.text = String.format("%.2f", (data.price))
                itemShopName.text = data.shop
                "${data.weight.toString()}г".also { itemWeight.text = it }
                itemDescription.text = data.itemDescription

                itemBuyButton.setOnClickListener {
                    itemBuyButton.visibility = View.GONE
                    itemQuantityLayout.visibility = View.VISIBLE
                    quantity = 1
                    data.quantity = quantity
                    itemQuantity.text = data.quantity.toString()
                    listener.onItemPriceChangeClick(data, 1, true)
                }

                minusButton.setOnClickListener {
                    quantity -= 1
                    data.quantity = quantity
                    if (quantity <= 0) {
                        itemBuyButton.visibility = View.VISIBLE
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
            if (discount != null) "-${discount}%".also { binding.discount.text = it }
            else binding.discount.visibility = View.GONE

            disposable = imageLoader.enqueue(
                ImageRequest.Builder(binding.root.context)
                    .data(data.pictureUrl)
//                        .placeholder(R.drawable.logo_top_bar)
                    .target(binding.itemImage)
                    .build()
            )
        }
    }

    override fun cancelTask() {
        disposable?.dispose()
    }
}