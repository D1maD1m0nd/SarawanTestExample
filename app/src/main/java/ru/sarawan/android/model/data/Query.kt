package ru.sarawan.android.model.data

import ru.sarawan.android.utils.constants.SortBy

sealed interface Query {

    sealed interface Get : Query {

        data class Products(
            val id: Long? = null,
            val productName: String? = null,
            var page: Int = 1,
            val discountProduct: Boolean? = null,
            val popularProducts: Boolean? = null,
            val similarProducts: Boolean? = null,
            val categoryFilter: Int? = null,
            val sortBy: SortBy? = null,
        ) : Get

        data class ProductByID(val id: Long) : Get

        sealed interface Users : Get {
            data class UserData(val id: Long) : Users
        }

        sealed interface Orders : Get {
            data class Order(val address: AddressItem) : Orders
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
            data class NewUser(val user: UserRegistration) : User
            data class Sms(val user: UserRegistration) : User
        }

        sealed interface Address : Post {
            data class NewAddress(val address: AddressItem) : Address
        }
    }

    sealed interface Put : Query {

        sealed interface Basket : Put {
            data class Update(val id: Int, val products: ProductsUpdate) : Basket
        }

        sealed interface Users : Put {
            data class Update(val id: Long, val user: UserDataModel) : Users
        }
    }

    sealed interface Delete : Query {

        sealed interface Basket : Delete {
            data class Remove(val id: Int) : Basket
            object Clear : Basket
        }

        sealed interface Order : Delete {
            data class Delete(val id: Int) : Order
        }
    }
}