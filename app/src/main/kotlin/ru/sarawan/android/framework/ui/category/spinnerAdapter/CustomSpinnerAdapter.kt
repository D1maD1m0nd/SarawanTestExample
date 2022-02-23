package ru.sarawan.android.framework.ui.category.spinnerAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import ru.sarawan.android.R
import ru.sarawan.android.databinding.SpinnerDropdownViewElementBinding
import ru.sarawan.android.utils.constants.SortBy

class CustomSpinnerAdapter(
    context: Context,
    @LayoutRes resource: Int,
    @IdRes textViewResourceId: Int,
    private val objects: List<SpinnerDropdownViewElementBinding>,
    strings: Array<out String>
) : ArrayAdapter<String>(context, resource, textViewResourceId, strings) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getView(position, convertView, parent).apply {
            when (position) {
                SortBy.PRICE_ASC.id -> findViewById<AppCompatImageView>(R.id.item_icon).setImageResource(
                    R.drawable.spinner_arrow_up
                )
                SortBy.PRICE_DES.id -> findViewById<AppCompatImageView>(R.id.item_icon).setImageResource(
                    R.drawable.spinner_arrow_down
                )
            }
            alpha = 1f
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return objects[position].root.apply {
            layoutParams =
                AbsListView.LayoutParams(parent.width, AbsListView.LayoutParams.WRAP_CONTENT)
        }
    }

    fun setSelected(selected: Int) {
        for (i in objects.indices) {
            if (i == selected) {
                objects[i].root.setBackgroundResource(R.drawable.spinner_background)
                objects[i].root.alpha = 1f
            } else {
                objects[i].root.background = null
                objects[i].root.alpha = .6f
            }
        }
    }
}