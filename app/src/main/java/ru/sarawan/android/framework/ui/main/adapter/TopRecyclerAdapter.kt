package ru.sarawan.android.framework.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import ru.sarawan.android.databinding.ListItemCardBinding
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogAdapter
import ru.sarawan.android.framework.ui.main.viewHolder.CardItemViewHolder
import ru.sarawan.android.model.data.MainScreenDataModel

class TopRecyclerAdapter(
    private var onListItemClickListener: BaseMainCatalogAdapter.OnListItemClickListener,
    private val imageLoader: ImageLoader,
    private val callback: (measuredHeight: Int) -> Unit
) : RecyclerView.Adapter<CardItemViewHolder>() {

    private val displayData: MutableList<MainScreenDataModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<MainScreenDataModel>?) {
        displayData.clear()
        if (!data.isNullOrEmpty()) displayData.addAll(data.sortedByDescending { it.discount })
        notifyDataSetChanged()
    }

    fun deleteProduct(product: MainScreenDataModel) {
        notifyItemRemoved(displayData.indexOf(product))
        displayData.remove(product)
    }

    fun changeProduct(product: MainScreenDataModel) {
        displayData.find { it.id == product.id }?.apply {
            quantity = product.quantity
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val view = ListItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).apply {
            root.layoutParams.apply {
                width = parent.width / 2
            }
        }
        return CardItemViewHolder(
            view,
            imageLoader,
            onListItemClickListener
        )
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
        holder.itemView.post {
            callback(holder.itemView.measuredHeight)
        }
        holder.bind(displayData[position])
    }

    override fun onViewRecycled(holder: CardItemViewHolder) {
        holder.cancelTask()
        super.onViewRecycled(holder)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        displayData.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = displayData.size
}