package com.example.sarawan.framework.ui.profile

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
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.sarawan.R
import com.example.sarawan.databinding.FragmentProfilePhoneBinding

class ProfilePhoneFragment : DialogFragment() {

    private var _binding: FragmentProfilePhoneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = FragmentProfilePhoneBinding
            .inflate(inflater, container, false)
            .also { _binding = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
        }
        initViews()
    }

    private fun initViews() = with(binding) {
        profilePhoneCloseButton.setOnClickListener { dismiss() }
        profilePhoneSendButton.setOnClickListener { sendCode() }
        profilePhoneAgreementTextView.setOnClickListener { showAgreement() }
        profilePhoneCheckbox.setOnClickListener { toggleCheckBox() }
        toggleCheckBox()

        val spannable = SpannableString(getString(R.string.profile_phone_user_agreement))
        spannable.setSpan(
                ForegroundColorSpan(Color.BLACK),
                0, 38,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
                UnderlineSpan(),
                37, spannable.length,
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
        Toast.makeText(context, "Отправить код в СМС!", Toast.LENGTH_SHORT)
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfilePhoneFragment()
    }
}