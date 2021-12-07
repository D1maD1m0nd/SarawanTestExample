package com.example.sarawan.framework.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sarawan.R
import com.example.sarawan.app.App.Companion.navController
import com.example.sarawan.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentInfoBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
        infoHowWorkingLayout.setOnClickListener { showHowWorking() }
        infoSupportLayout.setOnClickListener { showSupport() }
        infoAboutLayout.setOnClickListener { showAbout() }
        //несмотря на то, что обработчик на весь лейаут, нажатие на кнопки не работает
        //поэтому вешаю и на них обработчики те же
        infoHowWorkingButton.setOnClickListener { showHowWorking() }
        infoSupportButton.setOnClickListener { showSupport() }
        infoAboutButton.setOnClickListener { showAbout() }
    }

    private fun showHowWorking() {
        navController.navigate(R.id.action_infoFragment_to_infoHowFragment)
    }

    private fun showSupport() {
        navController.navigate(R.id.action_infoFragment_to_infoSupportFragment)
    }

    private fun showAbout() {
        navController.navigate(R.id.action_infoFragment_to_infoAboutFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = InfoFragment()
    }
}