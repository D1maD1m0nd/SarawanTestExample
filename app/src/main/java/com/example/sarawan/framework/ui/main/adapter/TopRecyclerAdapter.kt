package com.example.sarawan.framework.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.framework.ui.main.viewHolder.CardItemViewHolder
import com.example.sarawan.model.data.DataModel

class TopRecyclerAdapter(
    private var onListItemClickListener: MainRecyclerAdapter.OnListItemClickListener,
    private val imageLoader: ImageLoader,
    private val callback: (measuredHeight: Int) -> Unit
) : RecyclerView.Adapter<CardItemViewHolder>() {

    private val displayData: MutableList<DataModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<DataModel>?) {
        displayData.clear()
        if (data == null) return
        displayData.addAll(data)
        notifyDataSetChanged()
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
        return object : CardItemViewHolder(
            view,
            imageLoader,
            onListItemClickListener
        ) {}
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

    override fun getItemCount(): Int = displayData.size
}