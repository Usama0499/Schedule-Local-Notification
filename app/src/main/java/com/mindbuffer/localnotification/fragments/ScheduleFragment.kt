package com.mindbuffer.localnotification.fragments

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mindbuffer.localnotification.R
import com.mindbuffer.localnotification.databinding.FragmentScheduleBinding
import com.mindbuffer.localnotification.receiver.NotificationReceiver
import com.mindbuffer.localnotification.utils.AppConstants.DatePickerDialogExtra
import com.mindbuffer.localnotification.utils.AppConstants.NOTIFICATION_ID
import com.mindbuffer.localnotification.utils.AppConstants.SDF
import com.mindbuffer.localnotification.utils.AppConstants.TimePickerDialogExtra
import com.mindbuffer.localnotification.utils.AppConstants.messageExtra
import com.mindbuffer.localnotification.utils.AppConstants.titleExtra
import com.mindbuffer.localnotification.utils.DialogUtils.showNotificationPermissionDialog
import com.mindbuffer.localnotification.utils.DialogUtils.showScheduleAlert
import com.mindbuffer.localnotification.utils.FieldHelper.selectedTimeGreaterThanCurrentTime
import com.mindbuffer.localnotification.utils.FieldHelper.validation
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ScheduleFragment : Fragment() {

    private val binding by viewBinding<FragmentScheduleBinding>()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        navController = findNavController()
        binding.apply {
            pickDateBtn.setOnClickListener {
                val directions = ScheduleFragmentDirections.actionScheduleFragmentToDatePickerDialogFragment()
                navController.navigate(directions)
            }
            pickTimeBtn.setOnClickListener {
                val directions = ScheduleFragmentDirections.actionScheduleFragmentToTimePickerDialogFragment()
                navController.navigate(directions)
            }
            submitButton.setOnClickListener {
                checkNotificationPermission()
            }
        }

        // listeners for dialog Fragments
        dialogFragmentsListeners()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.POST_NOTIFICATIONS)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        checkValidations()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        showNotificationPermissionDialog(requireContext())
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?,
                    ) {
                        showNotificationPermissionDialog(requireContext())
                    }
                }).check()
        } else{
            checkValidations()
        }
    }

    private fun dialogFragmentsListeners() {

        // date dialog listener
        setFragmentResultListener(DatePickerDialogExtra) { _, result ->
            val selectedDate = result.getString("formattedDate").toString()
            Timber.d("DatePickerDialogExtra $selectedDate")
            binding.dateTxt.text = selectedDate
        }

        // date dialog listener
        setFragmentResultListener(TimePickerDialogExtra) { _, result ->
            val selectedTime = result.getString("formattedTime")
            Timber.d("formattedTime $selectedTime")
            binding.timeTxt.text = selectedTime
        }

    }

    private fun checkValidations() {
        binding.apply {
            if (!validation(titleET)) return
            if (!validation(messageET)) return
            if (!validation(dateTxt)) {
                Toast.makeText(requireContext(), R.string.err_empty,  Toast.LENGTH_SHORT).show()
                return
            }
            if (!validation(timeTxt)) {
                Toast.makeText(requireContext(), R.string.err_empty,  Toast.LENGTH_SHORT).show()
                return
            }
            if(!selectedTimeGreaterThanCurrentTime(getTime())) {
                Toast.makeText(requireContext(), R.string.error_date_time,  Toast.LENGTH_LONG).show()
                return
            }

            scheduleNotification()
        }
    }

    private fun scheduleNotification() {

        val title = binding.titleET.editText?.text.toString().trim()
        val message = binding.messageET.editText?.text.toString().trim()

        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showScheduleAlert(requireContext(), time, title, message)
    }

    private fun getTime(): Long {
        val formattedDate = binding.dateTxt.text.toString()
        val formattedTime = binding.timeTxt.text.toString()

        val dateTimeFormat = SimpleDateFormat(SDF, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateTimeFormat.parse("$formattedDate $formattedTime") as Date
        return calendar.timeInMillis
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFragmentResultListener(DatePickerDialogExtra)
        clearFragmentResultListener(TimePickerDialogExtra)
    }

}