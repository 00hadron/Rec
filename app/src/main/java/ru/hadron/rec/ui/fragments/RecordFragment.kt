package ru.hadron.rec.ui.fragments

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ru.hadron.rec.R
import ru.hadron.rec.others.Constants.REQUEST_CODE_WR_STORAGE_RECORD_AUDIO_PERMISSIONS
import ru.hadron.rec.others.RecordingUtility

class RecordFragment : Fragment(R.layout.fragment_record), EasyPermissions.PermissionCallbacks {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.requestPermissions()
    }

    fun requestPermissions() {
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