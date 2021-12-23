package com.example.sarawan.framework.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.example.sarawan.R
import com.example.sarawan.databinding.FragmentCatalogListBinding
import com.example.sarawan.databinding.SpinnerDropdownViewElementBinding
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import com.example.sarawan.framework.ui.category.spinnerAdapter.CustomSpinnerAdapter
import com.example.sarawan.framework.ui.category.viewModel.CategoryViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.MainScreenDataModel
import com.example.sarawan.utils.SortBy
import dagger.android.support.AndroidSupportInjection

class CategoryFragment : BaseMainCatalogFragment() {

    private var _binding: FragmentCatalogListBinding? = null
    private val categoryBinding get() = _binding!!

    override val viewModel: CategoryViewModel by lazy {
        viewModelFactory.create(CategoryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCatalogListBinding.inflate(inflater, container, false)
        return categoryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryBinding.backButton.setOnClickListener {
            onFragmentBackStack()
        }
        categoryBinding.fragmentCaption.text =
            arguments?.getString(KEY_CATEGORY_NAME) ?: "Выгодные предложения"
        initSpinner()
        watchForAdapter(categoryBinding.cardsRecycler)
    }

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

        categoryBinding.catalogSortSpinner.adapter = spinnerAdapter
        categoryBinding.catalogSortSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (isOnline) {
                        mainRecyclerAdapter?.clear()
                        spinnerAdapter.setSelected(position)
                        val sortType =
                            SortBy.values().find { it.id.toLong() == id } ?: SortBy.PRICE_ASC
                        viewModel.changeSorting(
                            arguments?.getInt(KEY_CATEGORY) ?: DISCOUNT,
                            isOnline,
                            sortType
                        )
                    } else { /*TODO handle network error */
                    }
                }
            }
        categoryBinding.catalogSortSpinner.alpha = 1f
        val selection =
            if (arguments?.getString(KEY_CATEGORY_NAME) != null) SortBy.PRICE_ASC.id
            else SortBy.DISCOUNT.id
        categoryBinding.catalogSortSpinner.setSelection(selection)
    }

    override fun attachAdapterToView() {
        categoryBinding.cardsRecycler.layoutManager = gridLayoutManager
        categoryBinding.cardsRecycler.adapter = mainRecyclerAdapter
    }

    override fun subscribeToViewModel() {
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Success<*> -> {
                    val data = appState.data as List<Pair<Int, List<MainScreenDataModel>>>
                    if (data.first().second.isNullOrEmpty()) {
                        categoryBinding.emptyDataLayout.root.visibility = View.VISIBLE
                    } else {
                        categoryBinding.emptyDataLayout.root.visibility = View.GONE
                        maxCount = data.first().first
                        mainRecyclerAdapter?.setData(data.first().second, false, maxCount)
                        isDataLoaded = true
                    }
                    categoryBinding.loadingLayout.visibility = View.GONE
                }
                is AppState.Error -> Unit
                AppState.Loading -> categoryBinding.loadingLayout.visibility = View.VISIBLE
                AppState.Empty -> Unit
            }
        }
    }

    override fun onDestroyView() {
        categoryBinding.cardsRecycler.layoutManager = null
        categoryBinding.cardsRecycler.adapter = null
        _binding = null
        super.onDestroyView()
    }

    override fun onFragmentNext() = Unit

    companion object {
        const val DISCOUNT = -1
        const val KEY_CATEGORY = "CATEGORY"
        const val KEY_CATEGORY_NAME = "CATEGORY NAME"
    }
}