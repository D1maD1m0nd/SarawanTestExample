package ru.sarawan.android.framework.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.sarawan.android.databinding.FragmentInfoBinding

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
    }

    private fun showHowWorking() {
        val action = InfoFragmentDirections.actionInfoFragmentToInfoHowFragment()
        findNavController().navigate(action)
    }

    private fun showSupport() {
        val action = InfoFragmentDirections.actionInfoFragmentToInfoSupportFragment()
        findNavController().navigate(action)
    }

    private fun showAbout() {
        val action = InfoFragmentDirections.actionInfoFragmentToInfoAboutFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}