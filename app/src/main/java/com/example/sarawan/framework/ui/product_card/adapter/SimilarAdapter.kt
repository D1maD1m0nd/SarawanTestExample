package com.example.sarawan.framework.ui.product_card.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.sarawan.R
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.framework.ui.main.viewHolder.CardItemViewHolder
import com.example.sarawan.model.data.Product
import com.example.sarawan.utils.ItemClickListener

class SimilarAdapter(val itemClickListener: ItemClickListener) : RecyclerView.Adapter<SimilarAdapter.ProductViewHolder>() {
    private var similarList : MutableList<Product> = ArrayList(20)

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
            if(!product.visible) {
                itemBuyButton.visibility = GONE
                itemQuantityLayout.visibility = VISIBLE
                itemQuantity.text = "1"
            } else {
                itemBuyButton.visibility = VISIBLE
                itemQuantityLayout.visibility = GONE
            }
            itemBuyButton.setOnClickListener {
                itemClickListener.changeVisible(absoluteAdapterPosition)
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