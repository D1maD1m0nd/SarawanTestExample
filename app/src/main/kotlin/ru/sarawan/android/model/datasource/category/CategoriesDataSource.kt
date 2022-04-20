package ru.sarawan.android.model.datasource.category

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.CategoryDataModel

interface CategoriesDataSource {
    fun getCategories(): Single<List<CategoryDataModel>>
}