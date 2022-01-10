package ru.sarawan.android.framework.ui.product_card

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.android.support.AndroidSupportInjection
import ru.sarawan.android.R
import ru.sarawan.android.app.App.Companion.navController
import ru.sarawan.android.databinding.FragmentProductCardBinding
import ru.sarawan.android.framework.ui.basket.BasketFragment
import ru.sarawan.android.framework.ui.product_card.adapter.ItemClickListener
import ru.sarawan.android.framework.ui.product_card.adapter.SimilarAdapter
import ru.sarawan.android.framework.ui.product_card.adapter.StoreAdapter
import ru.sarawan.android.framework.ui.product_card.viewModel.ProductCardViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Product
import ru.sarawan.android.model.data.StorePrice
import ru.sarawan.android.utils.TypeCardEnum
import ru.sarawan.android.utils.exstentions.token
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
            product.storePrices?.first()?.let {
                fabChanger?.changePrice(it.price.toFloat())
            }
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
    private var fabChanger: ru.sarawan.android.activity.FabChanger? = null
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
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState: AppState<*> ->
            setState(appState)
        }
        _binding = FragmentProductCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.productCloseButton.setOnClickListener {
            navController.popBackStack()
        }
        viewModel.similarProducts(productId, !sharedPreferences.token.isNullOrEmpty())
    }
    private fun clearViewState() {
        viewModel.clear()
        binding.containerStoreRecyclerView.layoutManager = null
        binding.containerStoreRecyclerView.adapter = null
        binding.similarProductRecyclerView.layoutManager = null
        binding.similarProductRecyclerView.adapter = null
    }
    override fun onDestroyView() {
        clearViewState()
        _binding = null
        super.onDestroyView()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fabChanger = context as ru.sarawan.android.activity.FabChanger
    }

    override fun onDetach() {
        fabChanger = null
        super.onDetach()
    }

    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                val data = appState.data as MutableList<Product>
                val filteredData = data.filter { !it.storePrices.isNullOrEmpty() }
                val product = data.findLast { it.id == productId }
                similarProducts.addAll(data)
                if (filteredData.isNullOrEmpty() || (filteredData - product).isNullOrEmpty()) {
                    binding.semilarTitleTextView.visibility = View.GONE
                } else {
                    binding.semilarTitleTextView.visibility = View.VISIBLE
                    initSimilarList(similarProducts)
                }
                product?.let { initViewData(it) }
                binding.progressBar.visibility = View.GONE
                binding.contentNestedScrollView.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
            is AppState.Loading -> {
                binding.contentNestedScrollView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            else -> {}
        }
    }

    private fun initSimilarList(data: MutableList<Product>) = with(binding){
        similarProductRecyclerView.adapter = similarAdapter
        similarProductRecyclerView.itemAnimator?.changeDuration = 0
        similarProductRecyclerView.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        val product = data.findLast { it.id == productId }
        similarAdapter.setData(data.filter { !it.storePrices.isNullOrEmpty() && it.id != product?.id }.toMutableList())
    }
    private fun initViewData(data : Product) = with(binding){
        containerStoreRecyclerView.layoutManager = LinearLayoutManager(root.context)
        containerStoreRecyclerView.adapter = storeAdapter
        containerStoreRecyclerView.itemAnimator?.changeDuration = 0
        titleTextView.text = data.name
        contentDescriptionTextView.text = data.description
        addBasketButton.setOnClickListener {
            data.quantity = 1
            data.storePrices?.firstOrNull()?.count = 1
            itemSave(data, 0, true, TypeCardEnum.DEFAULT)
        }
        data.storePrices?.let {
            setButtonAddBasketVisible(it)
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
    private fun setButtonAddBasketVisible(stores : List<StorePrice>) {
        val isAddedBasket = stores.count { it.count > 0 } > 0
        if(isAddedBasket) {
            binding.addBasketButton.visibility = View.GONE
        } else {
            binding.addBasketButton.visibility = View.VISIBLE
        }
    }
    private fun updateDataBasket(pos : Int, mode : Boolean) {
        val product = similarProducts[pos]
        when(mode) {
            true -> {
                product.quantity++
                product.storePrices?.first()?.let {
                    fabChanger?.changePrice(it.price.toFloat())
                }
            }
            false -> {
                product.quantity--
                product.storePrices?.first()?.let {
                    fabChanger?.changePrice(it.price.toFloat() * -1)
                }
            }
        }
        itemSave(product, pos, false, TypeCardEnum.SIMILAR)
    }

    private fun updateDataBasketIntoStore(pos : Int, mode : Boolean) {
        val store = storeProducts[pos]
        when(mode) {
            true -> {
                store.count++
                store.let {
                    fabChanger?.changePrice(it.price.toFloat())
                }
            }
            false -> {
                store.count--
                store.let {
                    fabChanger?.changePrice(it.price.toFloat() * -1)
                }
            }
        }
        val product = similarProducts.find { it.id == productId }?.apply {
            storePrices?.find { it == store }?.apply { count = store.count }
            quantity = store.count
        } ?: return
        itemSave(product , pos, false, TypeCardEnum.STORE)
    }

    private fun itemSave(product : Product, pos:Int, isNew : Boolean, type: TypeCardEnum){

        viewModel.saveData(
            listOf(product),
            isNewItem = isNew,
            isLoggedUser = !sharedPreferences.token.isNullOrEmpty()
        )
        when(type) {
            TypeCardEnum.SIMILAR -> {
                similarProducts[pos] = product
                similarAdapter.itemUpdate(pos, similarProducts)
            }
            TypeCardEnum.STORE -> {
                storeProducts[pos].count = product.quantity
                storeAdapter.itemUpdate(pos,storeProducts)

            }
            TypeCardEnum.DEFAULT -> storeAdapter.notifyItemChanged(0)
        }
        product.storePrices?.let {
            setButtonAddBasketVisible(it)
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