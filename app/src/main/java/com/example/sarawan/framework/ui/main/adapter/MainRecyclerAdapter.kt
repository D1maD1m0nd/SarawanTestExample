package com.example.sarawan.framework.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.model.data.DataModel

class MainRecyclerAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private val data: MutableList<DataModel>,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<MainRecyclerAdapter.RecyclerItemViewHolder>() {

    fun setData(data: List<DataModel>?) {
        data?.let { dataList ->
            dataList.forEach {
                setData(it)
            }
        }
    }

    fun setData(vararg data: DataModel?) {
        data.forEach {
            setData(it)
        }
    }

    fun setData(data: DataModel?) {
        data?.let {
            this.data.add(it)
            notifyItemInserted(this.data.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        return RecyclerItemViewHolder(
            ListItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerItemViewHolder(private val binding: ListItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var disposable: Disposable? = null

        fun bind(data: DataModel) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                binding.itemPrice.text = String.format("%.2f", data.price)
                binding.itemShopName.text = data.shop
                binding.itemWeight.text = data.weight
                binding.itemDescription.text = data.itemDescription

                val discount = data.discount
                if (discount != null) "-${discount}%".also { binding.discount.text = it }
                else binding.discount.visibility = View.GONE

                disposable = imageLoader.enqueue(
                    ImageRequest.Builder(binding.root.context)
                        .data(data.pictureUrl?.toInt())
//                        .placeholder(R.drawable.logo_top_bar)
                        .target(binding.itemImage)
                        .build()
                )

                var quantity = data.quantity ?: 0
                data.quantity = quantity

                if (quantity > 0) {
                    binding.itemBuyButton.visibility = View.GONE
                    binding.itemQuantityLayout.visibility = View.VISIBLE
                    binding.itemQuantity.text = quantity.toString()
                }

                binding.itemBuyButton.setOnClickListener {
                    binding.itemBuyButton.visibility = View.GONE
                    binding.itemQuantityLayout.visibility = View.VISIBLE
                    quantity = 1
                    data.quantity = quantity
                    binding.itemQuantity.text = data.quantity.toString()
                    changeQuantity(data)
                }

                binding.minusButton.setOnClickListener {
                    quantity -= 1
                    data.quantity = quantity
                    if (quantity <= 0) {
                        binding.itemBuyButton.visibility = View.VISIBLE
                        binding.itemQuantityLayout.visibility = View.GONE
                    }
                    binding.itemQuantity.text = quantity.toString()
                    changeQuantity(data)
                }

                binding.plusButton.setOnClickListener {
                    quantity += 1
                    data.quantity = quantity
                    binding.itemQuantity.text = quantity.toString()
                    changeQuantity(data)
                }
            }
        }

        fun cancelTask() = disposable?.dispose()
    }

    private fun changeQuantity(listItemData: DataModel) {
        onListItemClickListener.onItemClick(listItemData)
    }

    interface OnListItemClickListener {
        fun onItemClick(data: DataModel)
    }

    override fun onViewRecycled(holder: RecyclerItemViewHolder) {
        holder.cancelTask()
        super.onViewRecycled(holder)
    }
}