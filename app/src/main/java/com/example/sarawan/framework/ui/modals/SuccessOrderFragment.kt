package com.example.sarawan.framework.ui.modals

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.sarawan.R
import com.example.sarawan.databinding.FragmentProfileSuccessBinding
import com.example.sarawan.databinding.FragmentSuccessOrderBinding


class SuccessOrderFragment : DialogFragment() {
    private var _binding: FragmentSuccessOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuccessOrderBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
        }
        binding.confrimButton.setOnClickListener {
            dismiss()
        }
    }
    companion object {
        private const val ARG_MESSAGE = "MESSAGE"
        fun newInstance(message : String) = SuccessOrderFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_MESSAGE, message)
            }
        }
    }
}