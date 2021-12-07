package com.example.sarawan.framework.ui.product_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sarawan.app.App.Companion.navController
import com.example.sarawan.databinding.FragmentProductCardBinding
import com.example.sarawan.framework.ui.basket.BasketFragment
import com.example.sarawan.framework.ui.product_card.adapter.SimilarAdapter
import com.example.sarawan.framework.ui.product_card.adapter.StoreAdapter
import com.example.sarawan.framework.ui.product_card.viewModel.ProductCardViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Product
import com.example.sarawan.model.data.delegatesModel.BasketListItem
import com.example.sarawan.utils.ItemClickListener
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class ProductCardFragment : Fragment() {
    private val itemClickListener = object : ItemClickListener {
        override fun showModal(fragment: DialogFragment) {
            TODO("Not yet implemented")
        }

        override fun update() {
            TODO("Not yet implemented")
        }
        override fun deleteItem(basketId: Int, pos: Int, item : BasketListItem) {
            TODO("Not yet implemented")
        }

        override fun openProductCard(productId : Int) {
            TODO("Not yet implemented")
        }

        override fun changeVisible(pos : Int) {
            changeVisibleFlag(pos)
        }
    }
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ProductCardViewModel by lazy {
        viewModelFactory.create(ProductCardViewModel::class.java)
    }
    private var _binding: FragmentProductCardBinding? = null
    private val binding get() = _binding!!
    private  var productId : Long? = null
    private val storeAdapter  = StoreAdapter()
    private val similarProducts : MutableList<Product> = ArrayList(20)
    private val similarAdapter = SimilarAdapter(itemClickListener)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = arguments?.getLong(BasketFragment.PRODUCT_ID, 0)
        AndroidSupportInjection.inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductCardBinding.inflate(inflater, container, false)
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState: AppState<*> ->
            setState(appState)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.productCloseButton.setOnClickListener {
            navController.popBackStack()
        }
        viewModel.getProduct(productId)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                val data = appState.data as MutableList<Product>
                if(data.size > 1) {
                    similarProducts.addAll(data)
                    initSimilarList(similarProducts)
                } else {
                    val product = data.first()
                    initViewData(product)
                }
            }
            is AppState.Error -> Unit
            AppState.Loading -> Unit
        }
    }

    private fun initSimilarList(data: MutableList<Product>) = with(binding){
        similarProductRecyclerView.adapter = similarAdapter
        similarProductRecyclerView.itemAnimator?.changeDuration = 0
        similarProductRecyclerView.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        similarAdapter.setData(data)
    }
    private fun initViewData(data : Product) = with(binding){
        containerStoreRecyclerView.layoutManager = LinearLayoutManager(root.context)
        containerStoreRecyclerView.adapter = storeAdapter
        containerStoreRecyclerView.itemAnimator?.changeDuration = 0
        titleTextView.text = data.name
        contentDescriptionTextView.text = data.description
        data.storePrices?.let {
            priceTextView.text = it.first().price
            storeTextView.text = it.first().store
            storeAdapter.setData(it)
        }
    }

    private fun changeVisibleFlag(pos : Int) {
        similarProducts[pos].visible = false
        similarAdapter.updateItem(similarProducts, pos)
    }
    companion object {
        fun newInstance() = ProductCardFragment()
    }
}