package ru.sarawan.android.framework.ui.catalog.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import ru.sarawan.android.databinding.CatalogListItemBinding
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogAdapter
import ru.sarawan.android.framework.ui.base.mainCatalog.CardType
import ru.sarawan.android.framework.ui.catalog.viewHolder.ListItemHolder
import ru.sarawan.android.framework.ui.main.viewHolder.StringHolder
import ru.sarawan.android.model.data.CardScreenDataModel

class CatalogRecyclerAdapter(
    private var onListItemClickListener: OnListItemClickListener
) : BaseMainCatalogAdapter() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<CardScreenDataModel>?, callback: () -> Unit) {
        if (data == null) {
            callback()
            return
        }
        displayData.clear()
        displayData.add(
            CardScreenDataModel(
                itemDescription = "Каталог",
                fontSize = 28f,
                backgroundColor = Color.WHITE,
                cardType = CardType.TOP.type,
                gravity = Gravity.START,
                fontType = Typeface.BOLD,
                padding = arrayListOf(20, 24, 0, 8),
            )
        )
        displayData.addAll(data)
        displayData.add(
            CardScreenDataModel(
                cardType = CardType.TOP.type,
                backgroundColor = Color.WHITE,
                fontSize = 28f
            )
        )
        notifyDataSetChanged()
        callback()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CardType.STRING.type -> ListItemHolder(
                CatalogListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onListItemClickListener
            )
            CardType.TOP.type -> StringHolder(MaterialTextView(parent.context))
            else -> throw RuntimeException("No Such viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            CardType.STRING.type -> {
                holder as ListItemHolder
                holder.bind(displayData[position])
            }
            CardType.TOP.type -> {
                holder as StringHolder
                holder.bind(displayData[position])
            }
            else -> throw RuntimeException("No binder for holder: $holder")
        }
    }

    interface OnListItemClickListener {
        fun onItemClick(data: CardScreenDataModel)
    }
}