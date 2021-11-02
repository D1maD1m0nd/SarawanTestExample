package com.example.sarawan.framework.ui.basket.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.request.Disposable
import coil.request.ImageRequest
import com.example.sarawan.R
import com.example.sarawan.databinding.BasketItemBinding
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.framework.ui.main.MainRecyclerAdapter
import com.example.sarawan.model.data.BasketDataModel
import com.example.sarawan.model.data.DataModel

class BasketAdapter(private val data : List<BasketDataModel> = ArrayList(DEFAULT_CAPACITY)) : RecyclerView.Adapter<BasketAdapter.RecyclerItemViewHolder>() {

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

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = data.size


    inner class RecyclerItemViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        private val binding = BasketItemBinding.bind(view)
        fun bind(data: DataModel) {

        }
    }
    companion object{
        private val TAG = BasketAdapter::class.java.simpleName
        private const val DEFAULT_CAPACITY = 100
    }
}