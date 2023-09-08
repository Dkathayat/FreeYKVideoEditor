package com.example.freeykvideoeditor.ui.editor


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import com.example.freeykvideoeditor.databinding.ActivityEditorBinding
import com.example.freeykvideoeditor.ui.ffmpeg.callback.FFmpegSession
import com.example.freeykvideoeditor.ui.ffmpeg.tools.ImagesToSingleVideo
import com.example.freeykvideoeditor.ui.ffmpeg.tools.MediaCompressor
import com.example.freeykvideoeditor.ui.ffmpeg.tools.MovieMaker
import com.example.freeykvideoeditor.ui.ffmpeg.tools.TextOnVideo
import com.example.freeykvideoeditor.utils.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class EditorActivity : AppCompatActivity(), FFmpegSession {

    private lateinit var binding: ActivityEditorBinding
    lateinit var audio2: File

    private val mediaCompressor: MediaCompressor by lazy { MediaCompressor(applicationContext) }
    private val utils: Utility by lazy { Utility(applicationContext) }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
                if (uris.isNotEmpty()) {

                    testExcution(uris)

//                    mediaCompressor.compressFiles(this, uris as ArrayList<Uri>) { compressedMedia ->
//                        if (compressedMedia.isNotEmpty()) {
//                            // shareMedia(compressedMedia)
//                        }
//
//                    }
                }
            }

        binding.addResources.setOnClickListener {
            if (isPhotoPickerAvailable()) {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            } else {
                // for api leve below 28
                launchMediaPickerIntent()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun testExcution(file: List<Uri>) {
        ImagesToSingleVideo.with(this)
            .setVideoUrl(file.first())
            .setImages(file)
            .setInterval("25")
            .setOutputFileName("merged_" + System.currentTimeMillis() + ".mp4")
            .setVideoDuration("10")
            //.setOutputPath(Uri.Builder)
            .getSession(this)
            .extract()
    }

    private fun textOnVideo(file: List<String>) {
        val video = file.first()
        TextOnVideo.with(this)
            .setFile(video)
            //.setOutputPath(Utils.outputPath + "video")
            //.setOutputFileName("textOnVideo_" + System.currentTimeMillis() + ".mp4")
            //.setFont(font) //Font .ttf of text
            .setText("Text Displayed on Video!!") //Text to be displayed
            .setColor("#50b90e") //Color of Text
            .setSize("34") //Size of text
            .addBorder(true) //This will add background with border on text
            .setPosition(TextOnVideo.POSITION_CENTER_BOTTOM) //Can be selected
            //.setCallback(this@MainActivity)
            .draw()
    }

    private fun moveMakerEx(imageList: MutableList<Uri>) {
        MovieMaker.with(this)
            .setAudio(audio2)
            .setFile(imageList)
           // .setOutputPath(Utils(this).outputPath + "video")
            .setOutputFileName("movie_" + System.currentTimeMillis() + ".mp4")
            //.setCallback(this)
            .convert()
    }

    private fun launchMediaPickerIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, "image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startForResultLauncher.launch(Intent.createChooser(intent, "Select Media"))
    }

    private val startForResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                data?.let { intent ->
                    val imageUris: MutableList<Uri> = mutableListOf()
                    // Check if data contains a single URI
                    val singleImageUri = intent.data
                    singleImageUri?.let {
                        imageUris.add(it)
                    }
                    // Check if data contains multiple URIs (e.g., when selecting multiple items)
                    val clipData = intent.clipData
                    clipData?.let {
                        for (i in 0 until clipData.itemCount) {
                            val uri = clipData.getItemAt(i).uri
                            imageUris.add(uri)
                        }
                    }
                    // Handle the selected images/videos here
                    handleSelectedMedia(imageUris)
                }
            }
        }


    private fun handleSelectedMedia(uris: List<Uri>) {
        // Process the list of selected URIs (example)
        val imageList = mutableListOf<String>()
        for (uri in uris) {
            val inputVideoPath =
                FFmpegKitConfig.getSafParameterForRead(this, uri)
            imageList.add(inputVideoPath)
        }
        // testExcution(imageList)
        // Do something with each image/video URI, such as display it in an ImageView or upload it

    }

    override fun getSession(session: com.arthenica.ffmpegkit.FFmpegSession) {
        if (ReturnCode.isSuccess(session.returnCode)) {
//            val exoPlayer = ExoPlayer.Builder(this).build()
//            val mediaitem = MediaItem.fromUri("")
//
//            exoPlayer.apply {
//                setMediaItem(mediaitem)
//                seekTo(0)
//                playWhenReady = true
//            }.also {
//                binding.editorPlayer.player =  it
//            }

            // SUCCESS

        } else if (ReturnCode.isCancel(session.returnCode)) {

            // CANCEL

        } else {

            // FAILURE
            Log.d(
                "TAG",
                String.format(
                    "Command failed with state %s and rc %s.%s",
                    session.getState(),
                    session.getReturnCode(),
                    session.getFailStackTrace()
                )
            )

        }
    }

    private fun getInstalledAppInformation() = lifecycleScope.launch(Dispatchers.IO) {
        // Get the PackageManager instance
        val packageManager = packageManager

        // Get a list of all installed applications
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        installedApps.forEach { apps ->
            println("App name : ${apps.name}")
            println("App process name : ${apps.processName}")
            println("App package name : ${apps.packageName}")

        }

        // Iterate through the installed apps and retrieve their information
        for (appInfo in installedApps) {
            val appName = appInfo.loadLabel(packageManager).toString()
            val packageName = appInfo.packageName
            val versionName = packageManager.getPackageInfo(packageName, 0).versionName
            val versionCode = packageManager.getPackageInfo(packageName, 0).versionCode

            // Do something with the app information
            println("App Name: $appName")
            println("Package Name: $packageName")
            println("Version Name: $versionName")
            println("Version Code: $versionCode")
        }


    }

    override fun finish() {
        mediaCompressor.cancelAllOperations()
        //scheduleCacheCleanup()
        super.finish()
    }

    override fun onStop() {
        mediaCompressor.cancelAllOperations()
        super.onStop()
    }

}

