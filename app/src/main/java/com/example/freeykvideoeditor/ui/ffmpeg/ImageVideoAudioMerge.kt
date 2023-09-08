package com.example.freeykvideoeditor.ui.ffmpeg

import android.content.Context
import com.example.freeykvideoeditor.ui.ffmpeg.callback.FFMpegCallback
import java.io.File
import java.io.IOException

class ImageVideoAudioMerge private constructor(private val context: Context) {

    private var audio: File? = null
    private var video: File? = null
    private var image: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""


    fun setAudioFile(originalFile: File): ImageVideoAudioMerge {
        this.audio = originalFile
        return this
    }

    fun setVideoFile(originalFile: File): ImageVideoAudioMerge {
        this.video = originalFile
        return this
    }

    fun setImageFile(originalFile: File): ImageVideoAudioMerge {
        this.image = originalFile
        return this
    }

    fun setCallback(callback: FFMpegCallback): ImageVideoAudioMerge {
        this.callback = callback
        return this
    }

    fun setOutputPath(path: String): ImageVideoAudioMerge {
        this.outputPath = path
        return this
    }

    fun merge() {

        if (audio == null || !audio!!.exists() || video == null || !video!!.exists() || image == null || !image!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!audio!!.canRead() || !video!!.canRead() || !image!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val imageSlideshowCommand = arrayOf(
            "-framerate",
            "1/5", // Set the framerate (1 image per 5 seconds)
            image!!.path,
            "path/to/images/image%d.jpg", // Replace with the path to your images
            "-vf",
            "scale=1920:-1", // Resize images to a common resolution
            "-t",
            "10", // Set the output video duration to 10 seconds
            "-c:v",
            "libx264",
            "-r",
            "30", // Set the output video frame rate
            outputPath // Replace with the output path and filename
        )

//        try {
////            FFmpegKit.executeAsync(
////                imageSlideshowCommand.toString()
////            ) { executionId, returnCode ->
////                if (returnCode == RETURN_CODE_SUCCESS){
////                    callback?.onSuccess(outputPath, OutputType.TYPE_VIDEO)
////                }
//            }
//        }catch (e:Exception){
//            callback!!.onFailure(e)
//        }

    }

    companion object {
        val TAG = "ImageVideoAudioMerge"
        fun with(context: Context): ImageVideoAudioMerge {
            return ImageVideoAudioMerge(context)
        }
    }
}