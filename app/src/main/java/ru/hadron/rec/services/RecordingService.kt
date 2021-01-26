package ru.hadron.rec.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import ru.hadron.rec.R
import ru.hadron.rec.others.Constants.ACTION_PAUSE_SERVICE
import ru.hadron.rec.others.Constants.ACTION_SHOW_RECORD_FRAGMENT
import ru.hadron.rec.others.Constants.ACTION_START_OR_RESUME_SERVICE
import ru.hadron.rec.others.Constants.ACTION_STOP_SERVICE
import ru.hadron.rec.others.Constants.NOTIFICATION_CHANNEL_ID
import ru.hadron.rec.others.Constants.NOTIFICATION_CHANNEL_NAME
import ru.hadron.rec.others.Constants.NOTIFICATION_ID
import ru.hadron.rec.ui.MainActivity
import timber.log.Timber

class RecordingService : LifecycleService() {
    var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d("started service")
                    if (isFirstRun) {
                       startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("resuming service...")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("pause service")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d ("stop service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
 //--DI-- baseNotificationBuilder
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_mic)
            .setContentTitle("record")
            .setContentText("00:00:00")
            .setContentIntent(this.getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())

    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_RECORD_FRAGMENT },
        FLAG_UPDATE_CURRENT
    )
}