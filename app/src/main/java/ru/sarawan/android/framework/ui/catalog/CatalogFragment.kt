package ru.sarawan.android.framework.ui.catalog

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import ru.sarawan.android.framework.ui.catalog.adapter.CatalogRecyclerAdapter
import ru.sarawan.android.framework.ui.catalog.viewModel.CatalogViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.CategoryDataModel
import ru.sarawan.android.model.data.MainScreenDataModel
import ru.sarawan.android.model.data.toMainScreenDataModel
import ru.sarawan.android.utils.exstentions.token

class CatalogFragment : BaseMainCatalogFragment() {

    private var catalogAdapter: CatalogRecyclerAdapter? = null

    override val viewModel: CatalogViewModel by lazy {
        viewModelFactory.create(CatalogViewModel::class.java)
    }

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private val onCatalogItemClickListener: CatalogRecyclerAdapter.OnListItemClickListener =
        object : CatalogRecyclerAdapter.OnListItemClickListener {
            override fun onItemClick(data: MainScreenDataModel) {
                if (isOnline) {
                    val action = CatalogFragmentDirections.actionCatalogFragmentToCategoryFragment(
                        data.itemDescription.orEmpty(),
                        data.id?.toInt() ?: -1
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
//    {
//        binding.mainRecyclerView.layoutManager = linearLayoutManager
//        binding.mainRecyclerView.adapter = catalogAdapter
//    }

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
                        is Pair<*, *> -> {
                            if (mainRecyclerAdapter != null && mainRecyclerAdapter!!.itemCount > 0) {
                                if (isInitCompleted) handleSuccessData(listData)
                                else binding.loadingLayout.visibility = View.GONE
                            } else handleSuccessData(listData)
                            isInitCompleted = true
                        }
                        is CategoryDataModel -> {
                            mainRecyclerView.layoutManager = linearLayoutManager
                            mainRecyclerView.adapter = catalogAdapter
                            val result: MutableList<MainScreenDataModel> = mutableListOf()
                            (listData as List<CategoryDataModel>).forEach {
                                result.add(it.toMainScreenDataModel())
                            }
                            catalogAdapter?.setData(result) { loadingLayout.visibility = View.GONE }
                        }
                    }
                }
                is AppState.Error -> handleNetworkErrorWithToast(appState.error)
                AppState.Loading -> binding.loadingLayout.visibility = View.VISIBLE
                AppState.Empty -> Unit
            }
        }
    }

    private fun handleSuccessData(listData: List<*>?) = with(binding) {
        listData?.first() as Pair<Int, List<MainScreenDataModel>>
        if ((listData.first() as Pair<Int, List<MainScreenDataModel>>)
                .second.isNullOrEmpty()
        ) {
            loadingLayout.visibility = View.GONE
            emptyDataLayout.root.visibility = View.VISIBLE
        } else {
            emptyDataLayout.root.visibility = View.GONE
            mainRecyclerView.layoutManager = gridLayoutManager
            mainRecyclerView.adapter = mainRecyclerAdapter
            maxCount =
                (listData.first() as Pair<Int, List<MainScreenDataModel>>).first
            mainRecyclerAdapter?.setData(
                (listData.first() as Pair<Int, List<MainScreenDataModel>>).second,
                searchField.editText?.text.isNullOrEmpty(),
                maxCount
            )
            isDataLoaded = true
        }
    }
}