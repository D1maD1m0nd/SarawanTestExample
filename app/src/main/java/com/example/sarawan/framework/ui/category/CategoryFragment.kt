package com.example.sarawan.framework.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.R
import com.example.sarawan.databinding.FragmentCatalogListBinding
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import com.example.sarawan.framework.ui.base.mainCatalog.CardType
import com.example.sarawan.framework.ui.category.viewModel.CategoryViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.MainScreenDataModel
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
        initSpinner()
        loadStartData()
        watchForAdapter()
    }

    private fun watchForAdapter() {
        categoryBinding.cardsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                mainRecyclerAdapter?.let { adapter ->
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && adapter.itemCount - gridLayoutManager.findLastVisibleItemPosition() < 2
                        && isOnline
                    ) viewModel.getMoreData(isOnline)
                }
            }
        })
    }

    private fun loadStartData() {
        categoryBinding.fragmentCaption.text =
            arguments?.getString(KEY_CATEGORY_NAME) ?: "Выгодные предложения"
        if (isOnline) viewModel
            .getStartData(arguments?.getInt(KEY_CATEGORY) ?: DISCOUNT, isOnline)
    }

    private fun initSpinner() {
        val spinnerItems = resources.getStringArray(R.array.sortItems)
        val spinnerAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_dropdown_view_element,
                R.id.item_text,
                spinnerItems
            )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_view_element)
        categoryBinding.catalogSortSpinner.adapter = spinnerAdapter
        categoryBinding.catalogSortSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    spinnerItems[position]
                }
            }

    }

    override fun attachAdapterToView() {
        categoryBinding.cardsRecycler.layoutManager = gridLayoutManager
        categoryBinding.cardsRecycler.adapter = mainRecyclerAdapter
    }

    override fun subscribeToViewModel() {
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Success<*> -> {
                    val data = appState.data as List<MainScreenDataModel>
                    if (data.isNullOrEmpty()) {
                        mainRecyclerAdapter?.clearData()
                    } else {
                        data.forEach {
                            it.cardType = CardType.COMMON.type
                        }
                        mainRecyclerAdapter?.setData(data, false)
                    }
                    categoryBinding.cardsRecycler.scrollToPosition(0)
                    categoryBinding.loadingLayout.visibility = View.GONE
                }
                is AppState.Error -> Unit
                AppState.Loading -> categoryBinding.loadingLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoryBinding.cardsRecycler.layoutManager = null
        categoryBinding.cardsRecycler.adapter = null
        _binding = null
    }

    override fun onFragmentNext() = Unit

    companion object {
        const val DISCOUNT = -1
        const val KEY_CATEGORY = "CATEGORY"
        const val KEY_CATEGORY_NAME = "CATEGORY NAME"
    }
}