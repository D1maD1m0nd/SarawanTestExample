package com.example.sarawan.framework.ui.catalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sarawan.R
import com.example.sarawan.app.App.Companion.navController
import com.example.sarawan.databinding.FragmentBasketBinding
import com.example.sarawan.databinding.FragmentCatalogBinding
import com.example.sarawan.framework.INavigation


class CatalogFragment : Fragment(), INavigation {
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    private fun initView() = with(binding) {
        nextButton.setOnClickListener {
            onFragmentNext()
        }

        backButton.setOnClickListener {
            onFragmentBackStack()
        }
    }
    override fun onFragmentBackStack() {
        navController.popBackStack()
    }
    override fun onFragmentNext() {
        //action указывается в файле навигации ввиде стрелки(потяни точку у экрана и справа появится меню,
        // с помощью него можно передавать данные между фрагментами
        navController.navigate(R.id.action_catalogFragment_to_profileFragment)
        //navController.navigate(R.id.profileFragment)
    }
    companion object {
        fun newInstance() = CatalogFragment()
    }
}