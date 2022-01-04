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
import dagger.android.support.AndroidSupportInjection
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentProfilePhoneBinding
import ru.sarawan.android.framework.ui.profile.phone_fragment.viewModel.ProfilePhoneViewModel
import ru.sarawan.android.framework.ui.profile.sms_code_fragment.ProfileCodeFragment
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.UserRegistration
import javax.inject.Inject

class ProfilePhoneFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ProfilePhoneViewModel by lazy {
        viewModelFactory.create(ProfilePhoneViewModel::class.java)
    }

    private var _binding: FragmentProfilePhoneBinding? = null
    private val binding get() = _binding!!

    private lateinit var callback: () -> Unit

    private var inputMethodManager: InputMethodManager? = null
    private var keyboardShown = false

    private fun showKeyboard() {
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        keyboardShown = true
    }

    private fun hideKeyboard() {
        if (keyboardShown) {
            inputMethodManager?.hideSoftInputFromWindow(binding.profilePhoneRootView.windowToken, 0)
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
    ): View = FragmentProfilePhoneBinding
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
        profilePhoneCloseButton.setOnClickListener {
            hideKeyboard()
            dismiss()
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

    private fun initAgreementTextView() = with(binding) {
        val spannable = SpannableString(getString(R.string.profile_phone_user_agreement))
        spannable.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0, 38,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            UnderlineSpan(),
            38, spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        profilePhoneAgreementTextView.text = spannable
    }

    private fun toggleCheckBox() = with(binding) {
        profilePhoneSendButton.isVisible = profilePhoneCheckbox.isChecked
        profilePhoneNoSendButton.isVisible = !profilePhoneCheckbox.isChecked
    }

    private fun showAgreement() {
        Toast.makeText(context, "Показать пользовательское соглашение!", Toast.LENGTH_SHORT)
            .show()
    }

    private fun sendCode() {
        val number = getFormatNumber()
        if (number.length == 12) {
            val user = UserRegistration(phoneNumber = number)
            viewModel.sendSms(user)
        } else {
            Toast.makeText(context, "Номер не корректный", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFormatNumber(): String {
        val number = binding.profilePhoneMaskedEditText.text.toString()
        return phoneNumberWithoutMask(number)
    }

    private fun phoneNumberWithoutMask(number: String) = number
        .replace("(", "")
        .replace(")", "")
        .replace("_", "")
        .replace("-", "")
        .replace(" ", "")

    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                appState.data as MutableList<UserRegistration>
                if (appState.data.isNotEmpty()) {
                    val result = appState.data.first()
                    result.success?.let {
                        if (it) {
                            hideKeyboard()
                            val number = binding.profilePhoneMaskedEditText.text.toString()
                            ProfileCodeFragment.newInstance(
                                callback,
                                number
                            ).show(childFragmentManager, null)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        inputMethodManager = null
    }

    companion object {
        fun newInstance(callback: () -> Unit) =
            ProfilePhoneFragment().apply {
                this.callback = callback
            }
    }
}