package com.example.sarawan.framework.ui.product_card.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.sarawan.R
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.model.data.Product
import com.example.sarawan.utils.TypeCardEnum

class SimilarAdapter(val itemClickListener: ItemClickListener) : RecyclerView.Adapter<SimilarAdapter.ProductViewHolder>() {
    private var similarList : List<Product> = ArrayList(20)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(products : MutableList<Product>) {
        similarList = products
    }

    fun updateItem(products : MutableList<Product>, position: Int) {
        similarList = products
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = ListItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).apply {
            root.layoutParams.apply {
                width = parent.width / 2
            }
        }
        return object : ProductViewHolder(
            view,
            itemClickListener
        ) {}
    }


    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(similarList[position])
    }

    override fun getItemCount(): Int = similarList.size
    override fun getItemId(position: Int): Long {
        return similarList[position].id!!
    }
    abstract class ProductViewHolder(
        private val binding: ListItemCardBinding,
        private val itemClickListener: ItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product : Product) = with(binding) {
            val store = product.storePrices?.first()
            discount.visibility = GONE
            itemDescription.text = product.name
            itemShopName.text = store?.store
            itemPrice.text = store?.price
            itemQuantity.text = product.count.toString()
            if(product.count > 0) {
                itemBuyButtonFrame.visibility = GONE
                itemQuantityLayout.visibility = VISIBLE
            } else {
                itemBuyButtonFrame.visibility = VISIBLE
                itemQuantityLayout.visibility = GONE
            }
            itemQuantityLayout.setOnClickListener { }
            itemBuyButton.setOnClickListener {
                //itemClickListener.changeVisible(absoluteAdapterPosition)
                product.count++
                itemClickListener.create(product, absoluteAdapterPosition, TypeCardEnum.SIMILAR)
            }

            plusButton.setOnClickListener {
                itemClickListener.update(absoluteAdapterPosition, true, TypeCardEnum.SIMILAR)
            }

            minusButton.setOnClickListener {
                itemClickListener.update(absoluteAdapterPosition, false, TypeCardEnum.SIMILAR)
            }

            "${product.unitQuantity.toString()} Кг".also { itemWeight.text = it }

            itemCard.setOnClickListener {
                product.id?.let {
                    itemClickListener.openProductCard(productId = product.id)
                }
            }

            product.images?.let {
                if(it.isNotEmpty()) {
                    val image = product.images.first().image
                    itemImage.load(image) {
                        placeholder(R.drawable.card_placeholder)
                        error(R.drawable.card_placeholder)
                    }
                }
            }
        }
    }
}