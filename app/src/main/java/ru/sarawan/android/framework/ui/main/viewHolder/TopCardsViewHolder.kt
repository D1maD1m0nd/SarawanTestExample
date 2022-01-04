package ru.sarawan.android.framework.ui.main.viewHolder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.sarawan.android.R
import ru.sarawan.android.framework.ui.main.adapter.TopRecyclerAdapter
import ru.sarawan.android.model.data.MainScreenDataModel

class TopCardsViewHolder(
    view: RecyclerView,
    private val topRecyclerAdapter: TopRecyclerAdapter
) : RecyclerView.ViewHolder(view) {

    init {
        view.adapter = topRecyclerAdapter
        view.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                R.color.top_card_background
            )
        )
    }

    fun bind(data: List<MainScreenDataModel>?) {
        topRecyclerAdapter.setData(data)
    }
}