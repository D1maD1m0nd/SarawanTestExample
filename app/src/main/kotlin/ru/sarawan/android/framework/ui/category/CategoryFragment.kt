package ru.sarawan.android.framework.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.sarawan.android.R
import ru.sarawan.android.databinding.SpinnerDropdownViewElementBinding
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import ru.sarawan.android.framework.ui.category.spinnerAdapter.CustomSpinnerAdapter
import ru.sarawan.android.framework.ui.category.viewModel.CategoryViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.MainScreenDataModel
import ru.sarawan.android.utils.constants.SortBy
import ru.sarawan.android.utils.exstentions.token

class CategoryFragment : BaseMainCatalogFragment() {

    private val args: CategoryFragmentArgs by navArgs()

    override val viewModel: CategoryViewModel by lazy {
        viewModelFactory.create(CategoryViewModel::class.java)
    }

    private var sortType: SortBy = SortBy.PRICE_ASC

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getStartData(isOnline, !sharedPreferences.token.isNullOrEmpty())
        setupAppBar()
        initSpinner()
        if (args.filterList != null) isFilterSubcategory = true
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupAppBar() = with(binding) {
        topBar.visibility = View.GONE
        searchBar.visibility = View.GONE
        barWithSpinner.visibility = View.VISIBLE
        backButton.setOnClickListener { findNavController().popBackStack() }
        fragmentCaption.text = args.categoryName
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
                                args.categoryType,
                                filterCategory,
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
        if (!isInitCompleted) {
            sortType =
                if (args.categoryName != null) SortBy.PRICE_ASC
                else SortBy.DISCOUNT
            binding.catalogSortSpinner.setSelection(sortType.id)
        }
    }

    override fun attachAdapterToView() {
        binding.mainRecyclerView.layoutManager = gridLayoutManager
        binding.mainRecyclerView.adapter = mainRecyclerAdapter
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
        val filters = args.filterList?.toList() ?: data.filters
        fillChips(filters)
        if (data.listOfElements.isNullOrEmpty()) {
            emptyDataLayout.root.visibility = View.VISIBLE
        } else {
            emptyDataLayout.root.visibility = View.GONE
            mainRecyclerAdapter?.setData(data.listOfElements, false, data.isLastPage)
        }
        loadingLayout.visibility = View.GONE
        isDataLoaded = true
    }

    companion object {
        const val DISCOUNT = -1
    }
}