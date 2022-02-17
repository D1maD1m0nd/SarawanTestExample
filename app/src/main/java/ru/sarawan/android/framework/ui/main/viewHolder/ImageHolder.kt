package ru.sarawan.android.framework.ui.main.viewHolder

import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import ru.sarawan.android.R
import ru.sarawan.android.framework.ui.main.adapter.MainRecyclerAdapter
import ru.sarawan.android.model.data.CardScreenDataModel

class ImageHolder(private val view: ImageView, private val imageLoader: ImageLoader) :
    RecyclerView.ViewHolder(view), MainRecyclerAdapter.CancellableHolder {

    private var disposable: Disposable? = null

    fun bind(data: CardScreenDataModel) {
        with(view) {
            val color = data.backgroundColor ?: ContextCompat.getColor(
                view.context,
                R.color.top_card_background
            )
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setBackgroundColor(color)
            foregroundGravity = Gravity.CENTER
            data.padding?.let {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    60
                )
                params.setMargins(it[0], it[1], it[2], it[3])
                layoutParams = params
            }
            disposable = imageLoader.enqueue(
                ImageRequest.Builder(view.context)
                    .data(data.pictureUrl?.toInt())
                    .target(this)
                    .build()
            )
        }
    }

    override fun cancelTask() {
        disposable?.dispose()
    }
}