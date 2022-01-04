package ru.sarawan.android.framework.ui.main.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import ru.sarawan.android.R
import ru.sarawan.android.databinding.ListItemCardBinding
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogAdapter
import ru.sarawan.android.framework.ui.main.adapter.MainRecyclerAdapter
import ru.sarawan.android.model.data.MainScreenDataModel

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
                "${data.unitQuantity.toString()} ${root.resources.getString(R.string.kg)}"
                    .also { itemWeight.text = it }
                itemDescription.text = data.itemDescription
                itemMinPriceText.text = data.sortText

                itemBuyButton.setOnClickListener {
                    listener.onItemPriceChangeClick(data, 1, true) { isOnline ->
                        if (isOnline) {
                            itemBuyButtonFrame.visibility = View.GONE
                            itemQuantityLayout.visibility = View.VISIBLE
                            quantity = 1
                            data.quantity = quantity
                            itemQuantity.text = data.quantity.toString()
                        }
                    }
                }

                itemQuantityLayout.setOnClickListener { }

                minusButton.setOnClickListener {
                    listener.onItemPriceChangeClick(data, -1, false) { isOnline ->
                        if (isOnline) {
                            quantity -= 1
                            data.quantity = quantity
                            if (quantity <= 0) {
                                itemBuyButtonFrame.visibility = View.VISIBLE
                                itemQuantityLayout.visibility = View.GONE
                            }
                            itemQuantity.text = quantity.toString()
                        }
                    }
                }

                plusButton.setOnClickListener {
                    listener.onItemPriceChangeClick(data, 1, false) { isOnline ->
                        if (isOnline) {
                            quantity += 1
                            data.quantity = quantity
                            itemQuantity.text = quantity.toString()
                        }
                    }
                }

                val discountNumber = data.discount
                if (discountNumber != null && discountNumber > 0) "-$discountNumber%".also {
                    discount.visibility = View.VISIBLE
                    discount.text = it
                }
                else discount.visibility = View.GONE

                disposable = imageLoader.enqueue(
                    ImageRequest.Builder(root.context)
                        .data(data.pictureUrl)
                        .placeholder(R.drawable.card_placeholder)
                        .target(itemImage)
                        .error(R.drawable.card_placeholder)
                        .build()
                )
            }
        }
    }

    override fun cancelTask() {
        disposable?.dispose()
    }
}