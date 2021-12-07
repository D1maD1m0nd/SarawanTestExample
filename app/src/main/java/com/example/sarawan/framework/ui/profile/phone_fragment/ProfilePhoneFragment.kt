package com.example.sarawan.framework.ui.profile.phone_fragment

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
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.sarawan.R
import com.example.sarawan.databinding.FragmentProfilePhoneBinding
import com.example.sarawan.framework.ui.profile.phone_fragment.viewModel.ProfilePhoneViewModel
import com.example.sarawan.framework.ui.profile.sms_code_fragment.ProfileCodeFragment
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.UserRegistration
import dagger.android.support.AndroidSupportInjection
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

    private lateinit var inputMethodManager: InputMethodManager

    private fun showKeyboard() =
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

    private fun hideKeyboard(view: View) =
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

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

        inputMethodManager = requireActivity()
            .getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager

        initViews()
    }

    private fun initViews() = with(binding) {
        profilePhoneCloseButton.setOnClickListener { dismiss() }
        profilePhoneSendButton.setOnClickListener { sendCode() }
        profilePhoneAgreementTextView.setOnClickListener { showAgreement() }
        profilePhoneCheckbox.setOnClickListener { toggleCheckBox() }

        initAgreementTextView()
        toggleCheckBox()

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
        return number
            .replace("(", "")
            .replace(")", "")
            .replace("_", "")
            .replace("-", "")
            .replace(" ", "")
    }

    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                appState.data as MutableList<UserRegistration>
                if (appState.data.isNotEmpty()) {
                    val result = appState.data.first()
                    result.success?.let {
                        if (it) {
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
    }

    companion object {
        fun newInstance(callback: () -> Unit) =
            ProfilePhoneFragment().apply {
                this.callback = callback
            }
    }
}