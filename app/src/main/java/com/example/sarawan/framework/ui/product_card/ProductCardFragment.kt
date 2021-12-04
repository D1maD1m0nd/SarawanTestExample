package com.example.sarawan.framework.ui.product_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sarawan.app.App.Companion.navController
import com.example.sarawan.databinding.FragmentProductCardBinding
import com.example.sarawan.framework.ui.basket.BasketFragment
import com.example.sarawan.framework.ui.product_card.viewModel.ProductCardViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Product
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class ProductCardFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ProductCardViewModel by lazy {
        viewModelFactory.create(ProductCardViewModel::class.java)
    }
    private var _binding: FragmentProductCardBinding? = null
    private val binding get() = _binding!!
    private  var productId : Long? = null
    private var similarList : MutableList<Product> = ArrayList(20)
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
                val data = appState.data as List<Product>
                if(data.size > 1) {
                    similarList.addAll(data)
                } else {
                    val product = data.first()
                    initViewData(product)
                }
            }
            is AppState.Error -> Unit
            AppState.Loading -> Unit
        }
    }
    private fun initViewData(data : Product) = with(binding){
        titleTextView.text = data.name
        contentDescriptionTextView.text = data.description
        priceTextView.text = data.store_prices?.first()?.price.toString()
        storeTextView.text = data.store_prices?.first()?.store

    }
    companion object {
        fun newInstance() = ProductCardFragment()
    }
}