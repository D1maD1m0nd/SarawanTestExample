package ru.sarawan.android.framework.ui.catalog

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import ru.sarawan.android.framework.ui.catalog.adapter.CatalogRecyclerAdapter
import ru.sarawan.android.framework.ui.catalog.viewModel.CatalogViewModel
import ru.sarawan.android.model.data.*
import ru.sarawan.android.utils.exstentions.token

class CatalogFragment : BaseMainCatalogFragment() {

    private var catalogAdapter: CatalogRecyclerAdapter? = null
    private val categories: MutableList<CategoryDataModel> = mutableListOf()

    override val viewModel: CatalogViewModel by lazy {
        viewModelFactory.create(CatalogViewModel::class.java)
    }

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private val onCatalogItemClickListener: CatalogRecyclerAdapter.OnListItemClickListener =
        object : CatalogRecyclerAdapter.OnListItemClickListener {
            override fun onItemClick(data: CardScreenDataModel) {
                if (isOnline) {
                    val categoryType = data.id?.toInt() ?: -1
                    var filterArray: Array<Filter>? = null
                    if (categoryType != -1) {
                        filterArray = categories
                            .find { it.id == data.id }
                            ?.categories
                            ?.map { it.toFilter() }
                            ?.toTypedArray()
                    }
                    val action = CatalogFragmentDirections.actionCatalogFragmentToCategoryFragment(
                        categoryName = data.itemDescription.orEmpty(),
                        categoryType = categoryType,
                        filterList = filterArray
                    )
                    findNavController().navigate(action)
                } else handleNetworkErrorWithToast()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initCatalogRecyclerAdapter()
        if (isOnline) {
            if (!isInitCompleted) viewModel.getStartData(
                isOnline,
                !sharedPreferences.token.isNullOrEmpty()
            )
        } else handleNetworkErrorWithLayout()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun refresh() {
        if (isOnline) {
            viewModel.getStartData(isOnline, !sharedPreferences.token.isNullOrEmpty())
            binding.noConnectionLayout.root.visibility = View.GONE
            fabChanger?.changeState()
        }
    }

    override fun attachAdapterToView() = Unit

    private fun initCatalogRecyclerAdapter() {
        if (catalogAdapter == null) {
            catalogAdapter = CatalogRecyclerAdapter(onCatalogItemClickListener)
        } else catalogAdapter?.clear()
    }

    override fun subscribeToViewModel() = with(binding) {
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Success<*> -> {
                    val listData = appState.data as List<*>?
                    if (listData.isNullOrEmpty()) loadingLayout.visibility = View.GONE
                    else when (listData.first()) {
                        is MainScreenDataModel -> {
                            if (mainRecyclerAdapter != null && mainRecyclerAdapter!!.itemCount > 0) {
                                if (isInitCompleted) handleSuccessData(listData.first() as MainScreenDataModel)
                                else binding.loadingLayout.visibility = View.GONE
                            } else handleSuccessData(listData.first() as MainScreenDataModel)
                            isInitCompleted = true
                        }
                        is CategoryDataModel -> {
                            categories.clear()
                            categories.addAll(listData as List<CategoryDataModel>)
                            mainRecyclerView.layoutManager = linearLayoutManager
                            mainRecyclerView.adapter = catalogAdapter
                            val result: MutableList<CardScreenDataModel> = mutableListOf()
                            listData.forEach {
                                result.add(it.toMainScreenDataModel())
                            }
                            catalogAdapter?.setData(result) { loadingLayout.visibility = View.GONE }
                            isDataLoaded = true
                        }
                    }
                }
                is AppState.Error -> handleNetworkErrorWithToast(appState.error)
                AppState.Loading -> binding.loadingLayout.visibility = View.VISIBLE
                AppState.Empty -> Unit
            }
        }
    }

    private fun handleSuccessData(data: MainScreenDataModel) = with(binding) {
        fillChips(data.filters)
        if (data.listOfElements.isNullOrEmpty()) {
            loadingLayout.visibility = View.GONE
            emptyDataLayout.root.visibility = View.VISIBLE
        } else {
            emptyDataLayout.root.visibility = View.GONE
            mainRecyclerView.layoutManager = gridLayoutManager
            mainRecyclerView.adapter = mainRecyclerAdapter
            maxCount = data.maxElement
            mainRecyclerAdapter?.setData(
                data.listOfElements,
                searchField.editText?.text.isNullOrEmpty(),
                maxCount
            )
            isDataLoaded = true
        }
    }
}