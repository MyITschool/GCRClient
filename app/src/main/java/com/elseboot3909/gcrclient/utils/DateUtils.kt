package com.elseboot3909.gcrclient.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {
        val dateInputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val dateOutputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US)
        val clockOutputFormat = SimpleDateFormat("HH:mm", Locale.US)
        val monthOutputFormat = SimpleDateFormat("dd-MM", Locale.US)
        val yearOutputFormat = SimpleDateFormat("yyyy", Locale.US)
        val currentData: Date = Calendar.getInstance().time
    }

}