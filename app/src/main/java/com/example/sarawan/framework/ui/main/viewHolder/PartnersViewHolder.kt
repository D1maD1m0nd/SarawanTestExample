package com.example.sarawan.framework.ui.main.viewHolder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.R
import com.example.sarawan.framework.ui.main.adapter.PartnersRecyclerAdapter
import com.example.sarawan.framework.ui.main.adapter.TopRecyclerAdapter
import com.example.sarawan.model.data.MainScreenDataModel

class PartnersViewHolder(
    view: RecyclerView,
    private val partnersRecyclerAdapter: PartnersRecyclerAdapter
) : RecyclerView.ViewHolder(view) {

    init {
        view.adapter = partnersRecyclerAdapter
    }

    fun bind(data: List<MainScreenDataModel>?) {
        partnersRecyclerAdapter.setData(data)
    }
}