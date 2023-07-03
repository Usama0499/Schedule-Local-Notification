package com.mindbuffer.localnotification.utils

import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.mindbuffer.localnotification.R
import java.util.Calendar

object FieldHelper {

    fun validation(textView: TextView): Boolean {
        if (textView.text.toString().trim().isEmpty()) {
            textView.error = textView.context.getString(R.string.err_empty)
            return false
        }
        textView.error = null
        return true
    }

    fun validation(editText: TextInputLayout): Boolean {
        if (editText.editText!!.text.toString().trim().isEmpty()) {
            editText.error = editText.context.getString(R.string.err_empty)
            return false
        }
        editText.error = null
        return true
    }

    fun selectedTimeGreaterThanCurrentTime(selectedDateTime: Long): Boolean {
        val currentTimeInMillis = Calendar.getInstance().timeInMillis
        return selectedDateTime > currentTimeInMillis
    }

}