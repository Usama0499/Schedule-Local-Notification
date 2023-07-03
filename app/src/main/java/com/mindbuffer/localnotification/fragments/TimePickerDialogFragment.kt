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
import com.mindbuffer.localnotification.databinding.FragmentTimePickerDialogBinding
import com.mindbuffer.localnotification.utils.AppConstants
import com.mindbuffer.localnotification.utils.AppConstants.SDF_Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TimePickerDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding<FragmentTimePickerDialogBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_picker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.doneBtn.setOnClickListener {
            getTime()
        }
    }

    private fun getTime(){
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val timeFormat = SimpleDateFormat(SDF_Time, Locale.getDefault())
        val formattedTime = timeFormat.format(calendar.time)

        val bundle = bundleOf(
            "formattedTime" to formattedTime
        )
        setFragmentResult(AppConstants.TimePickerDialogExtra, bundle)
        dismiss()
    }

}