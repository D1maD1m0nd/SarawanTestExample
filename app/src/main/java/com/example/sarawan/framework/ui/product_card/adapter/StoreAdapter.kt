package com.example.sarawan.framework.ui.product_card.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.R
import com.example.sarawan.databinding.StoreItemBinding
import com.example.sarawan.model.data.Product
import com.example.sarawan.model.data.StorePrice
import com.example.sarawan.utils.TypeCardEnum


class StoreAdapter(val itemClickListener: ItemClickListener)  : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>(){
    private var stores : List<StorePrice> = ArrayList(10)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(store : List<StorePrice>) {
        stores = store
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StoreViewHolder(LayoutInflater
                        .from(parent.context)
                        .inflate(
                        R.layout.store_item,
                        parent,
                        false
                    ))


    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) = holder.bind(stores[position])
    override fun getItemCount(): Int = stores.size

    inner class StoreViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = StoreItemBinding.bind(item)
        fun bind(store : StorePrice) = with(binding) {
            storeTitleTextView.text = store.store
            priceTextView.text = String.format("%s ₽", store.price)
            counterTextView.text = store.count.toString()
            if(store.count > 0) {
                basketButton.visibility = INVISIBLE
                counterContainer.visibility = VISIBLE
            }
            basketButton.setOnClickListener {
                itemClickListener.create(
                    Product(count = 1, storePrices = mutableListOf(store)),
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
                itemClickListener.update(
                    absoluteAdapterPosition,
                    mode = false,
                    TypeCardEnum.STORE
                )
            }
        }
    }
}