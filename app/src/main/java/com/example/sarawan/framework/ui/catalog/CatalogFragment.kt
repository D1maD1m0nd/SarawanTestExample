package com.example.sarawan.framework.ui.catalog

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sarawan.R
import com.example.sarawan.app.App
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import com.example.sarawan.framework.ui.catalog.adapter.CatalogRecyclerAdapter
import com.example.sarawan.framework.ui.catalog.viewModel.CatalogViewModel
import com.example.sarawan.framework.ui.category.CategoryFragment
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.CategoryDataModel
import com.example.sarawan.model.data.MainScreenDataModel
import com.example.sarawan.model.data.toMainScreenDataModel

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
                val bundle = Bundle()
                bundle.putInt(CategoryFragment.KEY_CATEGORY, data.id?.toInt() ?: -1)
                bundle.putString(CategoryFragment.KEY_CATEGORY_NAME, data.itemDescription)
                App.navController.navigate(R.id.action_catalogFragment_to_categoryFragment, bundle)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && isOnline) viewModel.getStartData(isOnline) { /*TODO handle error loading data */ }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initCatalogRecyclerAdapter()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun attachAdapterToView() {
        binding.mainRecyclerView.layoutManager = linearLayoutManager
        binding.mainRecyclerView.adapter = catalogAdapter
    }

    private fun initCatalogRecyclerAdapter() {
        if (catalogAdapter == null) {
            catalogAdapter = CatalogRecyclerAdapter(onCatalogItemClickListener)
        }
    }

    override fun subscribeToViewModel() {
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Success<*> -> {
                    val listData = appState.data as List<*>?
                    if (listData.isNullOrEmpty()) {
                        binding.loadingLayout.visibility = View.GONE
                    } else when (listData.first()) {
                        is Pair<*, *> -> {
                            binding.mainRecyclerView.layoutManager = gridLayoutManager
                            binding.mainRecyclerView.adapter = mainRecyclerAdapter
                            maxCount =
                                (listData.first() as Pair<Int, List<MainScreenDataModel>>).first
                            mainRecyclerAdapter?.setData(
                                (listData.first() as Pair<Int, List<MainScreenDataModel>>).second,
                                binding.searchField.editText?.text.isNullOrEmpty(),
                                maxCount
                            )
                        }
                        is CategoryDataModel -> {
                            binding.mainRecyclerView.layoutManager = linearLayoutManager
                            binding.mainRecyclerView.adapter = catalogAdapter
                            val result: MutableList<MainScreenDataModel> = mutableListOf()
                            (listData as List<CategoryDataModel>).forEach {
                                result.add(it.toMainScreenDataModel())
                            }
                            catalogAdapter?.setData(result) {
                                binding.loadingLayout.visibility = View.GONE
                            }
                        }
                    }
                    isDataLoaded = true
                }
                is AppState.Error -> Unit
                AppState.Loading -> binding.loadingLayout.visibility = View.VISIBLE
                AppState.Empty -> Unit
            }
        }
    }

    override fun onFragmentNext() = Unit
}