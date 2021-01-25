package ru.hadron.rec.ui.fragments

import android.Manifest
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_record.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ru.hadron.rec.R
import ru.hadron.rec.others.Constants.REQUEST_CODE_WR_STORAGE_RECORD_AUDIO_PERMISSIONS
import ru.hadron.rec.others.RecordingUtility

class RecordFragment : Fragment(R.layout.fragment_record), EasyPermissions.PermissionCallbacks {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.requestPermissions()

        //set font on timer
        var timer = view.findViewById<TextView>(R.id.tvTimer)
        val typeface = activity?.baseContext?.let { ResourcesCompat.getFont(it, R.font.element) }
        timer.typeface = typeface
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
}