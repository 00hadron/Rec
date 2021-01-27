package ru.hadron.rec.ui.fragments

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_record.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ru.hadron.rec.R
import ru.hadron.rec.others.Constants.ACTION_PAUSE_SERVICE
import ru.hadron.rec.others.Constants.ACTION_START_OR_RESUME_SERVICE
import ru.hadron.rec.others.Constants.ACTION_STOP_SERVICE
import ru.hadron.rec.others.Constants.REQUEST_CODE_WR_STORAGE_RECORD_AUDIO_PERMISSIONS
import ru.hadron.rec.others.RecordingUtility
import ru.hadron.rec.services.RecordingService

class RecordFragment : Fragment(R.layout.fragment_record), EasyPermissions.PermissionCallbacks {

    private var curTimeInMillis = 0L
    private lateinit var timer: TextView

    private var isRecording = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.requestPermissions()

        //set font on timer
         timer = view.findViewById<TextView>(R.id.tvTimer)
        val typeface = activity?.baseContext?.let { ResourcesCompat.getFont(it, R.font.element) }
        timer.typeface = typeface

        //test service
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.setOnClickListener {
            //this.sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            btnStopTest.isVisible = true
            this.toggleFabRecord()
        }

        val buttonTest = view.findViewById<Button>(R.id.btnStopTest)
        buttonTest.setOnClickListener {

            this.stopRecord()
        }

        this.subscribeToObservers()
    }

    private fun requestPermissions() {
        if (RecordingUtility.hasWriteReadExternalStorageAndRecordAudioPermissions(requireContext())) { return }

        EasyPermissions.requestPermissions(
            this,
            "You need to accept this permissions",
            REQUEST_CODE_WR_STORAGE_RECORD_AUDIO_PERMISSIONS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {/*NO-OP*/}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
        else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this)
    }

    private fun sendCommandToService(action: String) = Intent(requireContext(), RecordingService::class.java)
        .also {
            it.action = action
            requireContext().startService(it)
        }

    private fun subscribeToObservers() {
        RecordingService.timeRecordInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            val formattedTime = RecordingUtility.getFormattedStopWatchTime(
                curTimeInMillis,
                includeMillis = false,
                includeHours = false)
            timer.text = formattedTime
        })

        RecordingService.isRecording.observe(viewLifecycleOwner, Observer {
            updateRecording(it)
        })
    }

    private fun stopRecord() {
        tvTimer.text = "00:00"
        btnStopTest.isVisible = false
        this.sendCommandToService(ACTION_STOP_SERVICE)
    }

    private fun toggleFabRecord() {
        if (isRecording) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateRecording(isRecording: Boolean) {
        this.isRecording = isRecording
        if (!isRecording && curTimeInMillis > 0) {
            tvComment.text = "pause"
        } else if (isRecording) {
            tvComment.text = "recording"
        }
    }
}