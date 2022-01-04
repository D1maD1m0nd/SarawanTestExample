package ru.sarawan.android.framework.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.sarawan.android.app.App
import ru.sarawan.android.databinding.FragmentInfoHowBinding
import ru.sarawan.android.framework.INavigation

class InfoHowFragment : Fragment(), INavigation {

    private var _binding: FragmentInfoHowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentInfoHowBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
        infoHowBackButton.setOnClickListener { onFragmentBackStack() }
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
        fun newInstance() = InfoHowFragment()
    }
}