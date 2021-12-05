package com.example.sarawan.framework.ui.base.mainCatalog

import com.example.sarawan.model.data.MainScreenDataModel

interface MainCatalogInterface {

    fun search(word: String, isOnline: Boolean)
    fun saveData(data: MainScreenDataModel, isOnline: Boolean, isNewItem: Boolean)
    fun getStartData(isOnline: Boolean)
}