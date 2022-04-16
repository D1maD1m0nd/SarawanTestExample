package ru.sarawan.android.framework.ui.profile.address_fragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import ru.sarawan.android.R
import ru.sarawan.android.model.data.address.sarawan.AddressItem
import java.util.*

class AddressAdapter(context: Context, countryList: List<AddressItem>) :
    ArrayAdapter<AddressItem>(context, 0, countryList) {
    private val countryListFull: List<AddressItem>

    init {
        countryListFull = ArrayList<AddressItem>(countryList)
    }

    override fun getFilter(): Filter {
        return countryFilter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                R.layout.address_item_layout, parent, false
            )
        }
        val textViewName = convertView!!.findViewById<TextView>(R.id.text_view_name)
        val countryItem: AddressItem? = getItem(position)
        if (countryItem != null) {
            textViewName.text = countryItem.addressFull
        }
        return convertView
    }

    private val countryFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            val suggestions: MutableList<AddressItem> = ArrayList()
            if (constraint.isEmpty()) {
                suggestions.addAll(countryListFull)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in countryListFull) {
                    if (item.addressFull.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        suggestions.add(item)
                    }
                }
            }
            results.values = suggestions
            results.count = suggestions.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (count > 0 && !constraint.isNullOrEmpty()) {
                clear()
                addAll(results?.values as List<AddressItem>)
                notifyDataSetChanged()
            }

        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as AddressItem).addressFull
        }
    }

}