package com.example.sarawan.framework.ui.profile.sms_code_fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.sarawan.R
import com.example.sarawan.app.App.Companion.sharedPreferences
import com.example.sarawan.databinding.FragmentProfileCodeBinding
import com.example.sarawan.framework.ui.modals.ProfileSuccessFragment
import com.example.sarawan.framework.ui.profile.sms_code_fragment.viewModel.SmsCodeViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.UserRegistration
import com.example.sarawan.utils.exstentions.token
import com.example.sarawan.utils.exstentions.userId
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ProfileCodeFragment : DialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SmsCodeViewModel by lazy {
        viewModelFactory.create(SmsCodeViewModel::class.java)
    }

    private var _binding: FragmentProfileCodeBinding? = null
    private val binding get() = _binding!!

    private var wrongCode = false
    private var phoneNumber = ""

    private lateinit var timer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            phoneNumber = it.getString(ARG_NUMBER, DEFAULT_PHONE_MASK)
        }
        AndroidSupportInjection.inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileCodeBinding
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
        val code = getSmsCode()
        val number = getFormatNumber()
        if(code.length == DEFAULT_CODE_LENGTH) {
            val user = UserRegistration(code = code, phoneNumber = number)
            viewModel.createUser(user)
        }
    }

    private fun getSmsCode() : String = with(binding) {
        return profileCodeCode1TextView.text.toString() + profileCodeCode2TextView.text.toString() + profileCodeCode3TextView.text.toString() + profileCodeCode4TextView.text.toString() + profileCodeCode5TextView.text.toString() + profileCodeCode6TextView.text.toString()
    }

    private fun getFormatNumber() : String{
        return phoneNumber
            .replace("(", "")
            .replace(")", "")
            .replace("_","")
            .replace("-", "")
            .replace(" ", "")
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

    private fun setState(appState: AppState<*>) = with(binding) {
        when (appState) {
            is AppState.Success<*> -> {
                appState.data as MutableList<UserRegistration>
                if(appState.data.isNotEmpty()) {
                    val result = appState.data.first()
                    sharedPreferences.token = result.token
                    sharedPreferences.userId = result.userId
                    ProfileSuccessFragment.newInstance().show(childFragmentManager, null)
                }
            }
            is AppState.Error -> {
                //при неверном коде возвращает ошибку 500
                profileCodeSendButton.isVisible = false
                profileCodeNoSendButton.isVisible = true
                wrongCode = true
                setTitle()
                setCodeBorder()
            }
            AppState.Loading -> Unit
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        _binding = null
    }

    companion object {
        private const val DEFAULT_CODE_LENGTH = 6
        private const val DEFAULT_PHONE_MASK = "+7 000 000 00 00"
        private const val TIMER_DURATION = 60_000L
        private const val TIMER_INTERVAL = 1_000L
        private const val ZERO = "0"
        private const val TWO_ZERO = "00"
        private const val TIMER_INIT_STRING = "01:00"
        private const val ARG_NUMBER = "PHONE NUMBER"
        fun newInstance(number : String) = ProfileCodeFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_NUMBER, number)
            }
        }
    }
}