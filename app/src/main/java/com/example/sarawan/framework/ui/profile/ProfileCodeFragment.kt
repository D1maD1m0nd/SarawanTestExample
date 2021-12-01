package com.example.sarawan.framework.ui.profile

import android.os.Bundle
import android.os.CountDownTimer
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

    private lateinit var timer: CountDownTimer

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
        updateTimerText(TIMER_INIT_STRING)
        initTimer()
    }

    private fun initTimer() {
        timer = object : CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {
            override fun onTick(millis: Long) {
                val seconds = millis / TIMER_INTERVAL
                val zero = if (seconds < 10) ZERO else ""
                updateTimerText("$TWO_ZERO:$zero$seconds")
            }

            override fun onFinish() = with(binding) {
                profileCodeRequestAgainButton.isVisible = true
                profileCodeNoRequestAgainButton.isVisible = false
                profileCodeTimerTextView.visibility = View.INVISIBLE
            }
        }.start()
    }

    private fun initViews() = with(binding) {
        profileCodeCloseButton.setOnClickListener { dismiss() }

        profileCodeSendButton.setOnClickListener { sendCode() }
        profileCodeRequestAgainButton.setOnClickListener { sendCodeAgain() }

        profileCodeNoSendButton.isVisible = false
        profileCodeRequestAgainButton.isVisible = false

        val text = getString(R.string.profile_code_code_sent) + phoneNumber
        profileCodeSentTextView.text = text
    }

    private fun sendCodeAgain() = with(binding) {
        profileCodeRequestAgainButton.isVisible = false
        profileCodeNoRequestAgainButton.isVisible = true
        profileCodeTimerTextView.visibility = View.VISIBLE

        profileCodeSendButton.isVisible = true
        profileCodeNoSendButton.isVisible = false

        wrongCode = false
        setTitle()
        setCodeBorder()
        clearCodeText()

        initTimer()
    }

    private fun sendCode() = with(binding) {
        //эта часть проверена, ее потом подключим
        //ProfileSuccessFragment.newInstance().show(childFragmentManager, null)
        //а тут мы притворимся, что код вводен неверно
        profileCodeSendButton.isVisible = false
        profileCodeNoSendButton.isVisible = true

        wrongCode = true
        setTitle()
        setCodeBorder()
    }

    private fun updateTimerText(time: String) = with(binding) {
        val text = getString(R.string.profile_code_re_request_code_in) + " $time"
        profileCodeTimerTextView.text = text
    }

    private fun setTitle() = with(binding) {
        profileCodeEnterCodeTextView.isVisible = !wrongCode
        profileCodeWrongCodeTextView.isVisible = wrongCode
    }

    private fun clearCodeText() = with(binding) {
        profileCodeCode1TextView.setText("")
        profileCodeCode2TextView.setText("")
        profileCodeCode3TextView.setText("")
        profileCodeCode4TextView.setText("")
        profileCodeCode5TextView.setText("")
        profileCodeCode6TextView.setText("")
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
        timer.cancel()
        _binding = null
    }

    companion object {
        private const val TIMER_DURATION = 60_000L
        private const val TIMER_INTERVAL = 1_000L
        private const val ZERO = "0"
        private const val TWO_ZERO = "00"
        private const val TIMER_INIT_STRING = "01:00"

        fun newInstance() = ProfileCodeFragment()
    }
}