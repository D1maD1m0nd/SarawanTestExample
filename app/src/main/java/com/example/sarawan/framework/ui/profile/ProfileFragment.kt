package com.example.sarawan.framework.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sarawan.R
import com.example.sarawan.app.App
import com.example.sarawan.databinding.FragmentProfileBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
        profileNameLayout.setOnClickListener { showName() }
        profilePhoneLayout.setOnClickListener { showPhone() }
        profileAddressLayout.setOnClickListener { showAddress() }

        profileNameButton.setOnClickListener { showName() }
        profilePhoneButton.setOnClickListener { showPhone() }
        profileAddressButton.setOnClickListener { showAddress() }

        profileExitButton.setOnClickListener { leave() }
    }

    private fun leave() {
    }

    private fun showAddress() {
    }

    private fun showPhone() {
    }

    private fun showName() {
        App.navController.navigate(R.id.profileNameFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}