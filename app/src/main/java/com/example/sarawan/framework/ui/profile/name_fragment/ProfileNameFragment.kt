package com.example.sarawan.framework.ui.profile.name_fragment

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
import com.example.sarawan.databinding.FragmentProfileNameBinding
import com.example.sarawan.framework.ui.profile.name_fragment.viewModel.NameViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.UserDataModel
import com.example.sarawan.utils.exstentions.userId
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ProfileNameFragment : DialogFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: NameViewModel by lazy {
        viewModelFactory.create(NameViewModel::class.java)
    }

    private var _binding: FragmentProfileNameBinding? = null
    private val binding get() = _binding!!

    private var userFirstName: String? = null
    private var userLastName: String? = null
    private lateinit var onSaveDataCallback: () -> Unit

    private var inputMethodManager: InputMethodManager? = null
    private var keyboardShown = false

    private fun showKeyboard() {
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        keyboardShown = true
    }

    private fun hideKeyboard() {
        if (keyboardShown) {
            inputMethodManager?.hideSoftInputFromWindow(binding.profileNameRootView.windowToken, 0)
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
    ): View = FragmentProfileNameBinding
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
        userFirstName?.let { profileNameEditText.setText(it) }
        userLastName?.let { profileLastnameEditText.setText(it) }

        profileNameBackButton.setOnClickListener {
            hideKeyboard()
            dismiss()
        }
        profileNameSaveButton.setOnClickListener { saveData() }

        profileNameEditText.requestFocus()
        showKeyboard()
    }

    private fun saveData() = with(binding) {
        if (sharedPreferences.userId != 1L) {
            sharedPreferences.userId?.let {
                val firstName = profileNameEditText.text.toString()
                val lastName = profileLastnameEditText.text.toString()
                val user = UserDataModel(firstName = firstName, lastName = lastName)
                viewModel.updateUser(user, it)
            }
        }
    }

    private fun setState(appState: AppState<*>) {
        when (appState) {
            is AppState.Success<*> -> {
                Toast.makeText(context, "Сохранение прошло успешно", Toast.LENGTH_SHORT).show()

                hideKeyboard()
                onSaveDataCallback.invoke()
                dismiss()
            }
            is AppState.Error -> {
                Toast.makeText(
                    context,
                    "Ошибка",
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
        fun newInstance(user: UserDataModel?, onSaveDataCallback: () -> Unit) =
            ProfileNameFragment().apply {
                this.onSaveDataCallback = onSaveDataCallback
                user?.let {
                    userFirstName = it.firstName
                    userLastName = it.lastName
                }
            }
    }
}