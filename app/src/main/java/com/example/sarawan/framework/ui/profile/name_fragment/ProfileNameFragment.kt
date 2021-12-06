package com.example.sarawan.framework.ui.profile.name_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.sarawan.databinding.FragmentProfileNameBinding

class ProfileNameFragment : DialogFragment() {

    private var _binding: FragmentProfileNameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileNameBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
        initViews()
    }

    private fun initViews() = with(binding) {
        profileNameBackButton.setOnClickListener { dismiss() }
        profileNameSaveButton.setOnClickListener { saveData() }
    }

    private fun saveData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfileNameFragment()
    }
}