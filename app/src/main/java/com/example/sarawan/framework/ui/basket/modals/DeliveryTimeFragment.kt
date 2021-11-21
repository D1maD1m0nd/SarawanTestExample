package com.example.sarawan.framework.ui.basket.modals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.sarawan.R
import com.example.sarawan.databinding.FragmentDeliveryTimeBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class DeliveryTimeFragment : DialogFragment() {
    private var _binding: FragmentDeliveryTimeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryTimeBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
        initView()
    }

    private fun initView() = with(binding){
        backButton.setOnClickListener {
            dismiss()
        }

        timeImageButton.setOnClickListener{
            showTimePicker()
        }

        dateImageButton.setOnClickListener {
            showDatePicker()
        }
    }
    private fun showDatePicker() {
        val picker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date_delivery))
                .build()

        // set listener when date is selected
        picker.addOnPositiveButtonClickListener {

            // Create calendar object and set the date to be that returned from selection
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.time = Date(it)
            val date = "${calendar.get(Calendar.DAY_OF_MONTH)}- " +
                    "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
            setDate(date)
        }
        picker.show(parentFragmentManager, "DATE PICKER")
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(INPUT_MODE_KEYBOARD)
                .setTitleText(getString(R.string.select_time_delivery))
                .build()
        picker.addOnDismissListener {
            val hour = picker.hour
            val time = picker.minute
            setTime(hour, time)
        }
        picker.show(parentFragmentManager, "TIME_PICKER")
    }
    private fun setDate(date : String) {
        binding.dateEditText.setText(date, TextView.BufferType.EDITABLE)
    }
    private fun setTime(hour : Int, minute : Int) {
        val time = "$hour:$minute"
        binding.timeEditText.setText(time, TextView.BufferType.EDITABLE)
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = DeliveryTimeFragment()
    }
}