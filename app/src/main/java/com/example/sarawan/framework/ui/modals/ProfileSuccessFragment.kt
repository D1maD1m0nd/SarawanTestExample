package com.example.sarawan.framework.ui.modals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.sarawan.databinding.FragmentProfileSuccessBinding

class ProfileSuccessFragment : DialogFragment() {

    private var _binding: FragmentProfileSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileSuccessBinding
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
        profileSuccessOkButton.setOnClickListener { closeAllDialogs() }
    }

    private fun closeAllDialogs() {
        val manager = requireActivity().supportFragmentManager
        for (fragment in manager.fragments) {
            if (fragment is DialogFragment) {
                fragment.dismiss()
            }
        }
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfileSuccessFragment()
    }
}