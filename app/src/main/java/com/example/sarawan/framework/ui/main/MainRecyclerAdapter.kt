package com.example.sarawan.framework.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.model.data.DataModel
import android.view.ViewTreeObserver.OnGlobalLayoutListener

import android.view.ViewTreeObserver




class MainRecyclerAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private var data: List<DataModel>,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<MainRecyclerAdapter.RecyclerItemViewHolder>() {

    fun setData(data: List<DataModel>?) {
        data?.let {
            this.data = it
            notifyDataSetChanged()
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
                binding.itemPrice.text =
                    data.price.toString().substring(0, data.price.toString().lastIndexOf(".") + 3)
                binding.itemShopName.text = data.shop
                binding.itemWeight.text = data.weight
                binding.itemDescription.text = data.itemDescription

                val discount = data.discount
                if (discount != null) "-${discount}%".also { binding.discount.text = it }
                else binding.discount.visibility = View.GONE

                disposable = imageLoader.enqueue(
                    ImageRequest.Builder(binding.root.context)
                        .data(data.pictureUrl?.toInt())
                        .target(binding.itemImage)
                        .build()
                )

                itemView.setOnClickListener { openInNewWindow(data) }
            }
        }

        fun cancelTask() = disposable?.dispose()
    }

    private fun openInNewWindow(listItemData: DataModel) {
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