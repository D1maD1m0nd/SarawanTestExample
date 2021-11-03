package com.example.sarawan.framework.ui.basket.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.sarawan.R
import com.example.sarawan.databinding.BasketItemBinding
import com.example.sarawan.model.data.BasketDataModel

class BasketAdapter(val data: MutableList<BasketDataModel> = ArrayList(DEFAULT_CAPACITY)) :
    RecyclerView.Adapter<BasketAdapter.RecyclerItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.basket_item,
            parent,
            false
        )
        return RecyclerItemViewHolder(
            view
        )
    }

    fun setData(data: List<BasketDataModel>) {
        this.data.addAll(data)
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size


    inner class RecyclerItemViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        private val binding = BasketItemBinding.bind(view)
        fun bind(data: BasketDataModel) {
            fillCard(data)
        }

        private fun fillCard(data: BasketDataModel) = with(binding) {
            titleProductTextView.text = data.name
            propertiesTextView.text = data.weight
            productCompanyTextView.text = data.company
            productCountryTextView.text = data.country
            productShopTextView.text = data.shop
            sumTextView.text = data.price.toString()
            productImageView.load(R.drawable.product_sample_img)
        }
    }

    companion object {
        private val TAG = BasketAdapter::class.java.simpleName
        private const val DEFAULT_CAPACITY = 100
    }
}