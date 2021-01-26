package ru.hadron.rec.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import ru.hadron.rec.others.Constants.ACTION_PAUSE_SERVICE
import ru.hadron.rec.others.Constants.ACTION_START_OR_RESUME_SERVICE
import ru.hadron.rec.others.Constants.ACTION_STOP_SERVICE
import timber.log.Timber

class RecordingService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d("started or resume service")
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

}