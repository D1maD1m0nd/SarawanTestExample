package com.example.sarawan.framework.ui.product_card.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
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