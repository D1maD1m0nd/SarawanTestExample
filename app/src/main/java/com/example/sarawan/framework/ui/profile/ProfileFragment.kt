package com.example.sarawan.framework.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sarawan.R
import com.example.sarawan.app.App.Companion.navController
import com.example.sarawan.databinding.FragmentProfileBinding
import com.example.sarawan.framework.ui.profile.address_fragment.ProfileAddressFragment
import com.example.sarawan.framework.ui.profile.name_fragment.ProfileNameFragment
import com.example.sarawan.framework.ui.profile.phone_fragment.ProfilePhoneFragment
import com.example.sarawan.framework.ui.profile.viewModel.ProfileViewModel
import com.example.sarawan.model.data.AddressItem
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.UserDataModel
import com.example.sarawan.utils.exstentions.basketId
import com.example.sarawan.utils.exstentions.token
import com.example.sarawan.utils.exstentions.userId
import dagger.android.support.AndroidSupportInjection
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
    private var user : UserDataModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileBinding
        .inflate(inflater, container, false)
        .also {
            viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState: AppState<*> ->
                setState(appState)
            }
            _binding = it
        }
        .root

    private fun getUserData() {
        if (sharedPreferences.userId != -1L) {
            sharedPreferences.userId?.let {
                viewModel.getUserData(it)
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
        sharedPreferences.userId = -1L
        navController.navigate(R.id.mainFragment)
    }

    private fun showAddress() {
        ProfileAddressFragment.newInstance().show(childFragmentManager, null)
    }

    private fun showPhone() {
        ProfilePhoneFragment.newInstance { callback() }
            .show(requireActivity().supportFragmentManager, null)
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
                                val primaryAddress = data.findLast { it.primary == true }
                                primaryAddress?.let {
                                    val address = formatAddress(it)
                                    profileAddressTextView.text = address
                                }
                            }
                        }
                        is UserDataModel -> {
                            if (sharedPreferences.basketId == -1) {
                                sharedPreferences.basketId = firstItem.basket?.basketId
                            }
                            profilePhoneTextView.text = firstItem.phone
                            val name = formatName(firstItem)
                            profileNameTextView.text = name

                            user = firstItem
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
        }
    }

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
        return if (fullName.isNotEmpty()) {
            fullName
        } else {
            getString(R.string.profile_add_name)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}