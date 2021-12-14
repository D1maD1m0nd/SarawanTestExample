package com.example.sarawan.framework.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sarawan.R
import com.example.sarawan.app.App.Companion.navController
import com.example.sarawan.databinding.FragmentBasketBinding
import com.example.sarawan.framework.ui.basket.adapter.BasketAdapter
import com.example.sarawan.framework.ui.basket.viewModel.BasketViewModel
import com.example.sarawan.framework.ui.modals.DeliveryTimeFragment
import com.example.sarawan.framework.ui.modals.PaymentMethodFragment
import com.example.sarawan.framework.ui.profile.address_fragment.ProfileAddressFragment
import com.example.sarawan.model.data.*
import com.example.sarawan.model.data.delegatesModel.BasketFooter
import com.example.sarawan.model.data.delegatesModel.BasketHeader
import com.example.sarawan.model.data.delegatesModel.BasketListItem
import com.example.sarawan.utils.ItemClickListener
import com.example.sarawan.utils.toProductShortItem
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class BasketFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!
    private var addressItem : AddressItem? = null
    //основной список для отображения
    private val list: MutableList<BasketListItem> = ArrayList(
        listOf(
            BasketHeader(),
            BasketFooter(),
        )
    )

    private val itemClickListener = object : ItemClickListener {
        override fun showModal(fragment: DialogFragment) {
            showModalDialog(fragment)
        }

        override fun update(pos: Int, mode: Boolean) {
            updateBasket()
        }

        override fun deleteItem(basketId: Int, pos: Int, item: BasketListItem) {
            list.remove(item)
            deleteBasketItem(pos, basketId)
        }

        override fun openProductCard(productId: Int) {
            showProductFragment(productId)
        }

        override fun changeVisible(pos: Int) {
            TODO("Not yet implemented")
        }

        override fun create() {
            addressItem?.let {
                viewModel.createOrder(it)
            }

        }

        override fun clear() {
            viewModel.clearBasket()
            removeItems()
        }
    }
    private val adapter = BasketAdapter(itemClickListener)
    private val viewModel: BasketViewModel by lazy {
        viewModelFactory.create(BasketViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasketBinding.inflate(inflater, container, false)
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState: AppState<*> ->
            setState(appState)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        viewModel.getBasket()
        viewModel.getAddress()
    }

    private fun initRcView() = with(binding) {
        cardContainerRcView.itemAnimator?.changeDuration = 0
        cardContainerRcView.layoutManager = LinearLayoutManager(context)
        cardContainerRcView.adapter = adapter
    }

    private fun setState(appState: AppState<*>) = with(binding){
        when (appState) {
            is AppState.Success<*> -> {
                val data = appState.data
                if (data.isNotEmpty()) {
                    when (val item = data.first()) {
                        is ProductsItem -> {
                            data as MutableList<ProductsItem>
                            if (list.count() < LIMIT) {
                                initDataRcView(data)
                            }
                        }
                        is AddressItem -> {

                            addressItem = AddressItem(idAddressOrder = item.id)
                            addressItem?.let {
                                viewModel.getOrder(it)
                            }
                            val footer = (list.last() as BasketFooter)
                            footer.apply {
                                address = formatAddress(item)
                            }
                        }

                        is Order -> {
                            setFooterData(item)
                            setHeaderData(item)
                        }
                    }
                }
            }
            is AppState.Error -> Unit
            is AppState.Loading -> Unit
            is AppState.Empty ->  {
                infoBasketTextView.text = getString(R.string.basket_empty)
                actionBasketTextView.text = getString(R.string.nav_sales)
                actionBasketTextView.setOnClickListener { navController.navigate(R.id.mainFragment)}
            }
        }
    }

    private fun formatAddress(address: AddressItem): String {
        val city = address.city
        val street = address.street
        val house = address.house
        val roomNum = address.roomNumber
        return "$city, ул $street, д $house, кв $roomNum"
    }

    private fun initDataRcView(data: List<ProductsItem>) {
        list.addAll(list.lastIndex, data)
        adapter.items = list
    }

    private fun setFooterData(order: Order) {
        val sumOrder = order.basketSum!! + order.deliveryAmount!! + order.paymentAmount!!
        val footer = (list.last() as BasketFooter)
        footer.apply {
            price = order.basketSum
            deliveryPrice = order.deliveryAmount
            resultPrice = sumOrder
        }
        adapter.updateFooter()
    }

    private fun setHeaderData(order: Order) {
        val header = (list.first() as BasketHeader)
        header.counter = order.basketCount!!
        adapter.updateHeader()
    }


    private fun showModalDialog(fragment: DialogFragment) {
        when (fragment) {
            is DeliveryTimeFragment -> fragment.show(childFragmentManager, null)
            is ProfileAddressFragment -> fragment.show(childFragmentManager, null)
            is PaymentMethodFragment -> fragment.show(childFragmentManager, null)
            else -> Unit
        }
    }

    private fun deleteBasketItem(pos: Int, basketItemId: Int) {
        adapter.apply {
            items = list
            notifyItemRemoved(pos)
            updateHolders()
        }
        viewModel.deleteBasketProduct(basketItemId)
    }

    private fun updateBasket() {
        val products = adapter.items.filterIsInstance<ProductsItem>()
        val items = products.map {
            it.toProductShortItem()
        }

        viewModel.updateBasket(ProductsUpdate(items))
        adapter.updateHolders()
    }

    private fun removeItems() {
        val oldSize = list.count { it is ProductsItem }
        list.removeAll { it is ProductsItem }
        adapter.items = list
        adapter.notifyItemRangeRemoved(1, oldSize)
    }

    private fun showProductFragment(idProduct: Int) {
        val bundle = Bundle()
        val products = adapter.items.filterIsInstance<ProductsItem>()
        bundle.putLong(PRODUCT_ID, idProduct.toLong())
        bundle.putParcelableArrayList(PRODUCTS_BASKET, ArrayList(products))
        navController.navigate(R.id.action_basketFragment_to_productCardFragment, bundle)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = BasketFragment()
        const val PRODUCT_ID = "PRODUCT_ID"
        const val PRODUCTS_BASKET = "PRODUCTS_BASKET"
        private const val LIMIT = 3
    }
}