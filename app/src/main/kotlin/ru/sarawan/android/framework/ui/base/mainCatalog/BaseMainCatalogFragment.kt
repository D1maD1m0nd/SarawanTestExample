package ru.sarawan.android.framework.ui.base.mainCatalog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.google.android.material.chip.Chip
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import ru.sarawan.android.R
import ru.sarawan.android.activity.contracts.FabChanger
import ru.sarawan.android.databinding.FragmentMainBinding
import ru.sarawan.android.framework.ui.catalog.CatalogFragment
import ru.sarawan.android.framework.ui.catalog.CatalogFragmentDirections
import ru.sarawan.android.framework.ui.category.CategoryFragment
import ru.sarawan.android.framework.ui.category.CategoryFragmentDirections
import ru.sarawan.android.framework.ui.main.MainFragment
import ru.sarawan.android.framework.ui.main.MainFragmentDirections
import ru.sarawan.android.framework.ui.main.adapter.MainRecyclerAdapter
import ru.sarawan.android.framework.ui.main.viewHolder.ButtonMoreClickListener
import ru.sarawan.android.framework.ui.main.viewHolder.CardItemViewHolder
import ru.sarawan.android.framework.ui.product_card.ProductCardFragment
import ru.sarawan.android.model.data.CardScreenDataModel
import ru.sarawan.android.model.data.Filter
import ru.sarawan.android.model.data.Product
import ru.sarawan.android.model.data.toProduct
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.NetworkStatus
import ru.sarawan.android.utils.StringProvider
import ru.sarawan.android.utils.exstentions.getNavigationResult
import ru.sarawan.android.utils.exstentions.localstore.token
import java.util.*
import javax.inject.Inject

