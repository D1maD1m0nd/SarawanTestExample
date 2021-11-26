package com.example.sarawan.framework.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.example.sarawan.databinding.ListItemButtonBinding
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.framework.ui.main.viewHolder.*
import com.example.sarawan.model.data.MainScreenDataModel
import com.google.android.material.textview.MaterialTextView

class MainRecyclerAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private val imageLoader: ImageLoader,
    private val callback: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val displayData: MutableList<MainScreenDataModel> = mutableListOf()
    private val topData: MutableList<MainScreenDataModel> = mutableListOf()
    private val partnersData: MutableList<MainScreenDataModel> = mutableListOf()

    private val topRecyclerAdapter =
        TopRecyclerAdapter(onListItemClickListener, imageLoader) { measuredHeight: Int ->
            recyclerHeight = measuredHeight
            callback()
        }
    private lateinit var topCardsRecycler: RecyclerView

    private val partnersRecyclerAdapter = PartnersRecyclerAdapter(imageLoader)
    private lateinit var partnersCardsRecycler: RecyclerView

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<MainScreenDataModel>?, isRecommended: Boolean) {
        if (data == null) return
        topData.clear()
        displayData.clear()
        partnersData.clear()
        topData.addAll(data.filter { it.cardType == CardType.TOP.type })
        partnersData.addAll(data.filter { it.cardType == CardType.PARTNERS.type })
        displayData.add(
            MainScreenDataModel(
                itemDescription = "Выгодные предложения",
                fontSize = 28F,
                padding = arrayListOf(0, 32, 0, 14),
                gravity = Gravity.CENTER,
                cardType = CardType.STRING.type
            )
        )
        displayData.add(MainScreenDataModel(cardType = CardType.TOP.type))
        displayData.add(
            MainScreenDataModel(
                itemDescription = "Посмотреть еще",
                cardType = CardType.BUTTON.type
            )
        )
        displayData.add(
            MainScreenDataModel(
                itemDescription = if (isRecommended) "Мы рекомендуем" else null,
                fontSize = if (isRecommended) 18F else 0F,
                backgroundColor = Color.WHITE,
                padding = if (isRecommended) arrayListOf(13, 24, 0, 14)
                else arrayListOf(13, 10, 0, 0),
                gravity = Gravity.START,
                fontType = Typeface.BOLD,
                cardType = CardType.STRING.type
            )
        )
        notifyDataSetChanged()
        val commonData = data.filter { it.cardType == CardType.COMMON.type }
        commonData.forEach {
            setData(it)
        }
        if (commonData.size % 2 != 0) setData(MainScreenDataModel(cardType = CardType.EMPTY.type))
        displayData.add(
            MainScreenDataModel(
                itemDescription = "Наши магазины",
                backgroundColor = Color.WHITE,
                padding = arrayListOf(13, 32, 0, 8),
                fontSize = 28F,
                gravity = Gravity.START,
                cardType = CardType.STRING.type
            )
        )
        displayData.add(MainScreenDataModel(cardType = CardType.PARTNERS.type))
    }

    private fun setData(data: MainScreenDataModel?) {
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
                topCardsRecycler = RecyclerView(parent.context)
                topCardsRecycler.layoutManager =
                    LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
                TopCardsViewHolder(topCardsRecycler, topRecyclerAdapter)
            }
            CardType.PARTNERS.type -> {
                partnersCardsRecycler = RecyclerView(parent.context)
                partnersCardsRecycler.layoutManager =
                    LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 140)
                partnersCardsRecycler.layoutParams = params
                PartnersViewHolder(partnersCardsRecycler, partnersRecyclerAdapter)
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
            CardType.PARTNERS.type -> {
                holder as PartnersViewHolder
                holder.bind(partnersData)
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
        fun onItemClick(data: MainScreenDataModel, diff: Int)
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
    EMPTY(4),
    PARTNERS(5)
}