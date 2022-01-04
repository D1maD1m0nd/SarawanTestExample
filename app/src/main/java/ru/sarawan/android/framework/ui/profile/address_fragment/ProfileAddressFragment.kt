package ru.sarawan.android.framework.ui.profile.address_fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import retrofit2.HttpException
import ru.sarawan.android.databinding.FragmentProfileAddressBinding
import ru.sarawan.android.framework.ui.modals.ProfileAlertFragment
import ru.sarawan.android.framework.ui.profile.address_fragment.viewModel.ProfileAddressViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.utils.exstentions.userId
import javax.inject.Inject

class ProfileAddressFragment : DialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val viewModel: ProfileAddressViewModel by lazy {
        viewModelFactory.create(ProfileAddressViewModel::class.java)
    }

    private var _binding: FragmentProfileAddressBinding? = null
    private val binding get() = _binding!!

    private var userCity: String? = null
    private var userStreet: String? = null
    private var userHouse: String? = null
    private var userRoomNumber: String? = null
    private var onSaveDataCallback: (() -> Unit)? = null

    private var inputMethodManager: InputMethodManager? = null
    private var keyboardShown = false

    private fun showKeyboard() {
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        keyboardShown = true
    }

    private fun hideKeyboard() {
        if (keyboardShown) {
            inputMethodManager?.hideSoftInputFromWindow(
                binding.profileAddressRootView.windowToken,
                0
            )
            keyboardShown = false
        }
    }

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
        inputMethodManager = requireActivity().getSystemService()
        initViews()
    }

    private fun initViews() = with(binding) {
        //город не обновляем
        //userCity?.let {  }
        //подъезда почему-то тоже нет в приходящем адресе

        userStreet.let { profileAddressStreetEditText.setText(it) }
        userHouse.let { profileAddressHouseEditText.setText(it) }
        userRoomNumber.let { profileAddressApartmentEditText.setText(it) }

        profileAddressBackButton.setOnClickListener {
            hideKeyboard()
            dismiss()
        }
        profileAddressSaveButton.setOnClickListener { saveData() }
        profileAddressCityTextView.setOnClickListener { showAlert() }

        profileAddressStreetEditText.requestFocus()
        showKeyboard()
    }

    private fun showAlert() {
        hideKeyboard()
        ProfileAlertFragment.newInstance().show(childFragmentManager, null)
    }

    private fun saveData() {
        val address = getAddress()
        viewModel.createAddress(address)
    }

    private fun getAddress(): AddressItem = with(binding) {
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
                Toast.makeText(context, "Сохранение прошло успешно", Toast.LENGTH_SHORT).show()
                hideKeyboard()
                onSaveDataCallback?.invoke()
                dismiss()
            }

            is AppState.Error -> {
                val error = appState.error
                if(error is HttpException) {
                    Toast.makeText(
                        context,
                        error.response().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            AppState.Loading -> Unit
            AppState.Empty -> Unit
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        inputMethodManager = null
    }

    companion object {
        //для совместимости с вызовом Димы из корзины переопределил временно
        fun newInstance() = newInstance(null, null)

        fun newInstance(addressItem: AddressItem?, onSaveDataCallback: (() -> Unit)?) =
            ProfileAddressFragment().apply {
                this.onSaveDataCallback = onSaveDataCallback
                addressItem?.let {
                    userCity = it.city
                    userStreet = it.street
                    userHouse = it.house
                    userRoomNumber = it.roomNumber
                }
            }
    }
}