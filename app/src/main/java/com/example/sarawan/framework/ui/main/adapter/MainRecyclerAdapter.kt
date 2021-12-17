package com.example.sarawan.framework.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.example.sarawan.databinding.ListItemButtonBinding
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.databinding.LoadingLayoutBinding
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogAdapter
import com.example.sarawan.framework.ui.base.mainCatalog.CardType
import com.example.sarawan.framework.ui.main.viewHolder.ButtonHolder
import com.example.sarawan.framework.ui.main.viewHolder.CardItemViewHolder
import com.example.sarawan.framework.ui.main.viewHolder.StringHolder
import com.example.sarawan.framework.ui.main.viewHolder.TopCardsViewHolder
import com.example.sarawan.model.data.MainScreenDataModel
import com.google.android.material.textview.MaterialTextView

class MainRecyclerAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private val imageLoader: ImageLoader,
    private val callback: () -> Unit
) : BaseMainCatalogAdapter() {

    private val topData: MutableList<MainScreenDataModel> = mutableListOf()
    private val commonData: MutableList<MainScreenDataModel> = mutableListOf()

    private var isLoadingHidden = false

    private val topRecyclerAdapter =
        TopRecyclerAdapter(onListItemClickListener, imageLoader) { measuredHeight: Int ->
            recyclerHeight = measuredHeight
            callback()
        }
    private lateinit var topCardsRecycler: RecyclerView

    fun setData(data: List<MainScreenDataModel>?, isRecommended: Boolean, maxCommonData: Int) {
        if (data.isNullOrEmpty()) return
        setTopData(data)
        setRecommendedString(isRecommended)
        setLoading()
        setCommonData(data)
        removeLoading(maxCommonData)
    }

    private fun removeLoading(maxCommonData: Int) {
        if (!isLoadingHidden && maxCommonData - commonData.size < 1) {
            isLoadingHidden = true
            displayData.removeLast()
            notifyItemRemoved(itemCount)
        }
    }

    private fun setLoading() {
        if (!isLoadingHidden && displayData.none { it.cardType == CardType.LOADING.type }) displayData.add(
            MainScreenDataModel(
                cardType = CardType.LOADING.type
            )
        )
    }

    private fun setCommonData(data: List<MainScreenDataModel>) {
        commonData.addAll(data.filter { it.cardType == CardType.COMMON.type })
        commonData.forEach { setData(it) }
    }

    private fun setData(data: MainScreenDataModel) {
        val index = if (isLoadingHidden) itemCount else itemCount - 1
        displayData.add(index, data)
        notifyItemInserted(index)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setRecommendedString(isRecommended: Boolean) {
        if (displayData.none { it.cardType == CardType.COMMON.type }) {
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
        }
    }

    private fun setTopData(data: List<MainScreenDataModel>) {
        val filteredTopData = data.filter { it.cardType == CardType.TOP.type }
        if (filteredTopData.isNotEmpty()) {
            topData.addAll(filteredTopData)
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
                    itemDescription = "Посмотреть всё",
                    cardType = CardType.BUTTON.type
                )
            )
        } else callback()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CardType.COMMON.type -> object : CardItemViewHolder(
                ListItemCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                imageLoader,
                onListItemClickListener
            ) {}
            CardType.TOP.type -> {
                topCardsRecycler = RecyclerView(parent.context)
                topCardsRecycler.layoutManager =
                    LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
                TopCardsViewHolder(topCardsRecycler, topRecyclerAdapter)
            }
            CardType.STRING.type -> StringHolder(MaterialTextView(parent.context))
            CardType.BUTTON.type -> ButtonHolder(
                ListItemButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            CardType.LOADING.type -> object : RecyclerView.ViewHolder(
                LoadingLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).root
            ) {}
            else -> throw RuntimeException("No Such viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            CardType.COMMON.type -> {
                holder as CardItemViewHolder
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
            CardType.LOADING.type -> Unit
            else -> throw RuntimeException("No binder for holder: $holder")
        }
    }

    override fun clear() {
        topData.clear()
        commonData.clear()
        topRecyclerAdapter.clear()
        super.clear()
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