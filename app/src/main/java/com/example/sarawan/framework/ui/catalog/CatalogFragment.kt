package com.example.sarawan.framework.ui.catalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sarawan.R
import com.example.sarawan.databinding.FragmentBasketBinding
import com.example.sarawan.databinding.FragmentCatalogBinding


class CatalogFragment : Fragment() {
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = CatalogFragment()
    }
}