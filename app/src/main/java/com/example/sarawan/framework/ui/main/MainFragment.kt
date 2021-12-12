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
        if (savedInstanceState == null) viewModel.getStartData(isOnline)
    }

    override fun attachAdapterToView() {
        binding.mainRecyclerView.layoutManager = gridLayoutManager
        binding.mainRecyclerView.adapter = mainRecyclerAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToMoreViewModel()
        watchForAdapter()
    }

    private fun watchForAdapter() {
        binding.mainRecyclerView.addRecyclerListener {
            mainRecyclerAdapter?.let { adapter ->
                if (adapter.itemCount - it.absoluteAdapterPosition == 20)
                    viewModel.getMoreData(isOnline)
            }
        }
    }

    private fun subscribeToMoreViewModel() {
        viewModel.getMoreLiveData().observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Success<*> -> {
                    val data = appState.data as List<MainScreenDataModel>
                    mainRecyclerAdapter?.addData(data)
                }
                is AppState.Error -> Unit
                AppState.Loading -> Unit
            }
        }
    }

    override fun subscribeToViewModel() {
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Success<*> -> {
                    val data = appState.data as List<MainScreenDataModel>
                    if (data.isNullOrEmpty()) {
                        mainRecyclerAdapter?.clearData()
                        binding.loadingLayout.visibility = View.GONE
                    } else mainRecyclerAdapter?.setData(
                        data,
                        binding.searchField.editText?.text.isNullOrEmpty()
                    )
                    binding.topBarLayout.setExpanded(true, true)
                    binding.mainRecyclerView.scrollToPosition(0)
                }
                is AppState.Error -> Unit
                AppState.Loading -> binding.loadingLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onFragmentNext() = Unit
}