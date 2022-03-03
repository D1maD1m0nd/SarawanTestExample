package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.CategoryDataModel
import javax.inject.Inject

class CategoriesRetrofitImpl @Inject constructor(
    private val apiService: ApiService
) : CategoriesDataSource {
    override fun getCategories(): Single<List<CategoryDataModel>> = apiService.getCategories()
}