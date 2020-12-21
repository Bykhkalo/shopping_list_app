package com.example.shoppinglistapp.utils

import android.annotation.SuppressLint
import java.sql.Timestamp
import java.text.SimpleDateFormat


class DateUtils {

    companion object{
        @SuppressLint("SimpleDateFormat")
        fun getStringFromTimestamp(timestamp: Timestamp, pattern: String = "yyyy-MM-dd hh:mm:ss"): String {
            val dateFormat = SimpleDateFormat(pattern)
            return dateFormat.format(timestamp)
        }


        @SuppressLint("SimpleDateFormat")
        fun getTimestampFromString(timestampInString: String, pattern: String = "yyyy-MM-dd hh:mm:ss"): Timestamp {
            val dateFormat = SimpleDateFormat(pattern)
            val parsedDate = dateFormat.parse(timestampInString)
            return Timestamp(parsedDate.time)
        }
    }

}