package ru.sarawan.android.framework.ui.profile.sms_code_fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import ru.sarawan.android.R
import ru.sarawan.android.databinding.FragmentProfileCodeDialogBinding
import ru.sarawan.android.framework.ui.profile.sms_code_fragment.viewModel.SmsCodeViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.UserRegistration
import ru.sarawan.android.utils.exstentions.token
import ru.sarawan.android.utils.exstentions.userId
import javax.inject.Inject

class ProfileCodeDialogFragment : DialogFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>

    private val viewModel: SmsCodeViewModel by lazy {
        viewModelFactory.get().create(SmsCodeViewModel::class.java)
    }

    private var _binding: FragmentProfileCodeDialogBinding? = null
    private val binding get() = _binding!!

    private val args: ProfileCodeDialogFragmentArgs by navArgs()

    private var wrongCode = false

    private lateinit var timer: CountDownTimer

    private var inputMethodManager: InputMethodManager? = null
    private var keyboardShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileCodeDialogBinding
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
        setTitle()
        setCodeBorder()
        updateTimerText(TIMER_INIT_STRING)
        initTimer()
        viewModel.getStateLiveData()
            .observe(viewLifecycleOwner) { appState: AppState<*> -> setState(appState) }
    }

    private fun showKeyboard() {
        @Suppress("DEPRECATION")
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        keyboardShown = true
    }

    private fun hideKeyboard() {
        if (keyboardShown) {
            inputMethodManager?.hideSoftInputFromWindow(binding.profileCodeRootView.windowToken, 0)
            keyboardShown = false
        }
    }

    private val profileCodeEdits by lazy {
        with(binding) {
            arrayOf(
                profileCodeCode1EditText,
                profileCodeCode2EditText,
                profileCodeCode3EditText,
                profileCodeCode4EditText
            )
        }
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
        profileCodeCloseButton.setOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }

        profileCodeSendButton.setOnClickListener { sendCode() }
        profileCodeRequestAgainButton.setOnClickListener { sendCodeAgain() }

        profileCodeNoSendButton.isVisible = false
        profileCodeRequestAgainButton.isVisible = false

        val text = getString(R.string.profile_code_code_sent) + args.number
        profileCodeSentTextView.text = text

        initCodeTextViews()
    }

    private fun initCodeTextViews() = with(binding) {
        profileCodeEdits.forEach { edit ->
            edit.doOnTextChanged { text, _, _, _ ->
                text?.let {
                    //сначала узнаем, на каком мы месте
                    val currentIndex = profileCodeEdits.indexOf(edit)
                    if (it.length == 1) {
                        //ввели символ, нужно переключиться на другой
                        //если это не последний
                        if (currentIndex == profileCodeEdits.size - 1) {
                            hideKeyboard()
                        } else {
                            profileCodeEdits[currentIndex + 1].requestFocus()
                        }
                    }
                }
            }
        }

        profileCodeCode1EditText.requestFocus()
        showKeyboard()
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
        if (code.length == DEFAULT_CODE_LENGTH) {
            val user = UserRegistration(code = code, phoneNumber = number)
            viewModel.createUser(user)
        }
    }

    private fun getSmsCode() = concatCode()

    private fun concatCode() = StringBuilder()
        .also { profileCodeEdits.forEach { tv -> it.append(tv.text.toString()) } }.toString()

    private fun getFormatNumber(): String = args.number.replace(Regex("\\D"), "")

    private fun updateTimerText(time: String) = with(binding) {
        val text = getString(R.string.profile_code_re_request_code_in) + " $time"
        profileCodeTimerTextView.text = text
    }

    private fun setTitle() = with(binding) {
        profileCodeEnterCodeTextView.isVisible = !wrongCode
        profileCodeWrongCodeTextView.isVisible = wrongCode
    }

    private fun clearCodeText() = profileCodeEdits.forEach { it.setText("") }

    private fun setCodeBorder() {
        val border = AppCompatResources.getDrawable(
            requireContext(),
            if (wrongCode) R.drawable.rectangle_red_shape
            else R.drawable.rectangle_gray_shape
        )
        profileCodeEdits.forEach { it.background = border }
    }

    private fun setState(appState: AppState<*>) = with(binding) {
        when (appState) {
            is AppState.Success<*> -> {
                if (appState.data is UserRegistration) {
                    sharedPreferences.token = appState.data.token
                    sharedPreferences.userId = appState.data.userId
                    hideKeyboard()
                    val action = ProfileCodeDialogFragmentDirections
                        .actionProfileCodeDialogFragmentToProfileSuccessDialogFragment()
                    findNavController().navigate(action)
                } else throw RuntimeException("Wrong AppState type $appState")
            }
            is AppState.Error -> {
                //при неверном коде возвращает ошибку 500
                profileCodeSendButton.isVisible = false
                profileCodeNoSendButton.isVisible = true
                wrongCode = true
                setTitle()
                setCodeBorder()
            }
            else -> throw RuntimeException("Wrong AppState type $appState")
        }
    }

    override fun onDestroyView() {
        timer.cancel()
        _binding = null
        inputMethodManager = null
        super.onDestroyView()
    }

    companion object {
        private const val DEFAULT_CODE_LENGTH = 4
        private const val TIMER_DURATION = 60_000L
        private const val TIMER_INTERVAL = 1_000L
        private const val ZERO = "0"
        private const val TWO_ZERO = "00"
        private const val TIMER_INIT_STRING = "01:00"
    }
}