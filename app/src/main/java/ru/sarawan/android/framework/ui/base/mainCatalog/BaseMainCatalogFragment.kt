package ru.sarawan.android.framework.ui.base.mainCatalog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.RecognizerIntent
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
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import dagger.android.support.AndroidSupportInjection
import ru.sarawan.android.R
import ru.sarawan.android.app.App
import ru.sarawan.android.databinding.FragmentMainBinding
import ru.sarawan.android.framework.INavigation
import ru.sarawan.android.framework.ui.basket.BasketFragment
import ru.sarawan.android.framework.ui.main.adapter.MainRecyclerAdapter
import ru.sarawan.android.framework.ui.main.viewHolder.ButtonMoreClickListener
import ru.sarawan.android.model.data.MainScreenDataModel
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.NetworkStatus
import ru.sarawan.android.utils.exstentions.token
import java.util.*
import javax.inject.Inject

abstract class BaseMainCatalogFragment : Fragment(), INavigation {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var schedulerProvider: ISchedulerProvider

    @Inject
    lateinit var networkStatus: NetworkStatus

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var _binding: FragmentMainBinding? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: BaseMainCatalogViewModel

    protected var isOnline = false

    protected var maxCount = -1

    protected var isDataLoaded = false

    protected var mainRecyclerAdapter: MainRecyclerAdapter? = null

    protected var fabChanger: ru.sarawan.android.activity.FabChanger? = null

    protected val gridLayoutManager: GridLayoutManager by lazy {
        GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
    }

    private val onListItemClickListener: BaseMainCatalogAdapter.OnListItemClickListener =
        object : BaseMainCatalogAdapter.OnListItemClickListener {
            override fun onItemPriceChangeClick(
                data: MainScreenDataModel,
                diff: Int,
                isNewItem: Boolean,
                callback: (isOnline: Boolean) -> Unit
            ) {
                data.price?.let {
                    if (isOnline) {
                        callback(isOnline)
                        fabChanger?.changePrice(it * diff)
                        viewModel.saveData(data, !sharedPreferences.token.isNullOrEmpty(), isNewItem)
                    } else handleNetworkErrorWithToast()
                }
            }

            override fun onItemClick(data: MainScreenDataModel) {
                if (isOnline) {
                    val bundle = Bundle()
                    bundle.putLong(BasketFragment.PRODUCT_ID, data.id ?: -1)
                    App.navController.navigate(R.id.productCardFragment, bundle)
                } else handleNetworkErrorWithToast()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        checkOnlineStatus()
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
        initRecyclerAdapter()
        attachAdapterToView()
        initSearchField()
        subscribeToViewModel()
        watchForAdapter()
        initRefreshButton()
    }

    private fun initRefreshButton() {
        binding.noConnectionLayout.refreshButton.setOnClickListener {
            refresh()
        }
    }

    protected abstract fun refresh()

    private fun watchForAdapter(recyclerView: RecyclerView = binding.mainRecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                mainRecyclerAdapter?.let { adapter ->
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && isDataLoaded
                        && recyclerView.layoutManager is GridLayoutManager
                        && adapter.getItemViewType(
                            (recyclerView.layoutManager as GridLayoutManager)
                                .findLastVisibleItemPosition()
                        ) == CardType.LOADING.type
                    ) if (isOnline) {
                        isDataLoaded = false
                        viewModel.getMoreData(isOnline, !sharedPreferences.token.isNullOrEmpty())
                    } else handleNetworkErrorWithToast()
                    adapter.changeLoadingAnimation(isOnline)
                }
            }
        })
    }

    protected abstract fun attachAdapterToView()

    protected abstract fun subscribeToViewModel()

    private fun initRecyclerAdapter() {
        val buttonMoreClickListener = ButtonMoreClickListener {
            if (isOnline) App.navController.navigate(R.id.action_mainFragment_to_categoryFragment)
            else handleNetworkErrorWithToast()
        }
        if (mainRecyclerAdapter == null) {
            mainRecyclerAdapter =
                MainRecyclerAdapter(onListItemClickListener, imageLoader, buttonMoreClickListener) {
                    _binding?.loadingLayout?.visibility = View.GONE
                }
        } else mainRecyclerAdapter?.clear()

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mainRecyclerAdapter!!.getItemViewType(position)) {
                    CardType.TOP.type -> 2
                    CardType.COMMON.type -> 1
                    CardType.STRING.type -> 2
                    CardType.BUTTON.type -> 2
                    CardType.LOADING.type -> 2
                    else -> -1
                }
            }
        }
    }

    private fun initSearchField() {
        initSearchInput()
        initSearchButton()
        initMicButton()
    }

    private fun initMicButton() {
        binding.micButton.setOnClickListener { displaySpeechRecognizer() }
    }

    private fun initSearchButton() {
        binding.searchButton.setOnClickListener {
            if (binding.searchField.editText?.text.toString().isNotEmpty()) makeSearch()
        }
    }

    private fun initSearchInput() {

        binding.searchField.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.clearText.visibility = View.VISIBLE
                binding.micButton.visibility = View.INVISIBLE
            } else {
                binding.clearText.visibility = View.INVISIBLE
                binding.micButton.visibility = View.VISIBLE
            }
        }

        binding.searchField.editText?.doOnTextChanged { _, _, _, count ->
            if (count > 0) {
                binding.clearText.visibility = View.VISIBLE
                binding.micButton.visibility = View.INVISIBLE
            } else {
                binding.clearText.visibility = View.INVISIBLE
                binding.micButton.visibility = View.VISIBLE
            }
        }

        binding.clearText.setOnClickListener { binding.searchField.editText?.setText("") }

        binding.searchField.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                && binding.searchField.editText?.text.toString().isNotEmpty()
            ) {
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
        if (isOnline) {
            viewModel.search(
                binding.searchField.editText?.text.toString(),
                isOnline,
                !sharedPreferences.token.isNullOrEmpty()
            )
            binding.loadingLayout.visibility = View.VISIBLE
            mainRecyclerAdapter?.clear()
            binding.topBarLayout.setExpanded(true, true)
            binding.mainRecyclerView.scrollToPosition(0)
        } else handleNetworkErrorWithToast()
    }

    protected fun handleNetworkErrorWithToast(throwable: Throwable? = RuntimeException(getString(R.string.no_internet))) {
        Toast.makeText(context, throwable?.message, Toast.LENGTH_SHORT).show()
    }

    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_in_sarafan))
        }
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { it?.get(0) }
            binding.searchField.editText?.setText(spokenText)
            makeSearch()
        }
    }

    protected fun handleNetworkErrorWithLayout() {
        binding.noConnectionLayout.root.visibility = View.VISIBLE
        fabChanger?.changeState()
    }

    private fun checkOnlineStatus() {
        networkStatus
            .isOnline()
            .observeOn(schedulerProvider.io)
            .subscribeOn(schedulerProvider.io)
            .subscribe { isOnline = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mainRecyclerView.layoutManager = null
        binding.mainRecyclerView.adapter = null
        _binding = null
        viewModel.clear()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fabChanger = context as ru.sarawan.android.activity.FabChanger
    }

    override fun onDetach() {
        fabChanger = null
        super.onDetach()
    }

    override fun onFragmentBackStack() {
        App.navController.popBackStack()
    }

    companion object {
        private const val SPEECH_REQUEST_CODE = 0
    }
}