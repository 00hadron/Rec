package ru.hadron.rec.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.widget.Toast
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
import java.io.File
import java.io.IOException
import java.lang.IllegalStateException

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



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        intent?.let {
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.e("--------------------------------------------started service")
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.e("-------------------------------------resuming service...")
                        this.startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.e("------------------------------------------------pause service")
                    // isTimerEnable = false
                    this.pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.e ("----------------------------------------------stop service")
                    try {
                        stopRecording()
                    } catch (e: IOException) {
                        Timber.e("------------------------error! not stop recording")
                    }

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

    @RequiresApi(Build.VERSION_CODES.N)
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


    @RequiresApi(Build.VERSION_CODES.N)
    private fun startTimer() {

        isRecording.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnable = true

        if (isMediaRecorderStarted) {
            resumeRecording()
        }

        if (!isMediaRecorderStarted) {
            startRecording()
        }

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
//-----------------------------------

            pauseRecording()

//-----------------------------------
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

    //--------------------------------------------------------------------------

    private var recorder: MediaRecorder? = null
    private var out: String? = null
    private val dir: File = File(Environment.getExternalStorageDirectory().toString() + "/soundrecorder/")

    private var isMediaRecorderStarted = false //работает или нет
    private var isMediaRecorderStoppedOrPaused = false

    init {
        try {
            // create a File object for the parent directory
            val recorderDirectory = File(Environment.getExternalStorageDirectory().toString() + "/soundrecorder/")
            // have the object build the directory structure, if needed.
            recorderDirectory.mkdirs()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (dir.exists()) {
            val count = dir.listFiles().size
            out = Environment.getExternalStorageDirectory().absolutePath + "/soundrecorder/recording"+count+".mp3"
        }

        recorder = MediaRecorder()
        makeToastFromService("$out")

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(out)
        }
    }
    private fun initRecorder() {

        recorder = MediaRecorder()

        if(dir.exists()){
            val count = dir.listFiles().size
            out = Environment.getExternalStorageDirectory().absolutePath + "/soundrecorder/recording"+count+".mp3"
        }

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(out)
        }
    }

    private fun startRecording() {

        Timber.e("--------------------------------------------------starting recording...")
        try {
            recorder?.apply {
                prepare()
                start()
                isMediaRecorderStarted = true
                makeToastFromService(toastMessage = "start recording...")
            }
        } catch (ise: IllegalStateException) {
            Timber.e("${ise.printStackTrace()}--------------------------------startRecording ERROR - illegal state!")
            ise.printStackTrace()
        } catch (ioe: IOException) {
            Timber.e("${ioe.printStackTrace()}--------------------------------startRecording ERROR - ioe!")
            ioe.printStackTrace()
        }
    }

    private fun stopRecording() {
        /**
         * Здесь мы проверяем, работает ли в данный момент MediaRecorder, прежде чем мы остановим запись
         */
        if (isMediaRecorderStarted) {
            Timber.e("--------------------------------------------------stopping recording...")
            recorder?.apply {
                stop()
                reset()
                release() // всунуть где остановка и ресет таймкера
            }
            Timber.e("--------------------------------------------------stopped")
            makeToastFromService(toastMessage = "Stopped recording!")
            isMediaRecorderStarted = false
            recorder = null

            if (File(out).exists()) {
                makeToastFromService(" файл $out существует")
            }

            this.initRecorder()

        } else {
            makeToastFromService(toastMessage = "You are not recording right now!")
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        if (isMediaRecorderStarted) {
            if (!isMediaRecorderStoppedOrPaused) {
                makeToastFromService("pause recording")
                recorder?.apply {
                    pause()
                }
                isMediaRecorderStoppedOrPaused = true
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {
        makeToastFromService("resume!")
        recorder?.resume()
        isMediaRecorderStoppedOrPaused = false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onRecord() {
        if (isRecording.value == true) {
            startRecording()
        } else {
            pauseRecording()
        }
    }

    private fun makeToastFromService(toastMessage: String) = CoroutineScope(Dispatchers.Main)
        .launch {
        Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_SHORT).show()
    }
}