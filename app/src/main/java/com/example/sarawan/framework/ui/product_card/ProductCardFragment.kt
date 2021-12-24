package com.example.sarawan.framework.ui.product_card

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
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
import com.example.sarawan.model.data.*
import com.example.sarawan.utils.TypeCardEnum
import com.example.sarawan.utils.exstentions.toProductShortItem
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class ProductCardFragment : Fragment() {
    private val itemClickListener = object : ItemClickListener {

        override fun openProductCard(productId: Long) {
            showProductFragment(productId)
        }

        override fun update(pos: Int, mode: Boolean, type: TypeCardEnum) {
            when(type) {
                TypeCardEnum.SIMILAR -> updateDataBasket(pos, mode)
                TypeCardEnum.STORE -> updateDataBasketIntoStore(pos, mode)
                TypeCardEnum.DEFAULT -> Unit
            }
        }

        override fun create(product: Product, pos: Int, type: TypeCardEnum) {
            itemSave(product, pos, true, type)
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
    private val storeAdapter  = StoreAdapter(itemClickListener)
    private val similarProducts : MutableList<Product> = ArrayList(20)
    private var storeProducts : MutableList<StorePrice> = ArrayList(5)
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
            else -> {}
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
        addBasketButton.setOnClickListener {
            itemSave(data, 0, true, TypeCardEnum.DEFAULT)
        }
        data.storePrices?.let {
            priceTextView.text = it.first().price.toString()
            storeTextView.text = it.first().store
            storeProducts = it
            storeAdapter.setData(storeProducts)
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
        itemSave(product, pos, false, TypeCardEnum.SIMILAR)
    }

    private fun updateDataBasketIntoStore(pos : Int, mode : Boolean) {
        val store = storeProducts[pos]
        when(mode) {
            true -> store.count++
            false -> store.count--
        }
        val product = Product(count = store.count, storePrices = mutableListOf(store))
        itemSave(product , pos, false, TypeCardEnum.STORE)
    }

    private fun itemSave(product : Product, pos:Int, isNew : Boolean, type: TypeCardEnum){

        viewModel.saveData(
            listOf(product.toProductShortItem()),
            isOnline = true,
            isNewItem = isNew
        )
        when(type) {
            TypeCardEnum.SIMILAR -> {
                similarProducts[pos] = product
                similarAdapter.notifyItemChanged(pos)
            }
            TypeCardEnum.STORE -> {
                storeProducts[pos].count = product.count
                storeAdapter.notifyItemChanged(pos)
            }
            TypeCardEnum.DEFAULT -> {
                binding.addBasketButton.visibility = GONE
            }
        }
    }

    private fun showProductFragment(idProduct: Long) {
        val bundle = Bundle()
        bundle.putLong(BasketFragment.PRODUCT_ID, idProduct)
        navController.navigate(R.id.action_productCardFragment_self, bundle)
    }

    companion object {
        fun newInstance() = ProductCardFragment()
    }
}