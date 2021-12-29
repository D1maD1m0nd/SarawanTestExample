package com.example.sarawan.framework.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.example.sarawan.R
import com.example.sarawan.databinding.SpinnerDropdownViewElementBinding
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import com.example.sarawan.framework.ui.category.spinnerAdapter.CustomSpinnerAdapter
import com.example.sarawan.framework.ui.category.viewModel.CategoryViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.MainScreenDataModel
import com.example.sarawan.utils.SortBy
import com.example.sarawan.utils.exstentions.token

class CategoryFragment : BaseMainCatalogFragment() {

    override val viewModel: CategoryViewModel by lazy {
        viewModelFactory.create(CategoryViewModel::class.java)
    }

    private var sortType: SortBy = SortBy.PRICE_ASC

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        initSpinner()
    }

    private fun setupAppBar() {
        binding.topBar.visibility = View.GONE
        binding.searchBar.visibility = View.GONE
        binding.barWithSpinner.visibility = View.VISIBLE
        binding.backButton.setOnClickListener { onFragmentBackStack() }
        binding.fragmentCaption.text =
            arguments?.getString(KEY_CATEGORY_NAME) ?: "Выгодные предложения"
    }

    override fun refresh() = fabChanger?.changeState() ?: Unit

    private fun initSpinner() {
        val spinnerStrings = arrayOf(
            resources.getString(SortBy.PRICE_ASC.text),
            resources.getString(SortBy.PRICE_DES.text),
            resources.getString(SortBy.ALPHABET.text),
            resources.getString(SortBy.DISCOUNT.text),
        )
        val spinnerItems = listOf(
            SpinnerDropdownViewElementBinding.inflate(LayoutInflater.from(context))
                .apply {
                    itemText.text = spinnerStrings[0]
                    itemIcon.setImageResource(R.drawable.spinner_arrow_up)
                },
            SpinnerDropdownViewElementBinding.inflate(LayoutInflater.from(context))
                .apply {
                    itemText.text = spinnerStrings[1]
                    itemIcon.setImageResource(R.drawable.spinner_arrow_down)
                },
            SpinnerDropdownViewElementBinding.inflate(LayoutInflater.from(context))
                .apply { itemText.text = spinnerStrings[2] },
            SpinnerDropdownViewElementBinding.inflate(LayoutInflater.from(context))
                .apply { itemText.text = spinnerStrings[3] }
        )
        val spinnerAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.spinner_dropdown_view_element,
            R.id.item_text,
            spinnerItems,
            spinnerStrings
        )

        binding.catalogSortSpinner.adapter = spinnerAdapter
        binding.catalogSortSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (isOnline) {
                        if (sortType.id != position || mainRecyclerAdapter?.itemCount == 0) {
                            mainRecyclerAdapter?.clear()
                            spinnerAdapter.setSelected(position)
                            sortType =
                                SortBy.values().find { it.id.toLong() == id } ?: SortBy.PRICE_ASC
                            viewModel.changeSorting(
                                arguments?.getInt(KEY_CATEGORY) ?: DISCOUNT,
                                isOnline,
                                sortType,
                                !sharedPreferences.token.isNullOrEmpty()
                            )
                        }
                    } else {
                        binding.catalogSortSpinner.setSelection(sortType.id)
                        handleNetworkErrorWithToast()
                    }
                }
            }
        binding.catalogSortSpinner.alpha = 1f
        sortType =
            if (arguments?.getString(KEY_CATEGORY_NAME) != null) SortBy.PRICE_ASC
            else SortBy.DISCOUNT
        binding.catalogSortSpinner.setSelection(sortType.id)
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
                        binding.emptyDataLayout.root.visibility = View.VISIBLE
                    } else {
                        binding.emptyDataLayout.root.visibility = View.GONE
                        maxCount = data.first().first
                        mainRecyclerAdapter?.setData(data.first().second, false, maxCount)
                        isDataLoaded = true
                    }
                    binding.loadingLayout.visibility = View.GONE
                }
                is AppState.Error -> handleNetworkErrorWithToast(appState.error)
                AppState.Loading -> binding.loadingLayout.visibility = View.VISIBLE
                AppState.Empty -> Unit
            }
        }
    }

    override fun onFragmentNext() = Unit

    companion object {
        const val DISCOUNT = -1
        const val KEY_CATEGORY = "CATEGORY"
        const val KEY_CATEGORY_NAME = "CATEGORY NAME"
    }
}