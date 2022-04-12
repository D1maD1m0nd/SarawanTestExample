package ru.sarawan.android.framework.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentProfileBinding
import ru.sarawan.android.framework.ui.profile.adapter.ItemClickListener
import ru.sarawan.android.framework.ui.profile.adapter.OrdersAdapter
import ru.sarawan.android.framework.ui.profile.address_fragment.ProfileAddressDialogFragment
import ru.sarawan.android.framework.ui.profile.name_fragment.ProfileNameDialogFragment
import ru.sarawan.android.framework.ui.profile.viewModel.ProfileViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.OrderApprove
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.utils.constants.TypeCase
import ru.sarawan.android.utils.exstentions.*
import ru.sarawan.android.utils.exstentions.localstore.UNREGISTERED
import ru.sarawan.android.utils.exstentions.localstore.basketId
import ru.sarawan.android.utils.exstentions.localstore.token
import ru.sarawan.android.utils.exstentions.localstore.userId
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>

    private val viewModel: ProfileViewModel by lazy {
        viewModelFactory.get().create(ProfileViewModel::class.java)
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var user: UserDataModel? = null

    private var addressItem: AddressItem? = null
    private var addressList: List<AddressItem> = ArrayList()
    private val itemClickListener = object : ItemClickListener {
        override fun cancel(pos: Int) {
            cancelOrder(pos)
        }

    }
    private var orders: MutableList<OrderApprove> = ArrayList()
    private val adapter = OrdersAdapter(itemClickListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentProfileBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserData()
        initViews()
        subscribeToViewModel()
        getNavigationCallback()
    }

    private fun getNavigationCallback() {
        getNavigationResult<Boolean>(ProfileAddressDialogFragment.KEY_ADDRESS) { if (it) getUserData() }
        getNavigationResult<Boolean>(ProfileNameDialogFragment.KEY_NAME) { if (it) getUserData() }
    }

    private fun subscribeToViewModel() = viewModel.getStateLiveData()
        .observe(viewLifecycleOwner) { appState: AppState<*> -> setState(appState) }

    private fun getUserData() = if (sharedPreferences.userId != UNREGISTERED) {
        sharedPreferences.userId?.let {
            viewModel.getUserData(it)
            viewModel.getOrders()
        }
    } else Unit

    private fun initViews() = with(binding) {
        profileNameLayout.setOnClickListener { showName() }
        profileAddressLayout.setOnClickListener { showAddress() }
        profileExitButton.setOnClickListener { leave() }
    }

    private fun leave() {
        sharedPreferences.token = null
        sharedPreferences.userId = UNREGISTERED
        val action = ProfileFragmentDirections.actionProfileFragmentToMainFragment()
        findNavController().navigate(action)
    }

    private fun showAddress() {
        val action = ProfileFragmentDirections.actionProfileFragmentToProfileAddressFragment(
            city = addressItem?.city,
            street = addressItem?.street,
            house = addressItem?.house,
            roomNumber = addressItem?.roomNumber,
            addressItemArray = addressList.toTypedArray()
        )
        findNavController().navigate(action)
    }

    private fun showName() {
        user?.let {
            val action = ProfileFragmentDirections.actionProfileFragmentToProfileNameDialogFragment(
                firstName = it.firstName,
                lastName = it.lastName,
                phone = it.phone
            )
            findNavController().navigate(action)
        }

    }

    private fun setState(appState: AppState<*>) = with(binding) {
        when (appState) {
            is AppState.Success<*> -> {
                when (val stateData = appState.data) {
                    is List<*> -> {
                        if (stateData.isEmpty()) Toast.makeText(
                            context,
                            getString(R.string.server_data_error),
                            Toast.LENGTH_SHORT
                        ).show()
                        else when (stateData.first()) {
                            is AddressItem -> {
                                @Suppress("UNCHECKED_CAST")
                                val data = appState.data as List<AddressItem>
                                if (data.isNotEmpty()) {
                                    val primaryAddress =
                                        data.findLast { it.primary } ?: data.first()
                                    primaryAddress.let {
                                        viewModel.getFormatAddress(it)
                                        addressItem = it
                                    }
                                    addressList = data
                                }
                            }
                            is OrderApprove -> {
                                @Suppress("UNCHECKED_CAST")
                                val data = appState.data as List<OrderApprove>
                                orders = data.toMutableList()
                                initRcView()
                            }
                            else -> throw RuntimeException("Wrong List type $stateData")
                        }
                    }

                    is String -> {
                        when (appState.case) {
                            TypeCase.FORMAT_NAME -> {
                                profileNameTextView.text = stateData
                            }
                            TypeCase.FORMAT_PHONE -> {
                                profilePhoneTextView.text = stateData
                            }
                            TypeCase.DEFAULT -> {}
                            TypeCase.ADDRESS -> {
                                profileAddressTextView.text = stateData
                            }
                        }

                    }
                    is UserDataModel -> {
                        if (sharedPreferences.basketId == UNREGISTERED.toInt()) {
                            sharedPreferences.basketId = stateData.basket?.basketId
                        }
                        viewModel.getFormatPhone(
                            stateData.phone,
                            getString(R.string.profile_phone_mask)
                        )
                        viewModel.getFormatName(
                            stateData,
                            getString(R.string.profile_add_name)
                        )
                        user = stateData

                    }
                    else -> throw RuntimeException("Wrong AppState type $appState")
                }
            }
            is AppState.Error -> {
                Toast.makeText(
                    context,
                    getText(R.string.smd_send_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> throw RuntimeException("Wrong AppState type $appState")
        }
    }

    private fun initRcView() = with(binding) {
        activeOrdersRcView.layoutManager = LinearLayoutManager(root.context)
        activeOrdersRcView.adapter = adapter
        activeOrdersRcView.itemAnimator?.changeDuration = 0
        adapter.setOrder(orders)
    }

    private fun cancelOrder(pos: Int) {
        val order = orders[pos]
        orders.remove(order)
        adapter.setOrder(orders)
        adapter.notifyItemRemoved(pos)
        viewModel.deleteOrder(order.id)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}