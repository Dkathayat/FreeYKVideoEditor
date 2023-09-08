package com.example.freeykvideoeditor.ui.ffmpeg.tools

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.example.freeykvideoeditor.utils.SharedConstants.Companion.APP_FOLDER
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class TextOnVideo private constructor(private val context: Context) {

    private var video: String? = null

    private var outputPath = ""
    private var outputFileName = ""
    private var font: File? = null
    private var text: String? = null
    private var position: String? = null
    private var color: String? = null
    private var size: String? = null
    private var border: String? = null
    private var addBorder: Boolean? = null

    //Border
    var BORDER_FILLED = ": box=1: boxcolor=black@0.5:boxborderw=5"
    var BORDER_EMPTY = ""

    fun setFile(originalFiles: String): TextOnVideo {
        this.video = originalFiles
        return this
    }


    fun setOutputPath(output: String): TextOnVideo {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): TextOnVideo {
        this.outputFileName = output
        return this
    }

    fun setFont(output: File): TextOnVideo {
        this.font = output
        return this
    }

    fun setText(output: String): TextOnVideo {
        this.text = output
        return this
    }

    fun setPosition(output: String): TextOnVideo {
        this.position = output
        return this
    }

    fun setColor(output: String): TextOnVideo {
        this.color = output
        return this
    }

    fun setSize(output: String): TextOnVideo {
        this.size = output
        return this
    }

    fun addBorder(output: Boolean): TextOnVideo {
        if (output)
            this.border = BORDER_FILLED
        else
            this.border = BORDER_EMPTY
        return this
    }

    fun draw() {

//        val outputDirectory =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
//        val outputFileName = "${System.currentTimeMillis()}/ykeditor.mp3"
//
//// Construct the full output file path
//        val outputFile = File(outputDirectory, outputFileName)
//        val outputPath = outputFile.absolutePath



        val outputFileName = "${UUID.randomUUID()}/ykeditor.mp3"

// Prepare the output directory path
        val outputDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), APP_FOLDER)
        outputDirectory.mkdirs() // Create the directory if it doesn't exist

// Prepare the output file
        val outputFile = File(outputDirectory, outputFileName)
        val outputStream = FileOutputStream(outputFile)
        outputStream.write(outputFile.absolutePath.toByteArray())
        outputStream.close()

        val cmd = arrayOf(
            "-i",
            video,
            "-vf",
            "drawtext=fontfile=:text=$text: fontcolor=$color: fontsize=$size$border: $position",
            "-c:v",
            "libx264",
            "-c:a",
            "copy",
            "-movflags",
            "+faststart",
            outputPath
        )

        val new =
            " -i $video -vf \"drawtext=text='$text':x=10:y=10:fontsize=24:fontcolor=white\" $outputPath"


        try {
            FFmpegKit.executeAsync("-i $video -vn ${outputFile.absolutePath}",
                { session ->
                    val state = session.state
                    val returnCode = session.returnCode
                    // CALLED WHEN SESSION IS EXECUTED
                    Log.d(
                        ImagesToSingleVideo.TAG,
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
                println("This are FFmpegKit statics logs :: $it")
            }

        } catch (e: Exception) {
            println("Got exeption exucting ffmpeg :: $e")
        }


    }

    companion object {

        val TAG = "TextOnVideo"

        fun with(context: Context): TextOnVideo {
            return TextOnVideo(context)
        }

        //Positions
        var POSITION_BOTTOM_RIGHT = "x=w-tw-10:y=h-th-10"
        var POSITION_TOP_RIGHT = "x=w-tw-10:y=10"
        var POSITION_TOP_LEFT = "x=10:y=10"
        var POSITION_BOTTOM_LEFT = "x=10:h-th-10"
        var POSITION_CENTER_BOTTOM = "x=(main_w/2-text_w/2):y=main_h-(text_h*2)"
        var POSITION_CENTER_ALLIGN = "x=(w-text_w)/2: y=(h-text_h)/3"
    }
}