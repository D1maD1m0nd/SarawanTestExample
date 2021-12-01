package com.example.sarawan.framework.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.sarawan.R
import com.example.sarawan.databinding.FragmentProfileCodeBinding

class ProfileCodeFragment : DialogFragment() {

    private var _binding: FragmentProfileCodeBinding? = null
    private val binding get() = _binding!!

    private var wrongCode = false
    private val phoneNumber = "+7 000 000 00 00"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileCodeBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
        }
        initViews()
        setTitle()
        setCodeBorder()
        updateTimerText("01:00")
    }

    private fun initViews() = with(binding) {
        profileCodeCloseButton.setOnClickListener { dismiss() }

        profileCodeSendButton.setOnClickListener { sendCode() }

        profileCodeNoSendButton.isVisible = false
        profileCodeRequestAgainButton.isVisible = false

        val text = getString(R.string.profile_code_code_sent) + phoneNumber
        profileCodeSentTextView.text = text
    }

    private fun sendCode() {
        ProfileSuccessFragment.newInstance().show(childFragmentManager, null)
    }

    private fun updateTimerText(time: String) = with(binding) {
        val text = getString(R.string.profile_code_re_request_code_in) + " $time"
        profileCodeTimerTextView.text = text
    }

    private fun setTitle() = with(binding) {
        profileCodeEnterCodeTextView.isVisible = !wrongCode
        profileCodeWrongCodeTextView.isVisible = wrongCode
    }

    private fun setCodeBorder() = with(binding) {
        val border = AppCompatResources.getDrawable(
            requireContext(),
            if (wrongCode) R.drawable.rectangle_red_shape
            else R.drawable.rectangle_gray_shape
        )
        profileCodeCode1TextView.background = border
        profileCodeCode2TextView.background = border
        profileCodeCode3TextView.background = border
        profileCodeCode4TextView.background = border
        profileCodeCode5TextView.background = border
        profileCodeCode6TextView.background = border
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfileCodeFragment()
    }
}