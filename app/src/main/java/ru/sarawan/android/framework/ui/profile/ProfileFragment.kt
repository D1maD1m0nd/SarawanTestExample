package ru.sarawan.android.framework.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import ru.sarawan.android.R
import ru.sarawan.android.app.App.Companion.navController
import ru.sarawan.android.databinding.FragmentProfileBinding
import ru.sarawan.android.framework.ui.profile.adapter.ItemClickListener
import ru.sarawan.android.framework.ui.profile.adapter.OrdersAdapter
import ru.sarawan.android.framework.ui.profile.address_fragment.ProfileAddressFragment
import ru.sarawan.android.framework.ui.profile.name_fragment.ProfileNameFragment
import ru.sarawan.android.framework.ui.profile.viewModel.ProfileViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.OrderApprove
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.utils.exstentions.UNREGISTERED
import ru.sarawan.android.utils.exstentions.basketId
import ru.sarawan.android.utils.exstentions.token
import ru.sarawan.android.utils.exstentions.userId
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

    //нам нужно отдельно иметь сохраненные имя и фамилию, чтобы передавать в фрагмент
    //их редактирования, если они не пустые
    private var user: UserDataModel? = null

    //аналогично и для адреса
    private var addressItem: AddressItem? = null

    private val itemClickListener = object : ItemClickListener {
        override fun cancel(pos : Int) {
            cancelOrder(pos)
        }

    }
    private var orders : MutableList<OrderApprove> = ArrayList()
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
        .also {
            viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState: AppState<*> ->
                setState(appState)
            }
            _binding = it
        }
        .root

    private fun getUserData() {
        if (sharedPreferences.userId != UNREGISTERED) {
            sharedPreferences.userId?.let {
                viewModel.getUserData(it)
                viewModel.getOrders()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserData()
        initViews()
    }

    private fun initViews() = with(binding) {
        profileNameLayout.setOnClickListener { showName() }
        profilePhoneLayout.setOnClickListener { showPhone() }
        profileAddressLayout.setOnClickListener { showAddress() }

        profileNameButton.setOnClickListener { showName() }
        profilePhoneButton.setOnClickListener { showPhone() }
        profileAddressButton.setOnClickListener { showAddress() }

        profileExitButton.setOnClickListener { leave() }
    }

    private fun leave() {
        sharedPreferences.token = null
        sharedPreferences.userId = UNREGISTERED
        navController.navigate(R.id.mainFragment)
    }

    private fun showAddress() {
        ProfileAddressFragment.newInstance(addressItem) {
            getUserData()
        }
            .show(childFragmentManager, null)
    }

    private fun showPhone() {
        /*
        оставил, вдруг понадобится протестировать еще что-то без выхода
        ProfilePhoneFragment.newInstance { callback() }
            .show(requireActivity().supportFragmentManager, null)
        */
    }

    private fun callback() {
        navController.navigate(R.id.profileFragment)
    }

    private fun showName() {
        ProfileNameFragment.newInstance(user) {
            getUserData()
        }
            .show(childFragmentManager, null)
    }

    private fun setState(appState: AppState<*>) = with(binding) {
        when (appState) {
            is AppState.Success<*> -> {
                if (appState.data.isNotEmpty()) {
                    when (val firstItem = appState.data.first()) {
                        is AddressItem -> {
                            val data = appState.data as MutableList<AddressItem>
                            if (data.isNotEmpty()) {
                                val primaryAddress = data.findLast { it.primary == true } ?: data.first()
                                primaryAddress.let {
                                    val address = formatAddress(it)
                                    profileAddressTextView.text = address

                                    addressItem = it
                                }
                            }
                        }
                        is UserDataModel -> {
                            if (sharedPreferences.basketId == UNREGISTERED.toInt()) {
                                sharedPreferences.basketId = firstItem.basket?.basketId
                            }
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
                )
                    .show()
            }
            AppState.Loading -> Unit
            AppState.Empty -> TODO()
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
        } ?: ""

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
        return fullName.ifEmpty {
            getString(R.string.profile_add_name)
        }
    }

    private fun cancelOrder(pos : Int) {
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

    companion object {
        fun newInstance() = ProfileFragment()
    }
}