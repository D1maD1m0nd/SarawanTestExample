package ru.sarawan.android.framework.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.google.android.material.textview.MaterialTextView
import ru.sarawan.android.databinding.ListItemButtonBinding
import ru.sarawan.android.databinding.ListItemCardBinding
import ru.sarawan.android.databinding.LoadingLayoutBinding
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogAdapter
import ru.sarawan.android.framework.ui.base.mainCatalog.CardType
import ru.sarawan.android.framework.ui.main.viewHolder.*
import ru.sarawan.android.model.data.CardScreenDataModel
import ru.sarawan.android.model.data.Product

class MainRecyclerAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private val imageLoader: ImageLoader,
    private val onButtonMoreClickListener: ButtonMoreClickListener,
    private val changeCardCallback: (CardScreenDataModel, Long) -> Unit,
    private val hideLoadingCallback: () -> Unit
) : BaseMainCatalogAdapter() {

    private val topData: MutableList<CardScreenDataModel> = mutableListOf()
    private val commonData: MutableList<CardScreenDataModel> = mutableListOf()

    private var isLoadingHidden = false

    private var loadingLayout: LoadingLayoutBinding? = null

    private val topRecyclerAdapter =
        TopRecyclerAdapter(onListItemClickListener, imageLoader) { measuredHeight: Int ->
            recyclerHeight = measuredHeight
            hideLoadingCallback()
        }
    private lateinit var topCardsRecycler: RecyclerView

    fun setData(data: List<CardScreenDataModel>?, isRecommended: Boolean, maxCommonData: Int) {
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
            CardScreenDataModel(
                cardType = CardType.LOADING.type
            )
        )
    }

    private fun setCommonData(data: List<CardScreenDataModel>) {
        val filteredData = data.filter { it.cardType == CardType.COMMON.type }
        commonData.addAll(filteredData)
        filteredData.forEach { setData(it) }
    }

    private fun setData(data: CardScreenDataModel) {
        val index = if (isLoadingHidden) itemCount else itemCount - 1
        displayData.add(index, data)
        notifyItemInserted(index)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setRecommendedString(isRecommended: Boolean) {
        if (displayData.none { it.cardType == CardType.COMMON.type }) {
            displayData.add(
                CardScreenDataModel(
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

    private fun setTopData(data: List<CardScreenDataModel>) {
        val filteredTopData = data.filter { it.cardType == CardType.TOP.type }
        if (filteredTopData.isNotEmpty()) {
            topData.addAll(filteredTopData)
            displayData.add(
                CardScreenDataModel(
                    itemDescription = "Выгодные предложения",
                    fontSize = 28F,
                    padding = arrayListOf(0, 32, 0, 14),
                    gravity = Gravity.CENTER,
                    cardType = CardType.STRING.type
                )
            )
            displayData.add(CardScreenDataModel(cardType = CardType.TOP.type))
            displayData.add(
                CardScreenDataModel(
                    itemDescription = "Посмотреть всё",
                    cardType = CardType.BUTTON.type
                )
            )
        } else hideLoadingCallback()
    }

    fun changeProduct(product: Product) {
        val dataToRemove: MutableList<CardScreenDataModel> = mutableListOf()
        changeProductInCommonData(product, dataToRemove)
        dataToRemove.clear()
        changeProductInTopData(product, dataToRemove)
    }

    private fun changeProductInTopData(
        product: Product,
        dataToRemove: MutableList<CardScreenDataModel>
    ) {
        topData.forEach {
            if (product.id == it.id) product.storePrices?.forEach { storePrice ->
                if (storePrice.count > 0) {
                    if (storePrice.id != it.storeId) {
                        dataToRemove.add(it)
                        topRecyclerAdapter.deleteProduct(it)
                    } else {
                        it.quantity = storePrice.count
                        val holder =
                            topCardsRecycler.findViewHolderForAdapterPosition(topData.indexOf(it))
                        if (holder != null && holder is CardItemViewHolder) holder.bind(it)
                        topRecyclerAdapter.changeProduct(it)
                    }
                } else if (storePrice.id == it.storeId) {
                    it.quantity = 0
                    val holder =
                        topCardsRecycler.findViewHolderForAdapterPosition(topData.indexOf(it))
                    if (holder != null && holder is CardItemViewHolder) holder.bind(it)
                    topRecyclerAdapter.changeProduct(it)
                }
            }
        }
        topData.removeAll(dataToRemove)
    }

    private fun changeProductInCommonData(
        product: Product,
        dataToRemove: MutableList<CardScreenDataModel>
    ) {
        commonData.forEach {
            if (product.id == it.id) product.storePrices?.forEach { storePrice ->
                if (storePrice.count > 0) {
                    if (storePrice.id != it.storeId) {
                        dataToRemove.add(it)
                        notifyItemRemoved(displayData.indexOf(it))
                        displayData.remove(it)
                    } else {
                        it.quantity = storePrice.count
                        changeCardCallback(it, getItemId(displayData.indexOf(it)))
                    }
                } else if (storePrice.id == it.storeId) {
                    it.quantity = 0
                    changeCardCallback(it, getItemId(displayData.indexOf(it)))
                }
            }
        }
        commonData.removeAll(dataToRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CardType.COMMON.type -> CardItemViewHolder(
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
            CardType.STRING.type -> StringHolder(MaterialTextView(parent.context))
            CardType.BUTTON.type -> ButtonHolder(
                ListItemButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onButtonMoreClickListener
            )
            CardType.LOADING.type -> {
                loadingLayout = LoadingLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                object : RecyclerView.ViewHolder(loadingLayout!!.root) {}
            }
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

    fun changeLoadingAnimation(enable: Boolean) {
        loadingLayout?.let {
            if (!isLoadingHidden && it.root.isVisible) {
                if (enable) {
                    if (!it.loadingProgress.isIndeterminate) {
                        it.loadingProgress.visibility = View.INVISIBLE
                        it.loadingProgress.isIndeterminate = true
                        it.loadingProgress.visibility = View.VISIBLE
                    }
                } else if (it.loadingProgress.isIndeterminate)
                    it.loadingProgress.setProgressCompat(20, true)
            }
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