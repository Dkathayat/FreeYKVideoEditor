package com.example.freeykvideoeditor.ui.ffmpeg.tools

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.example.freeykvideoeditor.ui.ffmpeg.callback.FFMpegCallback
import java.io.File

class MovieMaker private constructor(private val context: Context) {

    private var images: MutableList<Uri>? = null
    private var audio: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""

    fun setFile(originalFiles: MutableList<Uri>): MovieMaker {
        this.images = originalFiles
        return this
    }

    fun setAudio(originalFiles: File): MovieMaker {
        this.audio = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): MovieMaker {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): MovieMaker {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): MovieMaker {
        this.outputFileName = output
        return this
    }

    fun convert() {

        //val outputLocation = Utils.getConvertedFile(outputPath, outputFileName)

        val outputDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val outputFileName = "${System.currentTimeMillis()}-ykeditor.mp4"

// Construct the full output file path
        val outputFile = File(outputDirectory, outputFileName)
        val outputPath = outputFile.absolutePath

        //Here the images are from image1.png to image5.png
        //framerate 1/3.784 means, each image runs for 3.784 seconds
        //c:v libx264: video codec H.264
        //r 30: output video fps 30
        //pix_fmt yuv420p: output video pixel format
        //c:a aac: encode the audio using aac
        //shortest: end the video as soon as the audio is done.
        val cmd5 = arrayOf(
            "-analyzeduration",
            "1M",
            "-probesize",
            "1M",
            "-y",
            "-framerate",
            "1/3.79",
            "-i",
            "image%d.png",
            "-i",
            audio!!.path,
            "-c:v",
            "libx264",
            "-r",
            "30",
            "-pix_fmt",
            "yuv420p",
            "-c:a",
            "aac",
            "-shortest",
            outputPath
        )

        val ffmpegCommand = """
             -analyzeduration 1000000  
             -probesize 1000000        
             -y
             -framerate 1/3.79
             -i image%d.png
             -i ${audio!!.path}
             -c:v libx264
             -r 30
             -pix_fmt yuv420p
             -c:a aac
             -shortest
             $outputPath
                          """.trimIndent()

        try {

            FFmpegKit.executeAsync(ffmpegCommand, { session ->
                val state = session.state
                val returnCode = session.returnCode
                // CALLED WHEN SESSION IS EXECUTED
                Log.d(
                    ImagesToSingleVideo.TAG, String.format(
                        "FFmpeg process exited with state %s and rc %s.%s",
                        state,
                        returnCode,
                        session.failStackTrace
                    )
                )
            }, {
                println("This are FFmpegKit logs :: $it")
            }) {
                println("This are FFmpegKit statics logs :: $it")
            }
        } catch (e: Exception) {
            // callback!!.onFailure(e)
        }

//        try {
//            FFmpeg.getInstance(context).execute(cmd5.toString(), object : ExecuteBinaryResponseHandler() {
//                override fun onStart() {}
//
//                override fun onProgress(message: String?) {
//                    callback!!.onProgress(message!!)
//                }
//
//                override fun onSuccess(message: String?) {
//                    FfmpegUtility.refreshGallery(outputLocation.path, context)
//                   // callback!!.onSuccess(outputLocation, OutputType.TYPE_VIDEO)
//
//                }
//
//                override fun onFailure(message: String?) {
//                    if (outputLocation.exists()) {
//                        outputLocation.delete()
//                    }
//                    callback!!.onFailure(IOException(message))
//                }
//
//                override fun onFinish() {
//                    callback!!.onFinish()
//                }
//            })
//        } catch (e: Exception) {
//            callback!!.onFailure(e)
//        } catch (e2: FFmpegCommandAlreadyRunningException) {
//            callback!!.onNotAvailable(e2)
//        }

    }

    companion object {

        val TAG = "MovieMaker"
        fun with(context: Context): MovieMaker {
            return MovieMaker(context)
        }
    }
}