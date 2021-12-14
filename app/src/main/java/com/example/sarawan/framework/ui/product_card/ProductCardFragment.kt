package com.example.sarawan.framework.ui.product_card

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.sarawan.R
import com.example.sarawan.app.App.Companion.navController
import com.example.sarawan.databinding.FragmentProductCardBinding
import com.example.sarawan.framework.ui.basket.BasketFragment
import com.example.sarawan.framework.ui.product_card.adapter.ItemClickListener
import com.example.sarawan.framework.ui.product_card.adapter.SimilarAdapter
import com.example.sarawan.framework.ui.product_card.adapter.StoreAdapter
import com.example.sarawan.framework.ui.product_card.viewModel.ProductCardViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Product
import com.example.sarawan.utils.toProductShortItem
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class ProductCardFragment : Fragment() {
    private val itemClickListener = object : ItemClickListener {

        override fun openProductCard(productId: Int) {
            TODO("Not yet implemented")
        }

        override fun update(pos: Int, mode: Boolean) {
            updateDataBasket(pos, mode)
        }

        override fun create(product: Product, pos: Int) {
            itemSave(product, pos, true)
        }
    }
    @Inject
    lateinit var sharedPreferences: SharedPreferences
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
        arguments?.apply {
            productId = getLong(BasketFragment.PRODUCT_ID, 0)
        }
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
        viewModel.similarProducts(productId)
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
        data.images?.let {
            if(it.isNotEmpty()) {
                val url = it.first().image
                mainImageProductImageView.load(url) {
                    placeholder(R.drawable.card_placeholder)
                    error(R.drawable.card_placeholder)
                }
            }
        }
    }
    private fun updateDataBasket(pos : Int, mode : Boolean) {
        val product = similarProducts[pos]
        when(mode) {
            true -> product.count++
            false -> product.count--
        }
        itemSave(product, pos, false)
        similarAdapter.notifyItemChanged(pos)
    }

    private fun itemSave(product : Product, pos:Int, isNew : Boolean){
        similarProducts[pos] = product
        viewModel.saveData(
            listOf(product.toProductShortItem()),
            isOnline = true,
            isNewItem = isNew
        )
        similarAdapter.notifyItemChanged(pos)
    }

    companion object {
        fun newInstance() = ProductCardFragment()
    }
}