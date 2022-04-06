package ru.sarawan.android.framework.ui.profile.address_fragment

import android.content.DialogInterface
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import retrofit2.HttpException
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentProfileAddressDialogBinding
import ru.sarawan.android.framework.ui.profile.address_fragment.adapter.AddressAdapter
import ru.sarawan.android.framework.ui.profile.address_fragment.viewModel.ProfileAddressViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.utils.constants.AddressState
import ru.sarawan.android.utils.exstentions.setNavigationResult
import ru.sarawan.android.utils.exstentions.userId
import javax.inject.Inject

class ProfileAddressDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: ProfileAddressViewModel by lazy {
        viewModelFactory.get().create(ProfileAddressViewModel::class.java)
    }

    private var _binding: FragmentProfileAddressDialogBinding? = null
    private val binding get() = _binding!!

    private val args: ProfileAddressDialogFragmentArgs by navArgs()
    private var isSaveSuccess = false

    private var inputMethodManager: InputMethodManager? = null
    private var keyboardShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileAddressDialogBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
        inputMethodManager = requireActivity().getSystemService()
        initViews()
        viewModel.getStateLiveData()
            .observe(viewLifecycleOwner) { appState: AppState<*> -> setState(appState) }
    }

    private fun showKeyboard() {
        @Suppress("DEPRECATION")
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        keyboardShown = true
    }

    private fun hideKeyboard() = if (keyboardShown) {
        inputMethodManager
            ?.hideSoftInputFromWindow(binding.profileAddressRootView.windowToken, 0)
        keyboardShown = false
    } else Unit

    private fun initViews() = with(binding) {
        args.street.let { profileAddressStreetEditText.setText(it) }
        args.house.let { profileAddressHouseEditText.setText(it) }
        args.roomNumber.let { profileAddressApartmentEditText.setText(it) }
        args.addressItemArray.let { addressList ->
            addressList?.let {
                val adapter = AddressAdapter(root.context, it.toList())

                addressAutoCompleteTextView.setAdapter(adapter)
            }
        }

        profileAddressBackButton.setOnClickListener {
            hideKeyboard()
            isSaveSuccess = false
            findNavController().navigateUp()
        }

        profileAddressSaveButton.setOnClickListener { onSaved() }
        profileAddressCityTextView.setOnClickListener { showAlert() }

        profileAddressStreetEditText.requestFocus()
        showKeyboard()
    }

    private fun showAlert() {
        hideKeyboard()
        val action = ProfileAddressDialogFragmentDirections
            .actionProfileAddressDialogFragmentToProfileAlertDialogFragment()
        findNavController().navigate(action)
    }

    private fun onSaved() {
        val address = getAddress()
        viewModel.validateAddress(address)
    }

    private fun setError(error: AddressState) = with(binding) {
        when (error) {
            AddressState.STREET -> {
                profileAddressStreetEditText.error = getString(R.string.street_empty)
            }
            AddressState.HOUSE -> {
                profileAddressHouseEditText.error = getString(R.string.house_empty)
            }
            AddressState.NUMBER -> {
                profileAddressApartmentEditText.error = getString(R.string.room_number_empty)
            }
            else -> {}
        }
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
            user = userId ?: 0
        )
    }

    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                when (val item = appState.data) {
                    is AddressItem -> {
                        Toast.makeText(
                            context,
                            getString(R.string.save_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        hideKeyboard()
                        isSaveSuccess = true
                        findNavController().navigateUp()
                    }

                    is AddressState -> {
                        if (item != AddressState.VALID) setError(item)
                        else saveData()
                    }

                    else -> throw RuntimeException("Wrong AppState type $appState")
                }
            }

            is AppState.Error -> {
                val error = appState.error
                if (error is HttpException) {
                    Toast.makeText(context, error.response().toString(), Toast.LENGTH_SHORT).show()
                }
            }
            else -> throw RuntimeException("Wrong AppState type $appState")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setNavigationResult(KEY_ADDRESS, isSaveSuccess)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        inputMethodManager = null
    }

    companion object {
        const val KEY_ADDRESS = "Address"
    }
}