abstract class BaseMainCatalogFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var stringProvider: StringProvider

    private var _binding: FragmentMainBinding? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: BaseMainCatalogViewModel
    protected var isOnline = false
    protected var isDataLoaded = false
    protected var isInitCompleted = false
    protected var mainRecyclerAdapter: MainRecyclerAdapter? = null
    protected var fabChanger: FabChanger? = null
    protected val gridLayoutManager: GridLayoutManager by lazy {
        GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
    }

    private var wordToSearch: String? = null
    private var prevWordToSearch: String? = null
    private var filterCategory: Int? = null
    protected var filterSubcategory: Int? = null
    protected var isFilterSubcategory = false
    private val filterList: MutableList<Filter> = mutableListOf()

    private val getTextFromVoice =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val spokenText: String? =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        .let { it?.get(0) }
                binding.searchField.editText?.setText(spokenText)
                wordToSearch = binding.searchField.editText?.text.toString()
                makeSearch()
            }
        }

    private val onListItemClickListener: BaseMainCatalogAdapter.OnListItemClickListener =
        object : BaseMainCatalogAdapter.OnListItemClickListener {
            override fun onItemPriceChangeClick(
                data: CardScreenDataModel,
                diff: Int,
                isNewItem: Boolean,
                callback: (isOnline: Boolean) -> Unit
            ) {
                data.price?.let {
                    if (isOnline) {
                        callback(isOnline)
                        fabChanger?.changePrice(it * diff)
                        viewModel.saveData(
                            data,
                            !sharedPreferences.token.isNullOrEmpty(),
                            isNewItem
                        )
                        mainRecyclerAdapter?.changeProduct(data.toProduct())
                    } else handleNetworkErrorWithToast()
                }
            }

            override fun onItemClick(data: CardScreenDataModel) {
                if (isOnline) {
                    val action = when (this@BaseMainCatalogFragment) {
                        is MainFragment -> MainFragmentDirections
                            .actionMainFragmentToProductCardFragment(data.id ?: -1)
                        is CatalogFragment -> CatalogFragmentDirections
                            .actionCatalogFragmentToProductCardFragment(data.id ?: -1)
                        is CategoryFragment -> CategoryFragmentDirections
                            .actionCategoryFragmentToProductCardFragment(data.id ?: -1)
                        else -> throw RuntimeException("Wrong fragment $this")
                    }
                    findNavController().navigate(action)
                } else handleNetworkErrorWithToast()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMainBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isInitCompleted = false
        initRecyclerAdapter()
        attachAdapterToView()
        initSearchField()
        subscribeToViewModel()
        watchForAdapter()
        initRefreshButton()
        initFilterChips()
        handleProductCardResult()
    }

    private fun initFilterChips() = with(binding) {
        filterGroup.setOnCheckedChangeListener { _, checkedId ->
            val id = if (checkedId == -1) null else checkedId
            if (isFilterSubcategory) {
                filterSubcategory = id
                filterCategory = null
            } else {
                filterSubcategory = null
                filterCategory = if (id == -1) null
                else id
            }
            makeSearch()
        }
    }

    protected fun fillChips(filters: List<Filter>?) = with(binding) {
        when {
            filters.isNullOrEmpty() -> filtersBar.visibility = View.GONE

            !filterList.containsAll(filters) -> {
                filtersBar.visibility = View.VISIBLE
                filterList.clear()
                filterList.addAll(filters)
                filterGroup.removeAllViews()
                filters.forEach {
                    val chip = layoutInflater
                        .inflate(R.layout.filter_chip_layout, filterGroup, false) as Chip
                    chip.id = it.id.toInt()
                    chip.text = it.name
                    filterGroup.addView(chip)
                }
            }
        }
    }

    private fun handleProductCardResult() {
        getNavigationResult<ArrayList<Product?>>(ProductCardFragment.REQUEST_KEY) { products ->
            products.forEach { product ->
                if (product != null) mainRecyclerAdapter?.changeProduct(product)
            }
        }
    }

    private fun initRefreshButton() = binding
        .noConnectionLayout
        .refreshButton
        .setOnClickListener { refresh() }

    protected abstract fun refresh()

    private fun watchForAdapter(recyclerView: RecyclerView = binding.mainRecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                mainRecyclerAdapter?.let { adapter ->
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && isDataLoaded
                        && recyclerView.layoutManager is GridLayoutManager
                        && adapter.getItemViewType(
                            (recyclerView.layoutManager as GridLayoutManager)
                                .findLastVisibleItemPosition()
                        ) == CardType.LOADING.type
                    ) if (isOnline) {
                        isDataLoaded = false
                        viewModel.getMoreData(!sharedPreferences.token.isNullOrEmpty())
                    } else handleNetworkErrorWithToast()
                    adapter.changeLoadingAnimation(isOnline)
                }
            }
        })
    }

    protected abstract fun attachAdapterToView()

    protected abstract fun subscribeToViewModel()

    private fun initRecyclerAdapter() {
        val buttonMoreClickListener = ButtonMoreClickListener {
            if (isOnline) {
                val action = MainFragmentDirections.actionMainFragmentToCategoryFragment(
                    stringProvider.getString(R.string.sponsored_items),
                    CategoryFragment.DISCOUNT
                )
                findNavController().navigate(action)
            } else handleNetworkErrorWithToast()
        }
        if (mainRecyclerAdapter == null) {
            mainRecyclerAdapter = MainRecyclerAdapter(
                onListItemClickListener,
                imageLoader,
                stringProvider,
                buttonMoreClickListener,
                { item, position ->
                    val holder = binding.mainRecyclerView.findViewHolderForItemId(position)
                    if (holder != null && holder is CardItemViewHolder) holder.bind(item)
                },
                {
                    _binding?.loadingLayout?.visibility = View.GONE
                })
        }

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mainRecyclerAdapter!!.getItemViewType(position)) {
                    CardType.TOP.type -> 2
                    CardType.COMMON.type -> 1
                    CardType.STRING.type -> 2
                    CardType.BUTTON.type -> 2
                    CardType.LOADING.type -> 2
                    else -> -1
                }
            }
        }
    }

    private fun initSearchField() {
        initSearchInput()
        initSearchButton()
        initMicButton()
    }

    private fun initMicButton() {
        binding.micButton.setOnClickListener { displaySpeechRecognizer() }
    }

    private fun initSearchButton() = with(binding) {
        searchButton.setOnClickListener {
            if (isDataLoaded && searchField.editText?.text.toString().isNotEmpty()) {
                wordToSearch = searchField.editText?.text.toString()
                makeSearch()
            }
        }
    }

    private fun initSearchInput() = with(binding) {

        searchField.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearText.visibility = View.VISIBLE
                micButton.visibility = View.INVISIBLE
            } else {
                clearText.visibility = View.INVISIBLE
                micButton.visibility = View.VISIBLE
            }
        }

        searchField.editText?.doOnTextChanged { _, _, _, count ->
            if (count > 0) {
                clearText.visibility = View.VISIBLE
                micButton.visibility = View.INVISIBLE
            } else {
                clearText.visibility = View.INVISIBLE
                micButton.visibility = View.VISIBLE
            }
        }

        clearText.setOnClickListener { searchField.editText?.setText("") }

        searchField.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                && searchField.editText?.text.toString().isNotEmpty()
            ) {
                wordToSearch = searchField.editText?.text.toString()
                makeSearch()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun makeSearch() = with(binding) {
        activity?.getSystemService<InputMethodManager>()
            ?.hideSoftInputFromWindow(searchField.windowToken, 0)
        searchField.clearFocus()
        isDataLoaded = false
        if (prevWordToSearch != wordToSearch) {
            filterCategory = null
            filterSubcategory = null
            prevWordToSearch = wordToSearch
        }
        if (isOnline) {
            viewModel.search(
                wordToSearch,
                filterCategory,
                filterSubcategory,
                !sharedPreferences.token.isNullOrEmpty()
            )
            loadingLayout.visibility = View.VISIBLE
            mainRecyclerAdapter?.clear()
            filtersBar.visibility = View.VISIBLE
            topBarLayout.setExpanded(true, true)
            mainRecyclerView.scrollToPosition(0)
        } else handleNetworkErrorWithToast()
    }

    protected fun handleNetworkErrorWithToast(throwable: Throwable? = RuntimeException(getString(R.string.no_internet))) {
        Toast.makeText(context, throwable?.message, Toast.LENGTH_SHORT).show()
    }

    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_in_sarafan))
        }
        getTextFromVoice.launch(intent)
    }

    protected fun handleNetworkErrorWithLayout() {
        binding.noConnectionLayout.root.visibility = View.VISIBLE
        fabChanger?.changeState()
    }

    @Inject
    fun checkOnlineStatus(networkStatus: NetworkStatus, schedulerProvider: ISchedulerProvider) {
        networkStatus
            .isOnline()
            .observeOn(schedulerProvider.io)
            .subscribeOn(schedulerProvider.io)
            .subscribe { isOnline = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mainRecyclerView.layoutManager = null
        binding.mainRecyclerView.adapter = null
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fabChanger = context as FabChanger
    }

    override fun onDetach() {
        fabChanger = null
        super.onDetach()
    }
}