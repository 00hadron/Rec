/*
package ru.hadron.rec.others

import android.icu.util.TimeUnit
import android.media.MediaRecorder
import android.os.Environment
import ru.hadron.rec.services.RecordingService.Companion.file
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AppRecorder {
    private lateinit var recorder: MediaRecorder
    private var recordFileName = "default.3gp"
    //fileName = "${externalCacheDir.absolutePath}/audiorecordtest.3gp"
   // private val _file = "{${file}${fileName}"
    private lateinit var mOutputFile: File

*/
/*    fun init() {
        mOutputFile = file
        Timber.e("-------${_file}  - place for saving--------------")
    }*//*


*/
/*    fun setFileName(fileName: String) {
        this.fileName = fileName
    }*//*


*/
/*    fun onRecord() = if (isRecording.value == true) {
        Timber.e("------------onRecord ----------------------- true")
        startRecord()
    } else {
        Timber.e("------------onRecord ----------------------- false")
        stopRecord()
    }*//*



    fun startRecord() {
        this.prepareMediaPlayer()
        try {
            this.recorder?.start()
            Timber.e("------------startRecord -----------------------success")
        } catch (e: RuntimeException) {
            Timber.e("RuntimeException: start() ")
        }

    }

    fun stopRecord() {
        try {
            this.recorder?.stop()
            Timber.e("------------stopRecord -----------------------success")
        } catch (e: RuntimeException) {
            Timber.e("RuntimeException: stop() is called immediately after start() ")
            this.mOutputFile.delete()
        }
        this.releaseRecorder()
    }

    private fun releaseRecorder() {
        this.recorder?.release()
        this.recorder.reset()
    }
*/
/*    private fun pauseRecord() {
        this.recorder.pause()
    }*//*

private
    private fun prepareMediaPlayer() { //pre start recording
      //  val recordPath = Ser

        recorder = MediaRecorder()


        this.recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)   //mp3 в итоге
            setOutputFile(_file)
            prepare()
            */
/*  try {
                  prepare()
              } catch (e: IOException) {
                  Timber.e("----prepare() failed!----")
              }*//*

        }
    }

}*/
