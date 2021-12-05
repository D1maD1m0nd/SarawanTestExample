package com.example.sarawan.framework.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import com.example.sarawan.activity.FabChanger
import com.example.sarawan.app.App
import com.example.sarawan.databinding.FragmentCatalogListBinding
import com.example.sarawan.databinding.FragmentMainBinding
import com.example.sarawan.framework.INavigation
import com.example.sarawan.framework.ui.category.viewModel.CategoryViewModel
import com.example.sarawan.framework.ui.main.adapter.MainRecyclerAdapter
import com.example.sarawan.model.data.MainScreenDataModel
import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.utils.NetworkStatus
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class CategoryFragment : Fragment(), INavigation {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var schedulerProvider: ISchedulerProvider

    @Inject
    lateinit var networkStatus: NetworkStatus

    @Inject
    lateinit var imageLoader: ImageLoader

    private var _binding: FragmentCatalogListBinding? = null
    private val binding get() = _binding!!

    val viewModel: CategoryViewModel by lazy {
        viewModelFactory.create(CategoryViewModel::class.java)
    }

    private var isOnline = true

    private var recyclerAdapter: MainRecyclerAdapter? = null

    private var fabChanger: FabChanger? = null

    private val onListItemClickListener: MainRecyclerAdapter.OnListItemClickListener =
        object : MainRecyclerAdapter.OnListItemClickListener {
            override fun onItemClick(data: MainScreenDataModel, diff: Int, isNewItem: Boolean) {
                data.price?.let {
                    fabChanger?.changePrice(it * diff)
//                    viewModel.saveData(data, isOnline, isNewItem)
                }
            }
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
        _binding = FragmentCatalogListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onFragmentBackStack() {
        App.navController.popBackStack()
    }

    override fun onFragmentNext() = Unit
}