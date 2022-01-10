package ru.sarawan.android.framework.ui.basket

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import dagger.android.support.AndroidSupportInjection
import retrofit2.HttpException
import ru.sarawan.android.R
import ru.sarawan.android.app.App.Companion.navController
import ru.sarawan.android.databinding.FragmentBasketBinding
import ru.sarawan.android.framework.ui.basket.adapter.BasketAdapter
import ru.sarawan.android.framework.ui.basket.viewModel.BasketViewModel
import ru.sarawan.android.framework.ui.modals.DeliveryTimeFragment
import ru.sarawan.android.framework.ui.modals.PaymentMethodFragment
import ru.sarawan.android.framework.ui.modals.SuccessOrderFragment
import ru.sarawan.android.framework.ui.profile.address_fragment.ProfileAddressFragment
import ru.sarawan.android.model.data.*
import ru.sarawan.android.model.data.delegatesModel.BasketFooter
import ru.sarawan.android.model.data.delegatesModel.BasketHeader
import ru.sarawan.android.model.data.delegatesModel.BasketListItem
import ru.sarawan.android.utils.ItemClickListener
import ru.sarawan.android.utils.exstentions.token
import javax.inject.Inject


class BasketFragment : Fragment() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!

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

        override fun deleteItem(productsItem: ProductsItem, pos: Int, item: BasketListItem) {
            list.remove(item)
            deleteBasketItem(pos, productsItem)
        }

        override fun openProductCard(productId: Int) {
            showProductFragment(productId)
        }

        override fun openOrderCard() {
            navController.navigate(R.id.action_basketFragment_to_orderFragment)
        }

        override fun clear() {
            viewModel.clearBasket(!sharedPreferences.token.isNullOrEmpty())
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
        initData()
    }

    private fun initData() {
        initRcView()
        viewModel.getBasket(!sharedPreferences.token.isNullOrEmpty())
    }

    private fun initRcView() = with(binding) {
        val mLayoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(
            cardContainerRcView.context,
            mLayoutManager.orientation
        )
        cardContainerRcView.addItemDecoration(dividerItemDecoration)
        cardContainerRcView.itemAnimator?.changeDuration = 0
        cardContainerRcView.layoutManager = LinearLayoutManager(context)
        cardContainerRcView.adapter = adapter
    }

    private fun setState(appState: AppState<*>) = with(binding) {
        when (appState) {
            is AppState.Success<*> -> {
                val data = appState.data
                if (data.isNotEmpty()) {
                    when (data.first()) {
                        is BasketResponse -> if (list.size >= LIMIT) {
                            this@BasketFragment.recalculateData()
                        }
                        is ProductsItem -> {
                            Log.d("TAG_PRODUCT_ITEM", "ProductsItem THIS")
                            data as MutableList<ProductsItem>
                            Log.d("TAG_PRODUCT_ITEM", "THIS")
                            if (list.size < LIMIT) {
                                initDataRcView(data)
                            }
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            }
            is AppState.Error -> {
                progressBar.visibility = View.GONE
                val error = appState.error
                if (error is HttpException) {
                    when (error.code()) {
                        500 -> Toast.makeText(
                            context,
                            getString(R.string.error_500),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            is AppState.Loading -> {
                progressBar.visibility = View.VISIBLE
            }
            is AppState.Empty -> {
                emptyStatus()
            }
        }
    }

    private fun emptyStatus() = with(binding) {
        progressBar.visibility = View.GONE
        infoBasketTextView.text = getString(R.string.basket_empty)
        actionBasketTextView.text = getString(R.string.nav_sales)
        actionBasketTextView.setOnClickListener { navController.navigate(R.id.mainFragment) }
    }

    private fun initDataRcView(data: List<ProductsItem>) {
        val start = list.size - 1
        val end = data.size
        list.addAll(start, data)
        adapter.items = list
        recalculateData()
        adapter.notifyItemRangeInserted(start, end)
    }

    private fun productItemsToOrder(data: List<ProductsItem>): Order {
        val count = data.sumOf { it.quantity ?: 0 }
        val weight = data.sumOf {
            it.basketProduct
                ?.basketProduct
                ?.unitQuantity
                ?.toDouble()
                ?.times(it.quantity!!) ?: 0.0
        }
        val price = data.sumOf {
            it.basketProduct
                ?.price
                ?.toDouble()
                ?.times(it.quantity!!) ?: 0.0
        }

        return Order(
            basketCount = count,
            paymentAmount = 0.0,
            deliveryAmount = 0.0,
            basketSumm = price,
            weight = weight
        )
    }

    private fun setFooterData(order: Order) {
        val footer = (list.last() as BasketFooter)
        footer.apply {
            price = order.basketSumm ?: 0.0
            weight = order.weight ?: 0.0

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
            is SuccessOrderFragment -> fragment.show(childFragmentManager, null)
        }
    }

    private fun deleteBasketItem(pos: Int, productsItem: ProductsItem) {
        val end = list.size + 1
        adapter.apply {
            if (list.size == 2) {
                list.clear()
                items = list
                notifyItemRangeRemoved(0, end)
                emptyStatus()
            } else {
                items = list
                notifyItemRemoved(pos)
                recalculateData()
                updateHolders()
            }
        }
        val productID = if (!sharedPreferences.token.isNullOrEmpty()) productsItem.basketProductId
        else productsItem.basketProduct?.basketProduct?.id?.toInt()
        viewModel.deleteBasketProduct(productID ?: -1, !sharedPreferences.token.isNullOrEmpty())
    }

    private fun removeItems() {
        val end = list.size
        list.clear()
        adapter.items = list
        adapter.notifyItemRangeRemoved(0, end)
        binding.progressBar.visibility = View.GONE
    }

    private fun updateBasket() {
        val products = adapter.items.filterIsInstance<ProductsItem>()
        val items = products.map { it.toProduct() }
        viewModel.updateBasket(ProductsUpdate(items), !sharedPreferences.token.isNullOrEmpty())
        adapter.updateHolders()
    }

    private fun recalculateData() {
        val items: List<ProductsItem> = list.filterIsInstance<ProductsItem>()
        if (items.isNotEmpty()) {
            val order = productItemsToOrder(items)
            setFooterData(order)
            setHeaderData(order)
        }
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
