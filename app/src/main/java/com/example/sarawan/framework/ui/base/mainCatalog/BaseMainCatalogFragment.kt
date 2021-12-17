package com.example.sarawan.framework.ui.base.mainCatalog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.example.sarawan.R
import com.example.sarawan.activity.FabChanger
import com.example.sarawan.activity.MainActivity
import com.example.sarawan.app.App
import com.example.sarawan.databinding.FragmentMainBinding
import com.example.sarawan.framework.INavigation
import com.example.sarawan.framework.ui.basket.BasketFragment
import com.example.sarawan.framework.ui.main.adapter.MainRecyclerAdapter
import com.example.sarawan.model.data.MainScreenDataModel
import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.utils.NetworkStatus
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseMainCatalogFragment : Fragment(), INavigation {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var schedulerProvider: ISchedulerProvider

    @Inject
    lateinit var networkStatus: NetworkStatus

    @Inject
    lateinit var imageLoader: ImageLoader

    private var _binding: FragmentMainBinding? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: BaseMainCatalogViewModel

    protected var isOnline = true

    protected var maxCount = 0

    protected var isDataLoaded = false

    protected var mainRecyclerAdapter: MainRecyclerAdapter? = null

    private var fabChanger: FabChanger? = null

    protected val gridLayoutManager: GridLayoutManager by lazy {
        GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
    }

    private val onListItemClickListener: BaseMainCatalogAdapter.OnListItemClickListener =
        object : BaseMainCatalogAdapter.OnListItemClickListener {
            override fun onItemPriceChangeClick(
                data: MainScreenDataModel,
                diff: Int,
                isNewItem: Boolean
            ) {
                data.price?.let {
                    if (isOnline) {
                        fabChanger?.changePrice(it * diff)
                        viewModel.saveData(data, isOnline, isNewItem)
                    }
                }
            }

            override fun onItemClick(data: MainScreenDataModel) {
                val bundle = Bundle()
                bundle.putLong(BasketFragment.PRODUCT_ID, data.id ?: -1)
                App.navController.navigate(R.id.productCardFragment, bundle)
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
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkOnlineStatus()
        initRecyclerAdapter()
        attachAdapterToView()
        initSearchField()
        subscribeToViewModel()
        watchForAdapter()
    }

    protected fun watchForAdapter(recyclerView: RecyclerView = binding.mainRecyclerView) {
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
                        && isOnline
                    ) {
                        isDataLoaded = false
                        viewModel.getMoreData(isOnline) { /*TODO handle error loading data */ }
                    }
                }
            }
        })
    }

    protected abstract fun attachAdapterToView()

    protected abstract fun subscribeToViewModel()

    private fun initRecyclerAdapter() {
        if (mainRecyclerAdapter == null) {
            mainRecyclerAdapter = MainRecyclerAdapter(onListItemClickListener, imageLoader) {
                binding.loadingLayout.visibility = View.GONE
            }
        } else mainRecyclerAdapter?.clear()

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
        binding.micButton.setOnClickListener {
            Thread {
                Thread.sleep(3000)
                binding.root.post {
                    if (binding.searchField.editText?.text.toString().isNotEmpty()) makeSearch()
                }
            }.run()
        }
    }

    private fun initSearchButton() {
        binding.searchButton.setOnClickListener {
            if (binding.searchField.editText?.text.toString().isNotEmpty()) makeSearch()
        }
    }

    private fun initSearchInput() {

        binding.searchField.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.clearText.visibility = View.VISIBLE
//                binding.micButton.visibility = View.INVISIBLE
            } else {
                binding.clearText.visibility = View.INVISIBLE
//                binding.micButton.visibility = View.VISIBLE
            }
        }

        binding.searchField.editText?.doOnTextChanged { _, _, _, count ->
            if (count > 0) {
                binding.clearText.visibility = View.VISIBLE
//                binding.micButton.visibility = View.INVISIBLE
            } else {
                binding.clearText.visibility = View.INVISIBLE
//                binding.micButton.visibility = View.VISIBLE
            }
        }

        binding.clearText.setOnClickListener {
            binding.searchField.editText?.setText("")
        }

        binding.searchField.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && binding.searchField.editText?.text.toString()
                    .isNotEmpty()
            ) {
                makeSearch()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun makeSearch() {
        activity?.getSystemService<InputMethodManager>()
            ?.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        binding.searchField.clearFocus()
        if (!isOnline) Toast.makeText(
            context,
            "You are Offline! Get Results from Cache",
            Toast.LENGTH_SHORT
        ).show()
        else {
            viewModel.search(
                binding.searchField.editText?.text.toString(),
                isOnline
            ) { /*TODO handle error loading data */ }

            mainRecyclerAdapter?.clear()
            binding.topBarLayout.setExpanded(true, true)
            binding.mainRecyclerView.scrollToPosition(0)
        }
    }

    private fun checkOnlineStatus() {
        networkStatus
            .isOnline()
            .observeOn(schedulerProvider.io)
            .subscribeOn(schedulerProvider.io)
            .subscribe { isOnline ->
                this.isOnline = isOnline
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mainRecyclerView.layoutManager = null
        binding.mainRecyclerView.adapter = null
        viewModel.clear()
    }

    override fun onDestroy() {
        _binding = null
        (activity as MainActivity).setSupportActionBar(null)
        super.onDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fabChanger = context as FabChanger
    }

    override fun onDetach() {
        fabChanger = null
        super.onDetach()
    }

    override fun onFragmentBackStack() {
        App.navController.popBackStack()
    }
}