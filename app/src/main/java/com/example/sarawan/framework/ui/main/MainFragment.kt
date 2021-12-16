package com.example.sarawan.framework.ui.main

import android.os.Bundle
import android.view.View
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import com.example.sarawan.framework.ui.main.viewModel.MainViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.MainScreenDataModel

class MainFragment : BaseMainCatalogFragment() {

    override val viewModel: MainViewModel by lazy {
        viewModelFactory.create(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && isOnline) viewModel.getStartData(isOnline) { /*TODO handle error loading data */ }
    }

    override fun attachAdapterToView() {
        binding.mainRecyclerView.layoutManager = gridLayoutManager
        binding.mainRecyclerView.adapter = mainRecyclerAdapter
    }

    override fun subscribeToViewModel() {
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Success<*> -> {
                    val data = appState.data as List<Pair<Int, List<MainScreenDataModel>>>
                    if (data.first().second.isNullOrEmpty()) {
                        binding.loadingLayout.visibility = View.GONE
                    } else {
                        maxCount = data.first().first
                        mainRecyclerAdapter?.setData(
                            data.first().second,
                            binding.searchField.editText?.text.isNullOrEmpty(),
                            maxCount
                        )
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