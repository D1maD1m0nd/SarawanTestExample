package com.example.sarawan.model.data

sealed interface Query {

    sealed interface Get : Query {

        sealed interface Products : Get {
            data class ProductName(val productName: String, val page: Int = 1) : Products
            data class DiscountProducts(val page: Int = 1) : Products
            data class PopularProducts(val page: Int = 1) : Products
            data class Id(val id: Long) : Products
            data class SimilarProducts(val id : Long) : Products
        }

        object Basket : Get

        object Category : Get
    }

    sealed interface Post : Query {

        sealed interface Basket : Post {
            data class Put(val products: ProductsUpdate) : Basket
        }

        sealed interface User : Post {
            data class NewUser(val user : UserRegistration) : User
            data class Sms(val user : UserRegistration) : User
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