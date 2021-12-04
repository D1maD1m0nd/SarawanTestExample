package com.example.sarawan.framework.ui.modals

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
    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setInputMode(INPUT_MODE_KEYBOARD)
            .setTitleText(getString(R.string.select_time_delivery))
            .build()
        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour.toString()
            val minute = picker.minute.toString()
            val time = formatTime(hour, minute)
            setTime(time)
        }
        picker.show(parentFragmentManager, "TIME_PICKER")
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
            val day = "${calendar.get(Calendar.DAY_OF_MONTH)}"
            val month = "${calendar.get(Calendar.MONTH) + 1}"
            val year =  "${calendar.get(Calendar.YEAR)}"
            val date =  formatDate(day, month, year)
            setDate(date)
        }
        picker.show(parentFragmentManager, "DATE PICKER")
    }
    private fun setTime(time : String) {
        binding.timeEditText.setText(time, TextView.BufferType.EDITABLE)
    }
    private fun formatTime(hour: String, minute: String) : String{
        val formatHour = if (hour.length > 1) hour else "0${hour}"
        val formatMinute = if (minute.length > 1) minute else "0${minute}"
        return "$formatHour $formatMinute"
    }
    private fun setDate(date : String) {
        binding.dateEditText.setText(date, TextView.BufferType.EDITABLE)
    }

    private fun formatDate(day : String, month: String, year : String ) : String{
        val formatDay = if (day.length > 1) day else "0$day"
        val formatMonth = if (month.length > 1) month else "0$month"
        return "$formatDay $formatMonth $year"
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = DeliveryTimeFragment()
    }
}