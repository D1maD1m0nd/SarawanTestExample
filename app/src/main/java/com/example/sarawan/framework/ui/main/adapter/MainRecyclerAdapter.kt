package com.example.sarawan.framework.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.example.sarawan.R
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.model.data.DataModel
import com.google.android.material.textview.MaterialTextView
import java.util.*

class MainRecyclerAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private val data: LinkedList<DataModel>,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        data.push(DataModel(itemDescription = "Выгодные предложения"))
    }

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
            if (it.isTopCard == true) {
                this.data.add(1, it)
                notifyItemInserted(1)
            } else {
                this.data.add(it)
                notifyItemInserted(this.data.size)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_COMMON -> CommonCardsViewHolder(
                ListItemCardBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            TYPE_TOP -> TopCardsViewHolder(
                ListItemCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_STRING -> StringHolder(MaterialTextView(parent.context))
            else -> throw RuntimeException("No Such viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            getItemViewType(position) == TYPE_COMMON -> {
                holder as CommonCardsViewHolder
                holder.bind(data[position])
            }
            getItemViewType(position) == TYPE_TOP -> {
                holder as TopCardsViewHolder
                holder.bind(data[position])
            }
            getItemViewType(position) == TYPE_STRING -> {
                holder as StringHolder
                holder.bind(data[position])
            }
            else -> throw RuntimeException("No binder for holder: $holder")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position].isTopCard) {
            true -> TYPE_TOP
            false -> TYPE_COMMON
            else -> TYPE_STRING
        }
    }

    inner class StringHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: DataModel) {
            (view as TextView).text = data.itemDescription
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.top_card_background
                )
            )

        }
    }

    inner class CommonCardsViewHolder(binding: ListItemCardBinding) : CardItemViewHolder(binding)

    inner class TopCardsViewHolder(private val binding: ListItemCardBinding) :
        CardItemViewHolder(binding) {
        override fun bind(data: DataModel) {
            super.bind(data)
            binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.top_card_background
                )
            )
        }
    }

    abstract inner class CardItemViewHolder(private val binding: ListItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var disposable: Disposable? = null

        open fun bind(data: DataModel) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                var quantity = data.quantity ?: 0
                data.quantity = quantity

                with(binding) {

                    if (quantity > 0) {
                        itemBuyButton.visibility = View.GONE
                        itemQuantityLayout.visibility = View.VISIBLE
                        itemQuantity.text = quantity.toString()
                    }

                    itemPrice.text = String.format("%.2f", data.price)
                    itemShopName.text = data.shop
                    itemWeight.text = data.weight
                    itemDescription.text = data.itemDescription

                    itemBuyButton.setOnClickListener {
                        itemBuyButton.visibility = View.GONE
                        itemQuantityLayout.visibility = View.VISIBLE
                        quantity = 1
                        data.quantity = quantity
                        itemQuantity.text = data.quantity.toString()
                        changeQuantity(data)
                    }

                    minusButton.setOnClickListener {
                        quantity -= 1
                        data.quantity = quantity
                        if (quantity <= 0) {
                            itemBuyButton.visibility = View.VISIBLE
                            itemQuantityLayout.visibility = View.GONE
                        }
                        itemQuantity.text = quantity.toString()
                        changeQuantity(data)
                    }

                    plusButton.setOnClickListener {
                        quantity += 1
                        data.quantity = quantity
                        itemQuantity.text = quantity.toString()
                        changeQuantity(data)
                    }
                }

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
            }
        }

        open fun cancelTask() = disposable?.dispose()
    }

    private fun changeQuantity(listItemData: DataModel) {
        onListItemClickListener.onItemClick(listItemData)
    }

    interface OnListItemClickListener {
        fun onItemClick(data: DataModel)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is CardItemViewHolder) {
            holder.cancelTask()
        }
        super.onViewRecycled(holder)
    }

    companion object {
        const val TYPE_TOP = 0
        const val TYPE_COMMON = 1
        const val TYPE_STRING = 2
    }
}