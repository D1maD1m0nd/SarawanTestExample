package com.example.sarawan.framework.ui.product_card.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.R
import com.example.sarawan.databinding.StoreItemBinding
import com.example.sarawan.model.data.StorePrice


class StoreAdapter()  : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>(){
    private val stores : MutableList<StorePrice> = ArrayList(10);

    @SuppressLint("NotifyDataSetChanged")
    fun setData(store : List<StorePrice>) {
        stores.clear()
        stores.addAll(store)
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
            basketButton.setOnClickListener {
                it.visibility = GONE
                counterContainer.visibility = VISIBLE
            }

        }
    }
}