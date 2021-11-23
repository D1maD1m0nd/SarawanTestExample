package com.example.sarawan.framework.ui.main.viewHolder

import android.view.View
import coil.ImageLoader
import com.example.sarawan.databinding.ListItemCardBinding
import com.example.sarawan.framework.ui.main.adapter.MainRecyclerAdapter
import com.example.sarawan.model.data.MainScreenDataModel

class CommonCardsViewHolder(
    private val binding: ListItemCardBinding,
    imageLoader: ImageLoader,
    listener: MainRecyclerAdapter.OnListItemClickListener
) : CardItemViewHolder(binding, imageLoader, listener) {

    override fun bind(data: MainScreenDataModel) {
        super.bind(data)
        binding.discount.visibility = View.GONE
    }

}