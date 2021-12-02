package com.example.sarawan.framework.ui.catalog

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogFragment
import com.example.sarawan.framework.ui.catalog.adapter.CatalogRecyclerAdapter
import com.example.sarawan.framework.ui.catalog.viewModel.CatalogViewModel
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
                TODO("Not yet implemented")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) viewModel.getStartData(isOnline)
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
                        mainRecyclerAdapter?.clearData()
                        catalogAdapter?.clearData()
                        binding.loadingLayout.visibility = View.GONE
                    } else when (listData.first()) {
                        is MainScreenDataModel -> {
                            binding.mainRecyclerView.layoutManager = gridLayoutManager
                            binding.mainRecyclerView.adapter = mainRecyclerAdapter
                            mainRecyclerAdapter?.setData(
                                listData as List<MainScreenDataModel>,
                                binding.searchField.editText?.text.isNullOrEmpty()
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
                    binding.topBarLayout.setExpanded(true, true)
                    binding.mainRecyclerView.scrollToPosition(0)
                }
                is AppState.Error -> Unit
                AppState.Loading -> binding.loadingLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onFragmentNext() {
        //action указывается в файле навигации ввиде стрелки(потяни точку у экрана и справа появится меню,
        // с помощью него можно передавать данные между фрагментами
//        navController.navigate(R.id.action_catalogFragment_to_profileFragment)
        //navController.navigate(R.id.profileFragment)
        TODO("Not yet implemented")
    }

    companion object {
        fun newInstance() = CatalogFragment()
    }
}