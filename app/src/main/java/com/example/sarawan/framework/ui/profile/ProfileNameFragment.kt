package com.example.sarawan.framework.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sarawan.app.App
import com.example.sarawan.databinding.FragmentProfileNameBinding
import com.example.sarawan.framework.INavigation

class ProfileNameFragment : Fragment(), INavigation {

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
        initViews()
    }

    private fun initViews() = with(binding) {
        profileNameBackButton.setOnClickListener { onFragmentBackStack() }
        profileSaveButton.setOnClickListener { saveData() }
    }

    private fun saveData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onFragmentBackStack() {
        App.navController.popBackStack()
    }

    override fun onFragmentNext() = Unit

    companion object {
        fun newInstance() = ProfileFragment()
    }
}