package com.example.freeykvideoeditor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.freeykvideoeditor.databinding.ActivityMainBinding
import com.example.freeykvideoeditor.ui.adapter.HomePagerAdapter
import com.example.freeykvideoeditor.ui.editor.EditorActivity
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private val permission = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

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
        ActivityCompat.requestPermissions(this, permission, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    break
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    fun testing(){
        Log.d("logd","this is just for testing")
    }
}