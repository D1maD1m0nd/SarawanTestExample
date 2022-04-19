package ru.sarawan.android.framework.ui.product_card.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.sarawan.android.R
import ru.sarawan.android.databinding.ListItemCardBinding
import ru.sarawan.android.model.data.product.Product
import ru.sarawan.android.utils.constants.TypeCardEnum

class SimilarAdapter(val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<SimilarAdapter.ProductViewHolder>() {
    private var similarList: MutableList<Product> = mutableListOf()

    fun setData(products: MutableList<Product>) {
        this.similarList = products
        notifyItemRangeInserted(0, itemCount)
    }

    fun itemUpdate(pos: Int, products: MutableList<Product>) {
        this.similarList = products
        notifyItemChanged(pos)
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

    abstract class ProductViewHolder(
        private val binding: ListItemCardBinding,
        private val itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) = with(binding) {
            val store = product.storePrices?.first()
            discount.visibility = GONE
            itemDescription.text = product.name
            itemShopName.text = store?.store
            itemPrice.text = store?.price.toString()
            itemQuantity.text = product.quantity.toString()
            if (product.quantity > 0) {
                itemBuyButton.visibility = GONE
                itemQuantityLayout.visibility = VISIBLE
            } else {
                itemBuyButton.visibility = VISIBLE
                itemQuantityLayout.visibility = GONE
            }
            itemQuantityLayout.setOnClickListener { }
            itemBuyButton.setOnClickListener {
                product.quantity++
                itemClickListener.create(product, absoluteAdapterPosition, TypeCardEnum.SIMILAR)
            }

            plusButton.setOnClickListener {
                itemClickListener.update(absoluteAdapterPosition, true, TypeCardEnum.SIMILAR)
            }

            minusButton.setOnClickListener {
                itemClickListener.update(absoluteAdapterPosition, false, TypeCardEnum.SIMILAR)
            }

            "${product.unitQuantity.toString()} кг".also { itemWeight.text = it }

            itemCard.setOnClickListener {
                product.id?.let {
                    itemClickListener.openProductCard(productId = it)
                }
            }

            product.images?.let {
                val image = product.images?.first()?.image
                itemImage.load(image) {
                    placeholder(R.drawable.place_holder_image)
                    error(R.drawable.place_holder_image)
                }
            }
        }
    }
}