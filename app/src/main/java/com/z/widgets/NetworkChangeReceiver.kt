package com.z.widgets

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NetworkChangeReceiver : BroadcastReceiver() {
    private lateinit var telephonyManager : TelephonyManager
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {

        val serviceIntent = Intent(context,NetworkService::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context?.startForegroundService(serviceIntent)
        } else {
            context?.startActivity(serviceIntent)
        }

    }
}