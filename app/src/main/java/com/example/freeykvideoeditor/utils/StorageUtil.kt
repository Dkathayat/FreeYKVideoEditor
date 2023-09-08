package com.example.freeykvideoeditor.utils

import android.content.Context
import android.os.Build
import java.io.File

inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else null
}
