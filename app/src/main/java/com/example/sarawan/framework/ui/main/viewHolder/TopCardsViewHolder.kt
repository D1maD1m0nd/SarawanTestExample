package com.example.sarawan.framework.ui.main.viewHolder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.R
import com.example.sarawan.framework.ui.main.adapter.TopRecyclerAdapter
import com.example.sarawan.model.data.DataModel

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

    fun bind(data: List<DataModel>?) {
        topRecyclerAdapter.setData(data)
    }
}