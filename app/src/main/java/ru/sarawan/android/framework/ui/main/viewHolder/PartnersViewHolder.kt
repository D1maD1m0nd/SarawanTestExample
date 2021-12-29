package ru.sarawan.android.framework.ui.main.viewHolder

import androidx.recyclerview.widget.RecyclerView
import ru.sarawan.android.framework.ui.main.adapter.PartnersRecyclerAdapter
import ru.sarawan.android.model.data.MainScreenDataModel

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