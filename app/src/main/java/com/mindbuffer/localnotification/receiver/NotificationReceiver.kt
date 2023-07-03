package com.mindbuffer.localnotification.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.mindbuffer.localnotification.MainActivity
import com.mindbuffer.localnotification.R
import com.mindbuffer.localnotification.utils.AppConstants.NOTIFICATION_CHANNEL_ID
import com.mindbuffer.localnotification.utils.AppConstants.NOTIFICATION_CHANNEL_NAME
import com.mindbuffer.localnotification.utils.AppConstants.NOTIFICATION_ID
import com.mindbuffer.localnotification.utils.AppConstants.messageExtra
import com.mindbuffer.localnotification.utils.AppConstants.titleExtra
import timber.log.Timber


class NotificationReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent) {

        Timber.d("onReceive its calling")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val resultIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

}