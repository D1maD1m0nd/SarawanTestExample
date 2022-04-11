package ru.sarawan.android.framework.ui.basket

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import retrofit2.HttpException
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentBasketBinding
import ru.sarawan.android.framework.ui.basket.adapter.BasketAdapter
import ru.sarawan.android.framework.ui.basket.viewModel.BasketViewModel
import ru.sarawan.android.model.data.*
import ru.sarawan.android.model.data.delegatesModel.BasketFooter
import ru.sarawan.android.model.data.delegatesModel.BasketHeader
import ru.sarawan.android.model.data.delegatesModel.BasketListItem
import ru.sarawan.android.utils.ItemClickListener
import ru.sarawan.android.utils.exstentions.localstore.UNREGISTERED
import ru.sarawan.android.utils.exstentions.localstore.token
import ru.sarawan.android.utils.exstentions.localstore.userId
import javax.inject.Inject

class BasketFragment : Fragment() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>
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
        override fun update(pos: Int, mode: Boolean) {
            updateBasket(pos)
        }

        override fun deleteItem(productsItem: ProductsItem, pos: Int, item: BasketListItem) {
            list.remove(item)
            deleteBasketItem(pos, productsItem)
        }

        override fun openProductCard(productId: Int) {
            showProductFragment(productId)
        }

        override fun openOrderCard() {
            if (sharedPreferences.token != null) {
                val action = BasketFragmentDirections.actionBasketFragmentToOrderFragment()
                findNavController().navigate(action)
            } else {
                showProfile()
            }

        }

        override fun clear() {
            viewModel.clearBasket(!sharedPreferences.token.isNullOrEmpty())
            removeItems()
        }
    }
    private val adapter = BasketAdapter(itemClickListener)
    private val viewModel: BasketViewModel by lazy {
        viewModelFactory.get().create(BasketViewModel::class.java)
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

    private fun showProfile(): Boolean =
        if (sharedPreferences.userId == UNREGISTERED) {
            val action = BasketFragmentDirections.actionBasketFragmentToProfilePhoneDialogFragment()
            findNavController().navigate(action)
            false
        } else {
            navigateToProfile()
            true
        }

    private fun navigateToProfile() {
        val action = BasketFragmentDirections.actionBasketFragmentToProfileFragment()
        findNavController().navigate(action)
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
                when (appState.data) {
                    is Basket -> viewModel.calculateOrder()
                    is List<*> -> {
                        when {
                            appState.data.isEmpty() -> Toast.makeText(
                                context,
                                getString(R.string.server_data_error),
                                Toast.LENGTH_SHORT
                            ).show()
                            appState.data.first() is ProductsItem -> {
                                @Suppress("UNCHECKED_CAST")
                                appState.data as List<ProductsItem>
                                if (list.size < LIMIT) initDataRcView(appState.data)
                                progressBar.visibility = View.GONE
                            }
                            else -> throw RuntimeException("Wrong List type ${appState.data}")
                        }
                    }
                    is Order -> recalculateData(appState.data)
                    else -> throw RuntimeException("Wrong AppState type $appState")
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

            is AppState.Empty -> emptyStatus()
        }
    }

    private fun emptyStatus() = with(binding) {
        progressBar.visibility = View.GONE
        infoBasketTextView.text = getString(R.string.basket_empty)
        actionBasketTextView.text = getString(R.string.nav_sales)
        actionBasketTextView.setOnClickListener {
            val action = BasketFragmentDirections.actionBasketFragmentToMainFragment()
            findNavController().navigate(action)
        }
    }

    private fun initDataRcView(data: List<ProductsItem>) {
        val start = list.size - 1
        val end = data.size
        list.addAll(start, data)
        adapter.items = list
        adapter.notifyItemRangeInserted(start, end)
        viewModel.calculateOrder()
    }

    private fun setFooterData(order: Order) {
        val footer = (list.last() as BasketFooter)
        footer.apply {
            price = order.basketSumm
            weight = order.weight

        }
        adapter.updateFooter()
    }

    private fun setHeaderData(order: Order) {
        val header = (list.first() as BasketHeader)
        header.counter = order.basketCount
        adapter.updateHeader()
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
                updateHolders()
                viewModel.calculateOrder()
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

    private fun updateBasket(pos: Int) {
        val product = adapter.items[pos] as ProductsItem
        val products = listOf(product)
        val items = products.map { it.toProduct() }
        viewModel.updateBasket(ProductsUpdate(items), !sharedPreferences.token.isNullOrEmpty())
        adapter.updateHolders()
    }

    private fun recalculateData(order: Order) {
        val items: List<ProductsItem> = list.filterIsInstance<ProductsItem>()
        if (items.isNotEmpty()) {
            setFooterData(order)
            setHeaderData(order)
        }
    }

    private fun showProductFragment(idProduct: Int) {
        val action = BasketFragmentDirections
            .actionBasketFragmentToProductCardFragment(idProduct.toLong())
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val LIMIT = 3
    }
}

