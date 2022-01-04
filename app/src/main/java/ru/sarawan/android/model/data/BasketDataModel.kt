package ru.sarawan.android.model.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import ru.sarawan.android.model.data.delegatesModel.BasketListItem
import java.util.*

data class Basket(
    @field:Json(name="basket_id") val basketId: Int? = null,
    @field:Json(name="products") val products: List<ProductsItem>? = null
)

@Parcelize
data class ProductsItem(
    @field:Json(name="product") val basketProduct: BasketProduct? = null,
    @field:Json(name="quantity") var quantity: Int? = null,
    @field:Json(name="basket_product_id") val basketProductId: Int? = null
) : Parcelable, BasketListItem {
    override val id: Long
        get() = Random().nextLong()
}

@Parcelize
data class BasketProduct(
    @field:Json(name="store_prices") val storePrices: List<StorePrice>? = null,
    @field:Json(name="product") val basketProduct: BasketProduct? = null,
    @field:Json(name="price") val price: String? = null,
    @field:Json(name="discount") val discount: Int? = null,
    @field:Json(name="store") val store: String? = null,
    @field:Json(name="product_store_id") val productStoreId: Int? = null,
    @field:Json(name="images") val images: List<Image>? = null,
    @field:Json(name="name") val name: String? = null,
    @field:Json(name="price_type") val priceType: String? = null,
    @field:Json(name="id") val id: Long? = null,
    @field:Json(name="unit_quantity") val unitQuantity: String? = null
) : Parcelable

data class BasketResponse(
    @field:Json(name="id") val basketId: Int? = null
)

fun ProductsItem.toProduct() =
    Product(
        id = basketProduct?.basketProduct?.id,
        images = basketProduct?.images,
        product = basketProduct?.productStoreId,
        name = basketProduct?.basketProduct?.name,
        priceType = basketProduct?.basketProduct?.priceType,
        storePrices = basketProduct?.basketProduct?.storePrices?.toMutableList(),
        unitQuantity = basketProduct?.basketProduct?.unitQuantity,
        quantity = quantity ?: 0
    )