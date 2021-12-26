package com.example.sarawan.model.data

import com.example.sarawan.utils.SortBy

sealed interface Query {

    sealed interface Get : Query {

        sealed interface Products : Get {
            var page: Int
            data class ProductName(val productName: String, override var page: Int = 1) : Products
            data class ProductCategory(val productCategory: Int, override var page: Int = 1, val sortBy: SortBy = SortBy.PRICE_ASC) : Products
            data class DiscountProducts(override var page: Int = 1, val sortBy: SortBy = SortBy.DISCOUNT) : Products
            data class PopularProducts(override var page: Int = 1) : Products
            data class Id(val id: Long, override var page: Int = 1) : Products
            data class SimilarProducts(val id : Long, override var page: Int = 1) : Products
        }
        sealed interface Users : Get {
            data class UserData(val id : Long) : Users
        }

        sealed interface Orders : Get {
            data class Order(val address : AddressItem) : Orders
        }
        object Basket : Get

        object Category : Get

        object Address : Get

        object OrdersApproves : Get
    }

    sealed interface Post : Query {

        sealed interface Basket : Post {
            data class Put(val products: ProductsUpdate) : Basket
        }

        sealed interface Order : Post {
            data class Create(val address: AddressItem) : Basket
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
            object Clear :Basket
        }
    }
}