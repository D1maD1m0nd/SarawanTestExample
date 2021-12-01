package com.example.sarawan.model.data

sealed interface Query {

    sealed interface Get : Query {

        sealed interface Products : Get {
            data class ProductName(val productName: String) : Products
            object DiscountProducts : Products
            object PopularProducts : Products
            data class Page(val page: Int) : Products
            data class Id(val id: Long) : Products
        }

        object Basket : Get
    }

    sealed interface Post : Query {

        sealed interface Basket : Post {
            data class Put(val products: ProductsUpdate) : Basket
        }
    }

    sealed interface Put : Query {

        sealed interface Basket : Put {
            data class Update(val id: Int, val products: ProductsUpdate) : Basket
        }
    }

    sealed interface Delete : Query {
        sealed interface Basket : Delete {
            data class Remove(val id: Int) : Basket
        }
    }
}