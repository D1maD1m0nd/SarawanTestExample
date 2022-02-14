package ru.sarawan.android.framework.ui.profile.name_fragment

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
import dagger.android.support.AndroidSupportInjection
import ru.sarawan.android.databinding.FragmentProfileNameDialogBinding
import ru.sarawan.android.framework.ui.profile.name_fragment.viewModel.NameViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.utils.exstentions.UNREGISTERED
import ru.sarawan.android.utils.exstentions.setNavigationResult
import ru.sarawan.android.utils.exstentions.userId
import javax.inject.Inject

class ProfileNameDialogFragment : DialogFragment() {
    var phone = "";
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: NameViewModel by lazy {
        viewModelFactory.create(NameViewModel::class.java)
    }

    private var _binding: FragmentProfileNameDialogBinding? = null
    private val binding get() = _binding!!

    private val args: ProfileNameDialogFragmentArgs by navArgs()
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
    ): View = FragmentProfileNameDialogBinding
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
        args.firstName?.let { profileNameEditText.setText(it) }
        args.lastName?.let { profileLastnameEditText.setText(it) }
        args.phone?.let { phone = it }

        profileNameBackButton.setOnClickListener {
            hideKeyboard()
            isSaveSuccess = false
            findNavController().navigateUp()
        }

        profileNameSaveButton.setOnClickListener { saveData() }

        profileNameEditText.requestFocus()
        showKeyboard()
    }

    private fun saveData() = with(binding) {
        if (sharedPreferences.userId != UNREGISTERED) {
            sharedPreferences.userId?.let {
                val firstName = profileNameEditText.text.toString()
                val lastName = profileLastnameEditText.text.toString()
                val user = UserDataModel(firstName = firstName, lastName = lastName, phone = phone)
                viewModel.updateUser(user, it)
            }
        }
    }

    private fun showKeyboard() {
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        keyboardShown = true
    }

    private fun hideKeyboard() = if (keyboardShown) {
        inputMethodManager?.hideSoftInputFromWindow(binding.profileNameRootView.windowToken, 0)
        keyboardShown = false
    } else Unit

    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                Toast.makeText(context, "Сохранение прошло успешно", Toast.LENGTH_SHORT).show()
                hideKeyboard()
                isSaveSuccess = true
                findNavController().navigateUp()
            }
            is AppState.Error -> {
                Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setNavigationResult(KEY_NAME, isSaveSuccess)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        inputMethodManager = null
    }

    companion object {
        const val KEY_NAME = "Name"
    }
}