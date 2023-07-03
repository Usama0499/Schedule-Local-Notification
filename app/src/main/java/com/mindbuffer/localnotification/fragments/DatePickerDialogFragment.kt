package com.mindbuffer.localnotification.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindbuffer.localnotification.R
import com.mindbuffer.localnotification.databinding.FragmentDatePickerDialogBinding
import com.mindbuffer.localnotification.utils.AppConstants.DatePickerDialogExtra
import com.mindbuffer.localnotification.utils.AppConstants.SDF_Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DatePickerDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding<FragmentDatePickerDialogBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_picker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMinDateAsCurrentDate()

        binding.doneBtn.setOnClickListener {
            getDate()
        }
    }

    private fun setMinDateAsCurrentDate() {
        val c = Calendar.getInstance()
        binding.datePicker.minDate = c.timeInMillis
    }

    private fun getDate(){
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val dateFormat = SimpleDateFormat(SDF_Date, Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)

        val bundle = bundleOf(
            "formattedDate" to formattedDate
        )
        setFragmentResult(DatePickerDialogExtra, bundle)
        dismiss()
    }

}