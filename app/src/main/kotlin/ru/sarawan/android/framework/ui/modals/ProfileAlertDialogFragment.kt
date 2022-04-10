package ru.sarawan.android.framework.ui.modals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.sarawan.android.databinding.FragmentProfileDialogAlertBinding

class ProfileAlertDialogFragment : DialogFragment() {

    private var _binding: FragmentProfileDialogAlertBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileDialogAlertBinding
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
        profileAlertOkButton.setOnClickListener { findNavController().navigateUp() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}