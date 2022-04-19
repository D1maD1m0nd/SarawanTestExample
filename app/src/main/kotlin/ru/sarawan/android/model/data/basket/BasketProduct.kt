package ru.sarawan.android.model.data.basket

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import ru.sarawan.android.model.data.product.Image
import ru.sarawan.android.model.data.product.StorePrice

@Parcelize
data class BasketProduct(
    @field:Json(name = "store_prices") val storePrices: List<StorePrice>? = null,
    @field:Json(name = "product") val basketProduct: BasketProduct? = null,
    @field:Json(name = "price") val price: String? = null,
    @field:Json(name = "discount") val discount: Int? = null,
    @field:Json(name = "store") val store: String? = null,
    @field:Json(name = "product_store_id") val productStoreId: Int? = null,
    @field:Json(name = "images") val images: List<Image>? = null,
    @field:Json(name = "name") val name: String? = null,
    @field:Json(name = "price_type") val priceType: String? = null,
    @field:Json(name = "id") val id: Long? = null,
    @field:Json(name = "unit_quantity") val unitQuantity: String? = null,
    @field:Json(name = "information") var information: ProductInformation? = null
) : Parcelable