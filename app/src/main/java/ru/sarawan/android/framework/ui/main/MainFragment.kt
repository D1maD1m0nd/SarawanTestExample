package ru.sarawan.android.framework.ui.main

import android.os.Bundle
import android.view.View
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import ru.sarawan.android.framework.ui.main.viewModel.MainViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.MainScreenDataModel
import ru.sarawan.android.utils.exstentions.token

class MainFragment : BaseMainCatalogFragment() {

    override val viewModel: MainViewModel by lazy {
        viewModelFactory.create(MainViewModel::class.java)
    }

    override fun attachAdapterToView() {
        binding.mainRecyclerView.layoutManager = gridLayoutManager
        binding.mainRecyclerView.adapter = mainRecyclerAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

    override fun subscribeToViewModel() {
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Success<*> -> {
                    if (mainRecyclerAdapter != null && mainRecyclerAdapter!!.itemCount > 0) {
                        if (isInitCompleted) handleSuccessResult(appState)
                        else binding.loadingLayout.visibility = View.GONE
                    } else handleSuccessResult(appState)
                    isInitCompleted = true
                }
                is AppState.Error -> handleNetworkErrorWithToast(appState.error)
                AppState.Loading -> binding.loadingLayout.visibility = View.VISIBLE
                AppState.Empty -> Unit
            }
        }
    }

    private fun handleSuccessResult(appState: AppState.Success<*>) {
        val data = appState.data as List<Pair<Int, List<MainScreenDataModel>>>
        if (data.first().second.isNullOrEmpty()) {
            binding.loadingLayout.visibility = View.GONE
            binding.emptyDataLayout.root.visibility = View.VISIBLE
        } else {
            binding.emptyDataLayout.root.visibility = View.GONE
            maxCount = data.first().first
            mainRecyclerAdapter?.setData(
                data.first().second,
                binding.searchField.editText?.text.isNullOrEmpty(),
                maxCount
            )
            isDataLoaded = true
        }
    }

    override fun onFragmentNext() = Unit
}