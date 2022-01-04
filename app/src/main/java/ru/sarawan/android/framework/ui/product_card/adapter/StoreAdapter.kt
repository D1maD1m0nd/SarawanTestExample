package ru.sarawan.android.framework.ui.product_card.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.sarawan.android.R
import ru.sarawan.android.databinding.StoreItemBinding
import ru.sarawan.android.model.data.Product
import ru.sarawan.android.model.data.StorePrice
import ru.sarawan.android.utils.TypeCardEnum


class StoreAdapter(val itemClickListener: ItemClickListener)  : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>(){
    private var stores : List<StorePrice> = ArrayList(10)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(store : List<StorePrice>) {
        stores = store
        notifyDataSetChanged()
    }

    fun itemUpdate(pos : Int,stores : List<StorePrice>) {
        this.stores = stores
        //это необходимо что бы перебиндить состояние кнопок корзины
        notifyItemRangeChanged(pos, itemCount)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StoreViewHolder(LayoutInflater
                        .from(parent.context)
                        .inflate(
                        R.layout.store_item,
                        parent,
                        false
                    ))


    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val isNotEmptyCount = stores.count { it.count > 0 } > 0
        holder.bind(stores[position], isNotEmptyCount)
    }
    override fun getItemCount(): Int = stores.size

    inner class StoreViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = StoreItemBinding.bind(item)
        fun bind(store : StorePrice, isNotEmptyCount : Boolean) = with(binding) {
            storeTitleTextView.text = store.store
            priceTextView.text = String.format("%s ₽", store.price)
            counterTextView.text = store.count.toString()
            if(isNotEmptyCount) {
              basketButton.isEnabled = false
              basketButton.isClickable = false
            } else {
                basketButton.isEnabled = true
                basketButton.isClickable = true
            }
            if(store.count > 0) {
                basketButton.visibility = INVISIBLE
                counterContainer.visibility = VISIBLE
            } else {
                basketButton.visibility = VISIBLE
                counterContainer.visibility = INVISIBLE
            }
            basketButton.setOnClickListener {
                    itemClickListener.create(
                        Product(quantity = 1, storePrices = mutableListOf(store)),
                        absoluteAdapterPosition,
                        TypeCardEnum.STORE
                    )
            }

            plusButtonImageButton.setOnClickListener {
                itemClickListener.update(
                    absoluteAdapterPosition,
                    mode = true,
                    TypeCardEnum.STORE
                )
            }

            minusImageButton.setOnClickListener {
                if(store.count >= 1) {
                    itemClickListener.update(
                        absoluteAdapterPosition,
                        mode = false,
                        TypeCardEnum.STORE
                    )
                }
            }
        }
    }
}