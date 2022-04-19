package ru.sarawan.android.model.data.basket

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import ru.sarawan.android.model.data.delegatesModel.BasketListItem
import ru.sarawan.android.model.data.product.Product
import java.util.*

@Parcelize
data class ProductsItem(
    @field:Json(name = "product") val basketProduct: BasketProduct? = null,
    @field:Json(name = "quantity") var quantity: Int? = null,
    @field:Json(name = "basket_product_id") val basketProductId: Int? = null
) : Parcelable, BasketListItem {
    override val id: Long
        get() = Random().nextLong()
}

fun ProductsItem.toProduct() =
    Product(
        id = basketProduct?.basketProduct?.id,
        images = basketProduct?.basketProduct?.images,
        product = basketProduct?.productStoreId,
        name = basketProduct?.basketProduct?.name,
        priceType = basketProduct?.basketProduct?.priceType,
        storePrices = basketProduct?.basketProduct?.storePrices?.toMutableList(),
        unitQuantity = basketProduct?.basketProduct?.unitQuantity,
        quantity = quantity ?: 0
    )