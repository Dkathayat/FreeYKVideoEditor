package com.example.freeykvideoeditor

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.freeykvideoeditor.databinding.ActivityMainBinding
import com.example.freeykvideoeditor.ui.adapter.HomePagerAdapter
import com.example.freeykvideoeditor.ui.editor.EditorActivity
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private val REQUEST_PERMISSIONS_CODE = 102
    private val TAG = "MainActivityLogs"
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()

        binding.mainViewpager.adapter = HomePagerAdapter(supportFragmentManager, lifecycle)
        bindViewPager()

        binding.homeFloatingBtn.setOnClickListener {
            if (checkPermission()) {
                startActivity(Intent(this, EditorActivity::class.java))
            } else {
                requestPermissions()
            }
        }
    }

    private fun bindViewPager() {
        TabLayoutMediator(
            binding.tabLayout,
            binding.mainViewpager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = resources.getString(R.string.recommendation_fragment)
                1 -> tab.text = resources.getString(R.string.new_arrival_fragment)
                2 -> tab.text = resources.getString(R.string.romantic_fragment)
                3 -> tab.text = resources.getString(R.string.friends_fragment)
                4 -> tab.text = resources.getString(R.string.birthday_fragment)
                5 -> tab.text = resources.getString(R.string.anniversary_fragment)
            }
        }.attach()
    }

    private val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun checkPermission(): Boolean {
        for (permission in permission) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                return false
        }
        return true
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permission, REQUEST_PERMISSIONS_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: $result ")
                    showAlertDialog()
                    break
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this,R.style.AlertDialogCustomTheme)
        alertDialogBuilder.setTitle("Permission Required")
        alertDialogBuilder.setMessage("Permission required for the app to function properly.")
        alertDialogBuilder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            openAppSettings()
            dialog.dismiss() // Dismiss the dialog
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss() // Dismiss the dialog
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.main_color))

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.main_color))
    }
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}

