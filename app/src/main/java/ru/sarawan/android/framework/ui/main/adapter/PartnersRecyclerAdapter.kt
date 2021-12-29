package ru.sarawan.android.framework.ui.main.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import ru.sarawan.android.framework.ui.main.viewHolder.ImageHolder
import ru.sarawan.android.model.data.MainScreenDataModel

class PartnersRecyclerAdapter(
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<ImageHolder>() {

    private val displayData: MutableList<MainScreenDataModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<MainScreenDataModel>?) {
        displayData.clear()
        if (data == null) return
        displayData.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = ImageView(parent.context)
        return ImageHolder(
            view,
            imageLoader
        )
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(displayData[position])
    }

    override fun getItemCount(): Int = displayData.size

    override fun onViewRecycled(holder: ImageHolder) {
        holder.cancelTask()
        super.onViewRecycled(holder)
    }
}