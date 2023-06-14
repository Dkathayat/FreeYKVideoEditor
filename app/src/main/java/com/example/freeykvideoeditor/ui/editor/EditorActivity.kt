package com.example.freeykvideoeditor.ui.editor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.freeykvideoeditor.R
import com.example.freeykvideoeditor.databinding.ActivityEditorBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File


class EditorActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityEditorBinding

    private var audio: File? = null
    private var video: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_editor)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            //LogDumpGetUnique()
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_editor)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun ffmpegTestImp() {
        val cmd = arrayOf(
            "-i",
            video!!.path,
            "-i",
            audio!!.path,
            "-c:v",
            "copy",
            "-c:a",
            "aac",
            "-strict",
            "experimental",
            "-map",
            "0:v:0",
            "-map",
            "1:a:0",
            "-shortest",
            "outputLocation.path"
        )
        // FFmpeg.execute(cmd,object : ExecuteBinaryResponseHandler(){

        // })
    }


    fun LogDumpGetUnique(): String {
       Log.d("ThisISTAG", isPhotoPickerAvailable().toString())
        var log_dump: String =
            "name=John Trust, username=john3, email=john3@gmail.com, id=434453; name=Hannah Smith, username=hsmith, email=hsm@test.com, id=23312; name=Hannah Smith, username=hsmith, id=3223423, email=hsm@test.com; name=Robert M, username=rm44, id=222342, email=rm@me.com; name=Robert M, username=rm4411, id=5535, email=rm@me.com; name=Susan Vee, username=sv55, id=443432, email=susanv123@me.com; name=Robert Nick, username=rnick33, id=23432, email=rnick@gmail.com; name=Robert Nick II, username=rnickTemp34, id=23432, email=rnick@gmail.com; name=Susan Vee, username=sv55, id=443432, email=susanv123@me.com;"
        // code goes here
        val username = mutableSetOf<String>()
        val uniqueLogs = mutableListOf<String>()

        for (log in log_dump.split(",")) {
            val usernameSeen = log.substringAfter("username=").substringBefore(",")
            if (usernameSeen !in username) {
                username.add(usernameSeen)
                uniqueLogs.add(log.replaceFirst(", id=\\d+".toRegex(), ""))
            }
        }
        log_dump = uniqueLogs.joinToString(",")
        Log.d("log_dump", log_dump)
        return log_dump

    }

}