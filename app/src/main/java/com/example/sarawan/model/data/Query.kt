package com.example.sarawan.model.data

sealed interface Query {

    sealed interface Get : Query {

        sealed interface Products : Get {
            data class ProductName(val productName: String, val page: Int = 1) : Products
            data class ProductCategory(val productCategory: Int, val page: Int = 1) : Products
            data class DiscountProducts(val page: Int = 1) : Products
            data class PopularProducts(val page: Int = 1) : Products
            data class Id(val id: Long) : Products
            data class SimilarProducts(val id : Long, val page: Int = 1) : Products
        }
        sealed interface Users : Get{
            data class UserData(val id : Long) : Users
        }
        object Basket : Get

        object Category : Get

        object Address : Get
    }

    sealed interface Post : Query {

        sealed interface Basket : Post {
            data class Put(val products: ProductsUpdate) : Basket
        }

        sealed interface User : Post {
            data class NewUser(val user : UserRegistration) : User
            data class Sms(val user : UserRegistration) : User
        }

        sealed interface Address : Post {
            data class NewAddress(val address : AddressItem) : Address
        }
    }

    sealed interface Put : Query {

        sealed interface Basket : Put {
            data class Update(val id: Int, val products: ProductsUpdate) : Basket
        }

        sealed interface Users : Put {
            data class Update(val id : Long, val user : UserDataModel) : Users
        }
    }

    sealed interface Delete : Query {
        sealed interface Basket : Delete {
            data class Remove(val id: Int) : Basket
        }
    }
}