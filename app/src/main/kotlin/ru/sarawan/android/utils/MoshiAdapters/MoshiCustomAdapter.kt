package ru.sarawan.android.utils.MoshiAdapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import ru.sarawan.android.model.data.basket.ProductInformation
import java.lang.reflect.Type
import javax.inject.Inject

class MoshiCustomAdapter @Inject constructor(val moshi: Moshi) {
    private val jsonAdapter: JsonAdapter<ProductInformation> =
        moshi.adapter(ProductInformation::class.java)

    fun infoFromJson(json: String): ProductInformation? {
        return jsonAdapter.fromJson(json)
    }

    companion object {
        val LENIENT_FACTORY: JsonAdapter.Factory = object : JsonAdapter.Factory {
            override fun create(
                type: Type,
                annotations: Set<Annotation>,
                moshi: Moshi
            ): JsonAdapter<ProductInformation>? {
                return moshi.nextAdapter<ProductInformation>(this, type, annotations).lenient()
            }
        }
    }

}