package com.example.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.provider.Settings.System.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.core.content.getSystemService
import java.text.ParseException
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object{
        const val TYPE_ONE_TIME = "OneTimeAlarm"
        const val TYPE_REPEATING = "RepeatingAlarm"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_TYPE = "type"

        private const val ID_ONTIME = 100
        private const val ID_REPEATING = 101
    }
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE)
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val title = if (type.equals(TYPE_ONE_TIME, ignoreCase = true)) TYPE_ONE_TIME else TYPE_REPEATING
        val notif = if (type.equals(TYPE_ONE_TIME, ignoreCase = false)) ID_ONTIME else ID_REPEATING

        showToast(context, title, message)
    }

    private fun showToast(context: Context, title: String, message: String?){
      Toast.makeText(context, "$title : $message", Toast.LENGTH_LONG).show()
    }

    fun setOneTimeAlarm(context: Context, type: String, date: String, time: String, message: String){

        if (isDateInvalid(date, DATE_FORMAT) || isDateInvalid(time, TIME_12_24)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        Log.e("ONE TIME", "$date $time")
        val dateArray = date.split("-").toTypedArray()
        val timeArray = time.split(":").toTypedArray()

        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, Integer.parseInt(dateArray[0]))
        calender.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
        calender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]))
        calender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calender.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calender.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_ONTIME, intent, 0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calender.timeInMillis, pendingIntent)

        Toast.makeText(context, "One time alarm set up", Toast.LENGTH_SHORT).show()
    }

    private fun isDateInvalid(date: String, format: String): Boolean{
        return try {
            val df = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat(format, Locale.getDefault())
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                df.isLenient = false
            }
            df.parse(date)
            false
        } catch (e: ParseException){
            true
        }
    }
}
