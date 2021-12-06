package com.example.sarawan.framework.ui.profile.address_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.sarawan.app.App.Companion.sharedPreferences
import com.example.sarawan.databinding.FragmentProfileAddressBinding
import com.example.sarawan.framework.ui.modals.ProfileAlertFragment
import com.example.sarawan.framework.ui.profile.address_fragment.viewModel.ProfileAddressViewModel
import com.example.sarawan.model.data.Address
import com.example.sarawan.model.data.AddressItem
import com.example.sarawan.model.data.AppState
import com.example.sarawan.utils.exstentions.userId
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ProfileAddressFragment : DialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ProfileAddressViewModel by lazy {
        viewModelFactory.create(ProfileAddressViewModel::class.java)
    }

    private var _binding: FragmentProfileAddressBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileAddressBinding
        .inflate(inflater, container, false)
        .also {
            viewModel.getStateLiveData().observe(viewLifecycleOwner) { appState: AppState<*> ->
                setState(appState)
            }
            _binding = it
        }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
        initViews()
    }

    private fun initViews() = with(binding) {
        profileAddressBackButton.setOnClickListener { dismiss() }
        profileAddressSaveButton.setOnClickListener { saveData() }
        profileAddressCityTextView.setOnClickListener { showAlert() }
    }

    private fun showAlert() {
        ProfileAlertFragment.newInstance().show(childFragmentManager, null)
    }

    private fun saveData() {
        val address =  getAddress()
        viewModel.createAddress(address)
    }

    private fun getAddress() : AddressItem = with(binding) {
        val userId = sharedPreferences.userId
        val city = profileAddressCityTextView.text.toString()
        val street = profileAddressStreetEditText.text.toString()
        val house = profileAddressHouseEditText.text.toString()
        val apartment = profileAddressApartmentEditText.text.toString()
        return AddressItem(
            city = city,
            street = street,
            house = house,
            roomNumber = apartment,
            primary = true,
            user = userId
        )
    }

    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                appState.data as MutableList<Address>
            }
            is AppState.Error -> {
                Toast.makeText(context,
                    "При отправке смс кода произошла ошибка, повторите попытку позднее",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            AppState.Loading -> Unit
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfileAddressFragment()
    }
}