package com.example.sarawan.framework.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import coil.ImageLoader
import com.example.sarawan.databinding.FragmentMainBinding
import com.example.sarawan.framework.viewModel.MainViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.DataModel
import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.utils.NetworkStatus
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MainFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var schedulerProvider: ISchedulerProvider

    @Inject
    lateinit var networkStatus: NetworkStatus

    @Inject
    lateinit var imageLoader: ImageLoader

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        viewModelFactory.create(MainViewModel::class.java)
    }

    private var adapter: MainRecyclerAdapter? = null

    private val onListItemClickListener: MainRecyclerAdapter.OnListItemClickListener =
        object : MainRecyclerAdapter.OnListItemClickListener {
            override fun onItemClick(data: DataModel) {
                Toast.makeText(context, data.itemDescription, Toast.LENGTH_SHORT).show()
            }
        }

    private var isOnline = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkOnlineStatus()
        initRecyclerAdapter()
        initFab()

        viewModel.search("test", isOnline)
    }

    private fun initFab() {
        binding.fabPrice.text = "100 ₽"
    }

    private fun initRecyclerAdapter() {
        adapter = MainRecyclerAdapter(onListItemClickListener, listOf(), imageLoader)
        binding.mainRecyclerView.adapter = adapter
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Success -> {
                    adapter?.setData(appState.data)
                }
                is AppState.Error -> Unit
                AppState.Loading -> Unit
            }
        }
        binding.mainRecyclerView.setOnScrollChangeListener { _, _, scrollY, _, _ ->

            if ((binding.mainRecyclerView.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition() == 0) binding.topBar.visibility = View.VISIBLE
            else binding.topBar.visibility = View.GONE
        }
    }

    private fun checkOnlineStatus() {
        networkStatus
            .isOnlineSingle()
            .observeOn(schedulerProvider.io)
            .subscribeOn(schedulerProvider.io)
            .subscribe({ isOnline ->
                this.isOnline = isOnline
                if (isOnline) Toast.makeText(context, "You now Online", Toast.LENGTH_SHORT).show()
    //                else Toast.makeText(context, "You are Offline!", Toast.LENGTH_SHORT).show()
            },
                {
                    Log.d("Test", it.message.toString())
                }
            )
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}