package com.example.sarawan.framework.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.sarawan.R
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
        //это для тестирования, потом уберем
        initTempToggleButton()
    }

    private fun initTempToggleButton() = with(binding) {
        infoToggleButton.setOnClickListener {
            if (infoRegisteredLayout.isVisible) {
                infoRegisteredLayout.visibility = View.GONE
                infoSignUpLayout.visibility = View.VISIBLE
            } else {
                infoRegisteredLayout.visibility = View.VISIBLE
                infoSignUpLayout.visibility = View.GONE
            }
        }
    }

    private fun initViews() = with(binding) {
        infoHowWorkingLayout.setOnClickListener { showHowWorking() }
        infoReviewsLayout.setOnClickListener { showReviews() }
        infoSupportLayout.setOnClickListener { showSupport() }
        infoAboutLayout.setOnClickListener { showAbout() }
        //несмотря на то, что обработчик на весь лейаут, нажатие на кнопки не работает
        //поэтому вешаю и на них обработчики те же
        infoHowWorkingButton.setOnClickListener { showHowWorking() }
        infoReviewsButton.setOnClickListener { showReviews() }
        infoSupportButton.setOnClickListener { showSupport() }
        infoAboutButton.setOnClickListener { showAbout() }
    }

    private fun showHowWorking() {
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .replace(R.id.nav_fragment, InfoHowFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showReviews() {
        Toast.makeText(context, "reviews", Toast.LENGTH_SHORT).show()
    }

    private fun showSupport() {
        Toast.makeText(context, "support", Toast.LENGTH_SHORT).show()
    }

    private fun showAbout() {
        Toast.makeText(context, "about", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = InfoFragment()
    }
}