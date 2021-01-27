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
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.hadron.rec.R
import ru.hadron.rec.others.Constants.ACTION_PAUSE_SERVICE
import ru.hadron.rec.others.Constants.ACTION_SHOW_RECORD_FRAGMENT
import ru.hadron.rec.others.Constants.ACTION_START_OR_RESUME_SERVICE
import ru.hadron.rec.others.Constants.ACTION_STOP_SERVICE
import ru.hadron.rec.others.Constants.NOTIFICATION_CHANNEL_ID
import ru.hadron.rec.others.Constants.NOTIFICATION_CHANNEL_NAME
import ru.hadron.rec.others.Constants.NOTIFICATION_ID
import ru.hadron.rec.others.Constants.TIMER_UPDATE_INTERVAL
import ru.hadron.rec.ui.MainActivity
import timber.log.Timber

class RecordingService : LifecycleService() {
    var isFirstRun = true
    private val timeRecordInSeconds = MutableLiveData<Long>()
    companion object {
        val isRecording = MutableLiveData<Boolean>()

        val timeRecordInMillis = MutableLiveData<Long>()
    }


    private var isTimerEnable = false
    private var lapTime = 0L
    private var timeRecord = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    var serviceKilled = false


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
                        this.startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("pause service")
                   // isTimerEnable = false
                    this.pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d ("stop service")
                    this.killService()
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
        this.startTimer()
        isRecording.postValue(true)

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

    private fun postInitialValue() {
        isRecording.postValue(false)
        timeRecordInSeconds.postValue(0L)
        timeRecordInMillis.postValue(0L)
    }


    private fun startTimer() {
        //
        isRecording.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnable = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isRecording.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted
                timeRecordInMillis.postValue(timeRecord + lapTime)

                if (timeRecordInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRecordInSeconds.postValue(timeRecordInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                     delay(TIMER_UPDATE_INTERVAL)
            }
            timeRecord += lapTime
        }
    }

    private fun pauseService() {
        isRecording.postValue(false)
        isTimerEnable = false
    }

    override fun onCreate() {
        super.onCreate()
        this.postInitialValue()
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        this.pauseService()
        this.postInitialValue()
        stopForeground(true)
        stopSelf()
    }
}