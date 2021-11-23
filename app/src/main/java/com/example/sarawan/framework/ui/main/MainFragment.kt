package com.example.sarawan.framework.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import coil.ImageLoader
import com.example.sarawan.FabChanger
import com.example.sarawan.MainActivity
import com.example.sarawan.databinding.FragmentMainBinding
import com.example.sarawan.framework.ui.main.adapter.CardType
import com.example.sarawan.framework.ui.main.adapter.MainRecyclerAdapter
import com.example.sarawan.framework.ui.main.viewModel.MainViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.MainScreenDataModel
import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.utils.NetworkStatus
import dagger.android.support.AndroidSupportInjection
import java.util.*
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

    private var fabChanger: FabChanger? = null

    private var adapter: MainRecyclerAdapter? = null

    private val onListItemClickListener: MainRecyclerAdapter.OnListItemClickListener =
        object : MainRecyclerAdapter.OnListItemClickListener {
            override fun onItemClick(data: MainScreenDataModel, diff: Int) {
                data.price?.let {
                    fabChanger?.changePrice(it * diff)
                }
            }
        }

    private var isOnline = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

        if (savedInstanceState == null) viewModel.getStartData(isOnline)
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
        initSearchField()
    }

    private fun initSearchField() {
        initSearchInput()
        initSearchButton()
        initMicButton()
    }

    private fun initMicButton() {
        binding.micButton.setOnClickListener {
            Thread {
                Thread.sleep(3000)
                binding.root.post { makeSearch() }
            }.run()
        }
    }

    private fun initSearchButton() {
        binding.searchButton.setOnClickListener {
            makeSearch()
        }
    }

    private fun initSearchInput() {

        binding.searchField.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.clearText.visibility = View.VISIBLE
//                binding.micButton.visibility = View.INVISIBLE
            } else {
                binding.clearText.visibility = View.INVISIBLE
//                binding.micButton.visibility = View.VISIBLE
            }
        }

        binding.searchField.editText?.doOnTextChanged { _, _, _, count ->
            if (count > 0) {
                binding.clearText.visibility = View.VISIBLE
//                binding.micButton.visibility = View.INVISIBLE
            } else {
                binding.clearText.visibility = View.INVISIBLE
//                binding.micButton.visibility = View.VISIBLE
            }
        }

        binding.clearText.setOnClickListener {
            binding.searchField.editText?.setText("")
        }

        binding.searchField.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                makeSearch()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun makeSearch() {
        activity?.getSystemService<InputMethodManager>()
            ?.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        binding.searchField.clearFocus()
        viewModel.search("test", isOnline)
        if (!isOnline) Toast.makeText(
            context,
            "You are Offline! Get Results from Cache",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initRecyclerAdapter() {
        if (adapter == null) {
            adapter = MainRecyclerAdapter(onListItemClickListener, imageLoader) {
                binding.loadingLayout.visibility = View.GONE
            }
        }
        (binding.mainRecyclerView.layoutManager as GridLayoutManager).spanSizeLookup =
            object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter!!.getItemViewType(position)) {
                        CardType.TOP.type -> 2
                        CardType.COMMON.type -> 1
                        CardType.STRING.type -> 2
                        CardType.BUTTON.type -> 2
                        CardType.EMPTY.type -> 1
                        CardType.PARTNERS.type -> 2
                        else -> -1
                    }
                }
            }
        binding.mainRecyclerView.adapter = adapter
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState ->
            when (appState) {
                is AppState.Success<*> -> {
                    val data = appState.data as List<MainScreenDataModel>
                    var price = 0f
                    data.forEach {
                        if (it.price != null && it.quantity != null) price += (it.price * it.quantity!!)
                    }
                    if (data.isNullOrEmpty()) {
                        binding.loadingLayout.visibility = View.GONE
                    } else {
                        adapter?.setData(data, binding.searchField.editText?.text.isNullOrEmpty())
                    }
                    fabChanger?.putPrice(price)
                    binding.topBarLayout.setExpanded(true, true)
                    binding.mainRecyclerView.scrollToPosition(0)
                }
                is AppState.Error -> Unit
                AppState.Loading -> binding.loadingLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun checkOnlineStatus() {
        networkStatus
            .isOnline()
            .observeOn(schedulerProvider.io)
            .subscribeOn(schedulerProvider.io)
            .subscribe { isOnline ->
                this.isOnline = isOnline
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fabChanger = context as FabChanger
    }

    override fun onDetach() {
        fabChanger = null
        super.onDetach()
    }

    override fun onDestroy() {
        _binding = null
        (activity as MainActivity).setSupportActionBar(null)
        super.onDestroy()
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}