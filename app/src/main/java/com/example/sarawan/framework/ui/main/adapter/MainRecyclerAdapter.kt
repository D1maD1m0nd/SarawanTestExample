package com.example.sarawan.framework.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.example.sarawan.databinding.ListItemButtonBinding
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.framework.ui.main.viewHolder.ButtonHolder
import com.example.sarawan.framework.ui.main.viewHolder.CommonCardsViewHolder
import com.example.sarawan.framework.ui.main.viewHolder.StringHolder
import com.example.sarawan.framework.ui.main.viewHolder.TopCardsViewHolder
import com.example.sarawan.model.data.DataModel
import com.google.android.material.textview.MaterialTextView

class MainRecyclerAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private val imageLoader: ImageLoader,
    private val callback: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val displayData: MutableList<DataModel> = mutableListOf()
    private val topData: MutableList<DataModel> = mutableListOf()

    private val topRecyclerAdapter =
        TopRecyclerAdapter(onListItemClickListener, imageLoader) { measuredHeight: Int ->
            recyclerHeight = measuredHeight
            callback()
        }
    private lateinit var innerRecycler: RecyclerView

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<DataModel>?) {
        if (data == null) return
        topData.clear()
        topData.addAll(data.filter { it.cardType == CardType.TOP.type })
        displayData.clear()
        displayData.add(
            DataModel(
                itemDescription = "Выгодные предложения",
                price = 28F,
                discount = 0,
                cardType = CardType.STRING.type
            )
        )
        displayData.add(DataModel(cardType = CardType.TOP.type))
        displayData.add(
            DataModel(
                itemDescription = "Посмотреть еще",
                cardType = CardType.BUTTON.type
            )
        )
        notifyDataSetChanged()
        val commonData = data.filter { it.cardType == CardType.COMMON.type }
        commonData.forEach {
            setData(it)
        }
        if (commonData.size % 2 != 0) setData(DataModel(cardType = CardType.EMPTY.type))
        displayData.add(
            DataModel(
                itemDescription = "Наши партнеры",
                id = Color.WHITE.toLong(),
                price = 28F,
                discount = 1,
                cardType = CardType.STRING.type
            )
        )
    }

    private fun setData(data: DataModel?) {
        data?.let {
            if (it.cardType == CardType.COMMON.type) {
                displayData.add(it)
                notifyItemInserted(displayData.size)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CardType.COMMON.type -> CommonCardsViewHolder(
                ListItemCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                imageLoader,
                onListItemClickListener
            )
            CardType.TOP.type -> {
                innerRecycler = RecyclerView(parent.context)
                innerRecycler.layoutManager =
                    LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)


                TopCardsViewHolder(innerRecycler, topRecyclerAdapter)
            }
            CardType.STRING.type -> StringHolder(MaterialTextView(parent.context))
            CardType.BUTTON.type -> ButtonHolder(
                ListItemButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw RuntimeException("No Such viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            CardType.COMMON.type -> {
                holder as CommonCardsViewHolder
                holder.bind(displayData[position])
            }
            CardType.TOP.type -> {
                if (recyclerHeight > 0) {
                    holder.itemView.layoutParams = ViewGroup.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        recyclerHeight
                    )
                }
                holder.itemView.post {
                    holder as TopCardsViewHolder
                    holder.bind(topData)
                }
            }
            CardType.STRING.type -> {
                holder as StringHolder
                holder.bind(displayData[position])
            }
            CardType.BUTTON.type -> {
                holder as ButtonHolder
                holder.bind(displayData[position])
            }
            CardType.EMPTY.type -> {
                holder as CommonCardsViewHolder
                holder.itemView.visibility = View.INVISIBLE
            }
            else -> throw RuntimeException("No binder for holder: $holder")
        }
    }

    override fun getItemCount(): Int = displayData.size

    override fun getItemViewType(position: Int): Int {
        return displayData[position].cardType ?: -1
    }

    interface OnListItemClickListener {
        fun onItemClick(data: DataModel)
    }

    interface CancellableHolder {
        fun cancelTask()
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is CancellableHolder) {
            holder.cancelTask()
        }
        super.onViewRecycled(holder)
    }

    companion object {
        private var recyclerHeight = 0
    }
}

enum class CardType(val type: Int) {
    TOP(0),
    COMMON(1),
    STRING(2),
    BUTTON(3),
    EMPTY(4)
}