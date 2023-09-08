package com.example.freeykvideoeditor.ui.ffmpeg.callback

import java.io.File

interface FFMpegCallback {
    fun onProgress(progress: String)

    fun onSuccess(convertedFile: String, type: String)

    fun onFailure(error: Exception)

    fun onNotAvailable(error: Exception)

    fun onFinish()
}