package com.example.sarawan.framework.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sarawan.databinding.FragmentBasketBinding
import com.example.sarawan.framework.ui.basket.viewModel.BasketViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.DataModel
import com.example.sarawan.model.data.DelegatesModel.BasketFooter
import com.example.sarawan.model.data.DelegatesModel.BasketHeader
import com.example.sarawan.model.data.DelegatesModel.BasketListItem
import com.example.sarawan.utils.AdapterDelegatesTypes
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class BasketFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!
    private val adapter = ListDelegationAdapter(
        AdapterDelegatesTypes.headerDelegateViewBindingViewHolder,
        AdapterDelegatesTypes.itemDelegateViewBindingViewHolder,
        AdapterDelegatesTypes.footerDelegateViewBindingViewHolder,
    )
    //основной список для отображения
    private val list : MutableList<BasketListItem> = ArrayList(
        listOf(
            BasketHeader(0),
            BasketFooter(0.0, 0.0),
        )
    )
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
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            setState(appState)
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()

        viewModel.search("test", false)
    }

    private fun initRcView() = with(binding) {
        cardContainerRcView.layoutManager = LinearLayoutManager(context)
        cardContainerRcView.adapter = adapter
    }
    private fun setState(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                val data = appState.data
                val countAdapter = list.size - 1
                setFooterData(data)
                setHeaderData(data)
                list.addAll(countAdapter,data)
                adapter.items = list
                adapter.notifyDataSetChanged()
            }
            is AppState.Error -> Unit
            AppState.Loading -> Unit
        }
    }
    private fun setFooterData(data : List<DataModel>) {
        val footer = (list.last() as BasketFooter)
        footer.apply {
            weight = 10.0
            price = sumPrice(data)
        }
    }

    private fun setHeaderData(data : List<DataModel>) {
        val header = (list.first() as BasketHeader)
        header.counter = data.size
    }

    private fun sumPrice(data : List<DataModel>) = data.sumOf { it.price!!.toDouble() }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    companion object {
        fun newInstance() = BasketFragment()
    }
}