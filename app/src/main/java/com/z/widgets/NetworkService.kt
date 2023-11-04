package com.z.widgets

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.telephony.CellInfoLte
import android.telephony.PhoneStateListener
import android.telephony.ServiceState
import android.telephony.TelephonyManager
import android.util.Log
import android.view.MotionEvent.ACTION_CANCEL
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.security.Provider.Service

class NetworkService : android.app.Service() {

    private lateinit var telephonyManager : TelephonyManager
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        telephonyManager  = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        val phoneStateListener = object : PhoneStateListener() {
            override fun onDataConnectionStateChanged(state: Int, networkType: Int) {
                // Handle network changes here
                val networkTypeString = getNetworkType(networkType)
                showNetworkChangeNotification(networkTypeString)
            }
        }

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE)
    }

    override fun onBind(p0: Intent?): IBinder? = null

    private fun getNetworkType(networkType: Int): String {
        when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN,
            TelephonyManager.NETWORK_TYPE_GSM
            -> return "2G"
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_TD_SCDMA
            -> return "3G"
            TelephonyManager.NETWORK_TYPE_LTE
            -> return "4G"
            TelephonyManager.NETWORK_TYPE_NR
            -> return "5G"
            else -> return "Unknown"
        }
    }

    private fun showNetworkChangeNotification(networkType: String) {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("network_change_channel", "Network Change Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_IMMUTABLE)



        val notification = NotificationCompat.Builder(this, "network_change_channel")
            .setContentTitle("Network Changed")
            .setContentText("Network Type: $networkType")
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.baseline_circle_notifications_24,"Reset",getCancelPendingIntent())
            .setAutoCancel(true)
            .build()


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForeground(1,notification)
        } else {
            notificationManager.notify(1, notification)
        }

        Log.d("TAG","Current Rsrp ${getRsrpValues()}")
        if(getRsrpValues() >= -100 && getRsrpValues() <= -120){
            Log.d("TAG","RSRP " + getRsrpValues())
            Handler().postDelayed({ notificationManager.cancel(1) }, 15000)
        }

    }


    private fun getCancelPendingIntent() : PendingIntent {
        val intent = Intent(this,NetworkChangeReceiver::class.java)
        intent.action = "com.z.widgets.networkChangeReceiver.actionCancelNotification"
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_IMMUTABLE)
    }
    @SuppressLint("MissingPermission", "NewApi")
    private fun getRsrpValues() : Int {
        var rsrp = 0
        val cellInfoList = telephonyManager.allCellInfo
        for (cellInfo in cellInfoList){
            if(cellInfo is CellInfoLte){
                rsrp = cellInfo.cellSignalStrength.rsrp
            }
        }
        return rsrp
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(1)
        stopSelf()
    }

}