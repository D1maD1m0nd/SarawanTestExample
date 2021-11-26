package com.example.sarawan.framework.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sarawan.databinding.FragmentBasketBinding
import com.example.sarawan.framework.ui.basket.adapter.BasketAdapter
import com.example.sarawan.framework.ui.basket.modals.DeliveryTimeFragment
import com.example.sarawan.framework.ui.basket.modals.PaymentMethodFragment
import com.example.sarawan.framework.ui.basket.viewModel.BasketViewModel
import com.example.sarawan.framework.ui.profile.ProfileAddressFragment
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.ProductsItem
import com.example.sarawan.model.data.ProductsUpdate
import com.example.sarawan.model.data.delegatesModel.BasketFooter
import com.example.sarawan.model.data.delegatesModel.BasketHeader
import com.example.sarawan.model.data.delegatesModel.BasketListItem
import com.example.sarawan.utils.toUpdateProduct
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class BasketFragment : Fragment() {
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

        override fun update() {
            updateBasket()
        }

        override fun deleteItem(item: BasketListItem, pos: Int) {
            list.remove(item)
            deleteItemRcView(pos)
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
    }

    private fun initRcView() = with(binding) {
        cardContainerRcView.itemAnimator?.changeDuration = 0;
        cardContainerRcView.layoutManager = LinearLayoutManager(context)
        cardContainerRcView.adapter = adapter
    }

    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                val data = appState.data as List<ProductsItem>
                initDataRcView(data)
            }
            is AppState.Error -> Unit
            AppState.Loading -> Unit
        }
    }

    private fun initDataRcView(data: List<ProductsItem>) {
        val countAdapter = list.size - 1
        setFooterData(data)
        setHeaderData(data)
        list.addAll(countAdapter, data)
        adapter.items = list
    }

    private fun setFooterData(data: List<ProductsItem>) {
        val footer = (list.last() as BasketFooter)
        footer.apply {
            weight = BasketAdapter.calculateWeight(data)
            price = BasketAdapter.calculateSum(data)
        }
    }

    private fun setHeaderData(data: List<ProductsItem>) {
        val header = (list.first() as BasketHeader)
        header.counter = data.size
    }


    private fun showModalDialog(fragment: DialogFragment) {
        when (fragment) {
            is DeliveryTimeFragment -> fragment.show(childFragmentManager, null)
            is ProfileAddressFragment -> fragment.show(childFragmentManager, null)
            is PaymentMethodFragment -> fragment.show(childFragmentManager, null)
            else -> Unit
        }
    }

    private fun deleteItemRcView(pos: Int) {
        adapter.apply {
            items = list
            notifyItemRemoved(pos)
            updateHolders()
        }
    }

    private fun updateBasket() {
        val products = adapter.items.filterIsInstance<ProductsItem>()
        val items = products.map {
            it.toUpdateProduct()
        }

        viewModel.updateBasket(ProductsUpdate(items))
        adapter.updateHolders()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = BasketFragment()
    }
}