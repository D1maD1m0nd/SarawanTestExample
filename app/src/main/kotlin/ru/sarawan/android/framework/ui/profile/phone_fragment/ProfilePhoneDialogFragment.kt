package ru.sarawan.android.framework.ui.profile.phone_fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentProfilePhoneDialogBinding
import ru.sarawan.android.framework.ui.profile.phone_fragment.viewModel.ProfilePhoneViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.UserRegistration
import javax.inject.Inject

class ProfilePhoneDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>

    private val viewModel: ProfilePhoneViewModel by lazy {
        viewModelFactory.get().create(ProfilePhoneViewModel::class.java)
    }

    private var _binding: FragmentProfilePhoneDialogBinding? = null
    private val binding get() = _binding!!

    private var inputMethodManager: InputMethodManager? = null
    private var keyboardShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfilePhoneDialogBinding
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

    private fun initViews() = with(binding) {
        profilePhoneCloseButton.setOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        profilePhoneSendButton.setOnClickListener { sendCode() }
        profilePhoneAgreementTextView.setOnClickListener { showAgreement() }
        profilePhoneCheckbox.setOnClickListener { toggleCheckBox() }

        initAgreementTextView()
        toggleCheckBox()

        profilePhoneMaskedEditText.doOnTextChanged { text, _, _, _ ->
            text?.let {
                val number = phoneNumberWithoutMask(it.toString())
                if (number.length == 12) {
                    hideKeyboard()
                }
            }
        }

        profilePhoneMaskedEditText.requestFocus()
        showKeyboard()
    }

    private fun showKeyboard() {
        @Suppress("DEPRECATION")
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        keyboardShown = true
    }

    private fun hideKeyboard() {
        if (keyboardShown) {
            inputMethodManager?.hideSoftInputFromWindow(binding.profilePhoneRootView.windowToken, 0)
            keyboardShown = false
        }
    }

    private fun initAgreementTextView() = with(binding) {
        val start = 0
        val end = 38
        val spannable = SpannableString(getString(R.string.profile_phone_user_agreement))
        spannable.setSpan(
            ForegroundColorSpan(Color.BLACK),
            start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            UnderlineSpan(),
            end, spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        profilePhoneAgreementTextView.text = spannable
    }

    private fun toggleCheckBox() = with(binding) {
        profilePhoneSendButton.isVisible = profilePhoneCheckbox.isChecked
        profilePhoneNoSendButton.isVisible = !profilePhoneCheckbox.isChecked
    }

    private fun showAgreement() = Toast
        .makeText(context, getString(R.string.show_user_reference), Toast.LENGTH_SHORT).show()

    private fun sendCode() {
        val number = getFormatNumber()
        if (number.length == 11) {
            val user = UserRegistration(phoneNumber = number)
            viewModel.sendSms(user)
        } else Toast.makeText(context, getString(R.string.incorrect_number), Toast.LENGTH_SHORT)
            .show()
    }

    private fun getFormatNumber(): String {
        val number = binding.profilePhoneMaskedEditText.text.toString()
        return phoneNumberWithoutMask(number)
    }

    private fun phoneNumberWithoutMask(number: String) = number.replace(Regex("\\D"), "")

    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                if (appState.data is UserRegistration) {
                    val result = appState.data
                    result.success.let {
                        if (it) {
                            hideKeyboard()
                            val number = binding.profilePhoneMaskedEditText.text.toString()
                            val action = ProfilePhoneDialogFragmentDirections
                                .actionProfilePhoneDialogFragmentToProfileCodeDialogFragment(number)
                            findNavController().navigate(action)
                        }
                    }
                } else throw RuntimeException("Wrong AppState type $appState")
            }
            is AppState.Error -> {
                Toast.makeText(
                    context,
                    getString(R.string.smd_send_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> throw RuntimeException("Wrong AppState type $appState")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        inputMethodManager = null
    }
}