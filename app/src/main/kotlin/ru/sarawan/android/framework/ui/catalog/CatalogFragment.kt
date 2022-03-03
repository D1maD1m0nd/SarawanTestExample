package ru.sarawan.android.framework.ui.catalog

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.sarawan.android.R
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import ru.sarawan.android.framework.ui.catalog.adapter.CatalogRecyclerAdapter
import ru.sarawan.android.framework.ui.catalog.viewModel.CatalogViewModel
import ru.sarawan.android.model.data.*
import ru.sarawan.android.utils.exstentions.token

class CatalogFragment : BaseMainCatalogFragment() {

    private var catalogAdapter: CatalogRecyclerAdapter? = null
    private val categories: MutableList<CategoryDataModel> = mutableListOf()

    override val viewModel: CatalogViewModel by lazy {
        viewModelFactory.get().create(CatalogViewModel::class.java)
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
                        subCategoryType = filterSubcategory ?: -1,
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
                    when (appState.data) {
                        is MainScreenDataModel -> {
                            if (mainRecyclerAdapter != null && mainRecyclerAdapter!!.itemCount > 0) {
                                if (isInitCompleted) handleSuccessData(appState.data)
                                else binding.loadingLayout.visibility = View.GONE
                            } else handleSuccessData(appState.data)
                            isInitCompleted = true
                        }
                        is List<*> -> {
                            when {
                                appState.data.isEmpty() -> handleNetworkErrorWithToast(
                                    RuntimeException(getString(R.string.server_data_error))
                                )
                                appState.data.first() is CategoryDataModel -> {
                                    categories.clear()
                                    @Suppress("UNCHECKED_CAST")
                                    categories.addAll(appState.data as List<CategoryDataModel>)
                                    mainRecyclerView.layoutManager = linearLayoutManager
                                    mainRecyclerView.adapter = catalogAdapter
                                    val result: MutableList<CardScreenDataModel> = mutableListOf()
                                    categories.forEach { result.add(it.toMainScreenDataModel()) }
                                    catalogAdapter?.setData(result) {
                                        loadingLayout.visibility = View.GONE
                                    }
                                    isDataLoaded = true
                                }
                                else -> throw RuntimeException("Wrong List type ${appState.data}")
                            }
                        }
                        else -> throw RuntimeException("Wrong AppState type $appState")
                    }
                }

                is AppState.Error -> handleNetworkErrorWithToast(appState.error)

                AppState.Loading -> binding.loadingLayout.visibility = View.VISIBLE

                else -> throw RuntimeException("Wrong AppState type $appState")
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
            mainRecyclerAdapter?.setData(
                data.listOfElements,
                searchField.editText?.text.isNullOrEmpty(),
                data.isLastPage
            )
            isDataLoaded = true
        }
    }
}