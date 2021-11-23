package com.example.sarawan.framework.ui.basket

import androidx.fragment.app.DialogFragment
import com.example.sarawan.model.data.delegatesModel.BasketListItem

interface ItemClickListener {
    fun showModal(fragment : DialogFragment)
    fun deleteItem(item : BasketListItem, pos : Int)
}