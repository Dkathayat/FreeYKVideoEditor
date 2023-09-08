package com.example.freeykvideoeditor.utils

import android.content.Context
import java.io.File

val Context.mediaCacheDir: File
    get() {
        File(externalCacheDir, "media").mkdirs()
        return File(externalCacheDir, "media")
    }
