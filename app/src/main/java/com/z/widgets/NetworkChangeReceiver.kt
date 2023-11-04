package com.z.widgets

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager

class NetworkChangeReceiver : BroadcastReceiver() {
    private lateinit var telephonyManager : TelephonyManager
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {

        val serviceIntent = Intent(context,NetworkService::class.java)
        if(intent?.action == "com.z.widgets.networkChangeReceiver.actionCancelNotification"){
            context?.stopService(serviceIntent)
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context?.startForegroundService(serviceIntent)
            } else {
                context?.startActivity(serviceIntent)
            }
        }

    }
}