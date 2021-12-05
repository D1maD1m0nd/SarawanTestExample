package com.example.sarawan.framework.ui.catalog.viewHolder

import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.databinding.CatalogListItemBinding
import com.example.sarawan.framework.ui.catalog.adapter.CatalogRecyclerAdapter
import com.example.sarawan.model.data.MainScreenDataModel

class ListItemHolder(
    private val binding: CatalogListItemBinding,
    private val listener: CatalogRecyclerAdapter.OnListItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: MainScreenDataModel) {
        with(binding) {
            catalogItemText.text = data.itemDescription

            catalogItemText.setOnClickListener {
                listener.onItemClick(data)
            }
        }
    }
}