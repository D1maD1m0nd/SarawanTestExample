package ru.sarawan.android.framework.ui.catalog.viewHolder

import androidx.recyclerview.widget.RecyclerView
import ru.sarawan.android.databinding.CatalogListItemBinding
import ru.sarawan.android.framework.ui.catalog.adapter.CatalogRecyclerAdapter
import ru.sarawan.android.model.data.CardScreenDataModel

class ListItemHolder(
    private val binding: CatalogListItemBinding,
    private val listener: CatalogRecyclerAdapter.OnListItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: CardScreenDataModel) {
        with(binding) {
            catalogItemText.text = data.itemDescription

            catalogItemText.setOnClickListener {
                listener.onItemClick(data)
            }
        }
    }
}