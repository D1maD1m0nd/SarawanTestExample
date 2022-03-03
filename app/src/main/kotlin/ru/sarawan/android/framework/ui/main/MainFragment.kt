package ru.sarawan.android.framework.ui.main

import android.os.Bundle
import android.view.View
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import ru.sarawan.android.framework.ui.base.mainCatalog.CardType
import ru.sarawan.android.framework.ui.main.viewModel.MainViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.MainScreenDataModel
import ru.sarawan.android.utils.exstentions.token

class MainFragment : BaseMainCatalogFragment() {

    override val viewModel: MainViewModel by lazy {
        viewModelFactory.get().create(MainViewModel::class.java)
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
                    if (appState.data is MainScreenDataModel) {
                        if (mainRecyclerAdapter != null && mainRecyclerAdapter!!.itemCount > 0) {
                            if (isInitCompleted) handleSuccessResult(appState.data)
                            else binding.loadingLayout.visibility = View.GONE
                        } else handleSuccessResult(appState.data)
                        isInitCompleted = true
                    } else throw RuntimeException("Wrong AppState type $appState")
                }

                is AppState.Error -> handleNetworkErrorWithToast(appState.error)

                AppState.Loading -> binding.loadingLayout.visibility = View.VISIBLE

                else -> throw RuntimeException("Wrong AppState type $appState")
            }
        }
    }

    private fun handleSuccessResult(data: MainScreenDataModel) = with(binding) {
        if (data.listOfElements.find { it.cardType == CardType.TOP.type } == null) fillChips(data.filters)
        if (data.listOfElements.isNullOrEmpty()) {
            loadingLayout.visibility = View.GONE
            emptyDataLayout.root.visibility = View.VISIBLE
        } else {
            emptyDataLayout.root.visibility = View.GONE
            mainRecyclerAdapter?.setData(
                data.listOfElements,
                searchField.editText?.text.isNullOrEmpty(),
                data.isLastPage
            )
        }
        isDataLoaded = true
    }
}