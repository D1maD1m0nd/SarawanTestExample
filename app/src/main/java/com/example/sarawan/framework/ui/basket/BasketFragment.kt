package com.example.sarawan.framework.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sarawan.databinding.FragmentBasketBinding
import com.example.sarawan.databinding.FragmentMainBinding
import com.example.sarawan.framework.ui.basket.adapter.BasketAdapter
import com.example.sarawan.framework.ui.basket.adapter.DiffUtilsBasket
import com.example.sarawan.framework.ui.basket.viewModel.BasketViewModel
import com.example.sarawan.framework.viewModel.MainViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.BasketDataModel
import com.example.sarawan.model.data.DataModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class BasketFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!
    private val adapter = BasketAdapter()
    private val viewModel: BasketViewModel by lazy {
        viewModelFactory.create(BasketViewModel::class.java)
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
        _binding = FragmentBasketBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            setState(appState)
        }
    }

    private fun initRcView() = with(binding) {
        cardContainerRcView.layoutManager = LinearLayoutManager(context)
        cardContainerRcView.adapter = adapter
    }
    private fun setState(appState: AppState) {
        when (appState) {
            is AppState.Success<*> -> {
                val data = appState.data as List<BasketDataModel>
                diffData(data)
            }
            is AppState.Error -> Unit
            AppState.Loading -> Unit
        }
    }

    private fun diffData(newList : List<BasketDataModel>) {
        val oldList = adapter.data
        val utils = DiffUtilsBasket(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(utils)
        adapter.setData(newList)
        diffResult.dispatchUpdatesTo(adapter)
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    companion object {
        fun newInstance() = BasketFragment()
    }
}