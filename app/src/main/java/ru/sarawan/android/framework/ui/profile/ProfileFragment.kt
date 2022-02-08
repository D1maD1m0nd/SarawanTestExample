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
import ru.sarawan.android.utils.exstentions.*
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ProfileViewModel by lazy {
        viewModelFactory.create(ProfileViewModel::class.java)
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var user: UserDataModel? = null

    private var addressItem: AddressItem? = null

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
            roomNumber = addressItem?.roomNumber
        )
        findNavController().navigate(action)
    }

    private fun showName() {
        val action = ProfileFragmentDirections.actionProfileFragmentToProfileNameDialogFragment(
            firstName = user?.firstName,
            lastName = user?.lastName
        )
        findNavController().navigate(action)
    }

    private fun setState(appState: AppState<*>) = with(binding) {
        when (appState) {
            is AppState.Success<*> -> {
                if (appState.data.isNotEmpty()) {
                    when (val firstItem = appState.data.first()) {
                        is AddressItem -> {
                            val data = appState.data as MutableList<AddressItem>
                            if (data.isNotEmpty()) {
                                val primaryAddress =
                                    data.findLast { it.primary } ?: data.first()
                                primaryAddress.let {
                                    val address = formatAddress(it)
                                    profileAddressTextView.text = address
                                    addressItem = it
                                }
                            }
                        }
                        is UserDataModel -> {
                            if (sharedPreferences.basketId == UNREGISTERED.toInt())
                                sharedPreferences.basketId = firstItem.basket?.basketId
                            profilePhoneTextView.text = formatPhone(firstItem.phone)
                            val name = formatName(firstItem)
                            profileNameTextView.text = name
                            user = firstItem
                        }

                        is OrderApprove -> {
                            val data = appState.data as MutableList<OrderApprove>
                            orders = data
                            initRcView()
                        }
                    }
                }
            }
            is AppState.Error -> {
                Toast.makeText(
                    context,
                    "При отправке смс кода произошла ошибка, повторите попытку позднее",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> Unit
        }
    }

    private fun initRcView() = with(binding) {
        activeOrdersRcView.layoutManager = LinearLayoutManager(root.context)
        activeOrdersRcView.adapter = adapter
        activeOrdersRcView.itemAnimator?.changeDuration = 0
        adapter.setOrder(orders)
    }

    private fun formatPhone(number: String?): String =
        number?.let {
            var index = 0
            getString(R.string.profile_phone_mask)
                .asSequence()
                .map { c ->
                    if (index < number.length) {
                        val cc = number[index]
                        if (cc == c || c == '9') {
                            index++
                            cc
                        } else c
                    } else c
                }.joinToString("")
        }.orEmpty()

    private fun formatAddress(address: AddressItem): String {
        val city = address.city
        val street = address.street
        val house = address.house
        val roomNum = address.roomNumber
        return "$city, ул $street, д $house, кв $roomNum"
    }

    private fun formatName(user: UserDataModel): String {
        val firstName = user.firstName
        val lastName = user.lastName
        val fullName = "$firstName $lastName".trim()
        return fullName.ifEmpty { getString(R.string.profile_add_name) }
    }

    private fun cancelOrder(pos: Int) {
        val order = orders[pos]
        val id = order.orderId
        orders.remove(order)

        adapter.setOrder(orders)
        adapter.notifyItemRemoved(pos)
        viewModel.deleteOrder(id)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}