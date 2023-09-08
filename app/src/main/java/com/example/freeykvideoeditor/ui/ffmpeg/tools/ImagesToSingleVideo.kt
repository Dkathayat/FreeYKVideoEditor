package com.example.freeykvideoeditor.ui.ffmpeg.tools

import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFprobeKit
import com.example.freeykvideoeditor.ui.ffmpeg.callback.FFmpegSession
import com.example.freeykvideoeditor.utils.Settings
import com.example.freeykvideoeditor.utils.Utility
import timber.log.Timber
import java.io.File


class ImagesToSingleVideo private constructor(val context: Context) {
    //  private var videoUrl: File? = null
    private var inputUri: Uri? = null
    private var inputImages : List<Uri>? = null
    private var fFmpegSession: FFmpegSession? = null
    private var outputFileName = ""
    private var outputPath: File? = null
    private var interval = ""
    private var videoDuration = ""

    private val utils: Utility by lazy { Utility(context) }
    private val utility: Utility by lazy { Utility(context) }
    private val settings: Settings by lazy { Settings(context) }

    fun setVideoUrl(link: Uri): ImagesToSingleVideo {
        this.inputUri = link
        return this
    }

    fun setImages(imgs: List<Uri>): ImagesToSingleVideo {
        this.inputImages = imgs
        return this
    }

    fun setOutputFileName(name: String): ImagesToSingleVideo {
        this.outputFileName = name
        return this
    }

    fun setInterval(output: String): ImagesToSingleVideo {
        this.interval = output
        return this
    }

    fun setOutputPath(output: File): ImagesToSingleVideo {
        this.outputPath = output
        return this
    }

    fun setVideoDuration(time: String): ImagesToSingleVideo {
        this.videoDuration = time
        return this
    }

    fun getSession(session: FFmpegSession): ImagesToSingleVideo {
        this.fFmpegSession = session
        return this
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun extract() {
        val mediaType = utility.getMediaType(inputUri!!)
        if (!utility.supportedMediaType(mediaType)) { // not supported show error
            Toast.makeText(context, "Unknown file type", Toast.LENGTH_LONG).show()
            //failureHandler()
            return
        }

        val showProgress = !utility.isImage(mediaType)
        if (!showProgress) {
            Toast.makeText(context, "This is a image file", Toast.LENGTH_SHORT).show()
//            processedTableRow.visibility = View.INVISIBLE
        }

        val inputFileName = utils.getFilenameFromUri(inputUri!!) ?: "unknown"
        val (outputFile, outputMediaType) = utility.getCacheOutputFile(inputUri!!, mediaType)

        val outputFileUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".fileprovider",
            outputFile
        )

        val mediaInformation = FFprobeKit.getMediaInformation(
            FFmpegKitConfig.getSafParameterForRead(
                context,
                inputUri
            )
        ).mediaInformation

        if (mediaInformation == null) {
            println("Unable to get media information, throwing error")
            Toast.makeText(
                context,
                "Invalid file",
                Toast.LENGTH_LONG
            ).show()
            //failureHandler()
            return
        }

        val inputFileSize = mediaInformation.size.toLong() // get input file size

        var duration = 0 // default duration for image
        if (showProgress) {
            // invalid video file if ffprobe cant parse duration and size
            if (mediaInformation.duration == null || mediaInformation.size == null) {
               println("Unable to get size & duration for media, throwing error")
                Toast.makeText(context,"Invalid File!" ,Toast.LENGTH_LONG).show()
                //failureHandler()
                return
            }
            duration = (mediaInformation.duration.toFloat() * 1_000).toInt()
        }

        val outputSaf: String = FFmpegKitConfig.getSafParameterForWrite(context, outputFileUri)

        val inputSaf: String = FFmpegKitConfig.getSafParameterForRead(context, inputUri)

        val ffmpegCommand = "-i $inputSaf -ss 0 -t 5 -c:v copy -c:a copy $outputSaf"
        val ffmpegCommand2 = "-framerate 1/2 -i $inputImages -c:v libx264 -t 10 $outputSaf"
        val newCommand = "-loop 1 -i $inputSaf -vf format=yuv420p -t 30 $outputSaf"

        val complexCommand = arrayOf<String>(
            "-framerate", "30",
            "-i", inputSaf,
            "-i", inputSaf,
            // Add more -i options for additional images if needed
            "-c:v", "h264",
            "-b:v", "5000k", // Adjust the bitrate as needed
            "-s", "1920x1080", // Adjust the resolution as needed
            "-r", "30",
            "-pix_fmt", "yuv420p",
            outputSaf
        ).joinToString(" ")

        try {
            FFmpegKit.executeAsync(complexCommand,
                { session ->
                    fFmpegSession?.getSession(session)
                    val state = session.state
                    val returnCode = session.returnCode
                    // CALLED WHEN SESSION IS EXECUTED
                    Timber.tag(TAG).d(
                        String.format(
                            "FFmpeg process exited with state %s and rc %s.%s",
                            state,
                            returnCode,
                            session.failStackTrace
                        )
                    )
                }, {
                    println("This are FFmpegKit logs :: $it")
                }) {
               // txtProcessedPercent.text = context.getString(R.string.format_percentage, (statistics.time.toFloat() / duration) * 100)
                println("This are FFmpegKit statics logs :: $it")


            }
        } catch (e: Exception) {
            println("FmpegKit exception :: $e")
        }
    }

    companion object {

        val TAG = "ImagesToSingleVideo"
        fun with(context: Context): ImagesToSingleVideo {
            return ImagesToSingleVideo(context)
        }
    }
}