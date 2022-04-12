package ru.sarawan.android.framework.ui.modals

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.sarawan.android.MobileNavigationDirections
import ru.sarawan.android.activity.contracts.BasketSaver
import ru.sarawan.android.databinding.FragmentProfileSuccessDialogBinding

class ProfileSuccessDialogFragment : DialogFragment() {

    private var _binding: FragmentProfileSuccessDialogBinding? = null
    private val binding get() = _binding!!

    private var basketSaver: BasketSaver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileSuccessDialogBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
        }
        isCancelable = false
        initViews()
    }

    private fun initViews() = with(binding) {
        profileSuccessOkButton.setOnClickListener { closeAllDialogs() }
    }

    private fun closeAllDialogs() {
        basketSaver?.saveBasket()
        val action = MobileNavigationDirections.actionGlobalToProfileFragment()
        findNavController().navigate(action)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        basketSaver = context as BasketSaver
    }

    override fun onDetach() {
        super.onDetach()
        basketSaver = null
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}