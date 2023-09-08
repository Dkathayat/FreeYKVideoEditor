//package com.example.freeykvideoeditor.utils
//
//import android.content.ContentValues
//import android.content.Context
//import android.content.Intent
//import android.media.MediaScannerConnection
//import android.net.Uri
//import android.os.Build
//import android.os.Environment
//import android.provider.MediaStore
//import android.provider.OpenableColumns
//import android.util.Log
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.core.net.toUri
//import com.example.freeykvideoeditor.utils.SharedConstants.Companion.APP_FOLDER
//import java.io.File
//import java.io.FileNotFoundException
//import java.io.FileOutputStream
//import java.io.IOException
//import java.io.InputStream
//import java.util.UUID
//
//
//class Utils(private val context: Context) {
//    val outputPath: String
//        get() {
//            val path =
//                Environment.getExternalStorageDirectory().toString() + File.separator + APP_FOLDER + File.separator
//
//            val folder = File(path)
//            if (!folder.exists())
//                folder.mkdirs()
//
//            return path
//        }
//
//    fun getConvertedFile(uri: Uri, fileName: String): File {
//
//        return File(makeCacheUUIDFolder(), fileName)
//    }
//     fun makeCacheUUIDFolder(): File {
//        val cacheUUIDFolder = File(context.externalCacheDir,null)
//        cacheUUIDFolder.mkdirs()
//        return cacheUUIDFolder
//    }
//
//     fun saveVideoForFFmpeg(videoUri: Uri) {
//
//         val videoCollection = sdk29AndUp {
//             MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//         } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//
//         val contentValues = ContentValues().apply {
//             put(MediaStore.Video.Media.DISPLAY_NAME, "${UUID.randomUUID()}.mp4")
//             put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
//         }
//
//         try {
//             val contentResolver = context.applicationContext.contentResolver
//             val newUri = contentResolver.insert(videoCollection, contentValues)
//
//             if (newUri != null) {
//                 contentResolver.openOutputStream(newUri)?.use { outputStream ->
//                     contentResolver.openInputStream(videoUri)?.use { inputStream ->
//                         inputStream.copyTo(outputStream)
//                         println("Save to movies :: $videoUri")
//                     }
//                 }
//             }
//
//         } catch (e: IOException) {
//             e.printStackTrace()
//             println("Failed to save to movies ::$e")
//         }
//
//     }
//
//
//    fun saveVideoToMoviesDirectory(videoData: Uri): File? {
//        val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
//        val ykEditorFolder = File(moviesDir, APP_FOLDER)
//
//        if (!ykEditorFolder.exists()) {
//            if (!ykEditorFolder.mkdirs()) {
//                return null
//            }
//        }
//
//        val values = ContentValues().apply {
//            put(MediaStore.Video.Media.DISPLAY_NAME, getFilenameFromUri(videoData))
//            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
//        }
//
//
//        val contentResolver = context.contentResolver
//        contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
//
//        val videoFile = File(ykEditorFolder, "${System.currentTimeMillis()}-yk.mp4")
//
//        try {
//            context.contentResolver.openInputStream(videoData)?.use { inputStream ->
//                FileOutputStream(videoFile).use {
//                    inputStream.copyTo(it)
//                }
//            }
//            // Add the video to the MediaStore database
//            // Notify the MediaScanner to add the new file to the MediaStore
//            MediaScannerConnection.scanFile(
//                context,
//                arrayOf(videoFile.absolutePath),
//                null,
//                null
//            )
//            return videoFile
//            println("Save to movies :: $videoFile")
//
//        } catch (e: IOException) {
//            // Handle file write error
//            e.printStackTrace()
//            println("Failed to save to movies ::$e")
//        }
//        return null
//    }
//
//
//    fun copyFileToExternalStorage(resourceId: Int, resourceName: String, context: Context): File {
//        val pathSDCard = outputPath + resourceName
//        try {
//            val inputStream = context.resources.openRawResource(resourceId)
//            inputStream.toFile(pathSDCard)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        return File(pathSDCard)
//    }
//
//    fun InputStream.toFile(path: String) {
//        File(path).outputStream().use { this.copyTo(it) }
//    }
//
//    fun refreshGallery(path: String, context: Context) {
//        val file = File(path)
//        try {
//            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//            val contentUri = Uri.fromFile(file)
//            mediaScanIntent.data = contentUri
//            context.sendBroadcast(mediaScanIntent)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    fun milliSecondsToTimer(milliseconds: Long): String {
//        var finalTimerString = ""
//        var secondsString = ""
//
//        // Convert total duration into time
//        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
//        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
//        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
//        // Add hours if there
//        if (hours > 0) {
//            finalTimerString = hours.toString() + ":"
//        }
//
//        // Prepending 0 to seconds if it is one digit
//        if (seconds < 10) {
//            secondsString = "0" + seconds
//        } else {
//            secondsString = "" + seconds
//        }
//
//        finalTimerString = "$finalTimerString$minutes:$secondsString"
//
//        // return timer string
//        return finalTimerString
//    }
//
//
//    fun getMediaType(uri: Uri): MediaType {
//        val filename: String? = getFilenameFromUri(uri)
//        var mediaType: MediaType = MediaType.UNKNOWN
//
//        // get type from file extension
//        if (filename != null) {
//            val extensionsToMediaType = mapOf(
//                // images
//                "jpg" to MediaType.JPEG,
//                "jpeg" to MediaType.JPEG,
//                "png" to MediaType.PNG,
//                "gif" to MediaType.GIF,
//                // videos
//                "mp4" to MediaType.MP4,
//                "mkv" to MediaType.MKV,
//                "webm" to MediaType.WEBM,
//                "avi" to MediaType.AVI,
//                "mov" to MediaType.MP4,
//                // audios
//                "mp3" to MediaType.MP3,
//                "ogg" to MediaType.OGG,
//                "aac" to MediaType.AAC,
//                "wav" to MediaType.WAV
//            )
//
//            val extension = filename.substringAfterLast('.').lowercase()
//            mediaType = extensionsToMediaType[extension] ?: MediaType.UNKNOWN
//        }
//        // unable to get filetype from filename extension, using signature detection
//        // https://en.wikipedia.org/wiki/List_of_file_signatures
//        if (mediaType == MediaType.UNKNOWN) {
//            println("unable to find filetype from extension, trying file signature")
//            val inputStream = context.contentResolver.openInputStream(uri)
//            val signature = getFileHexSignature(inputStream!!)
//            inputStream.close()
//
//            if (signature.startsWith("FFD8FF")) {
//                mediaType = MediaType.JPEG
//            } else if (signature.startsWith("89504E470D0A1A0A")) {
//                mediaType = MediaType.PNG
//            } else if (signature.startsWith("47494638")) {
//                mediaType = MediaType.GIF
//            } else if (signature.drop(8).startsWith("6674797069736F6D")) { // ** ** ** ** 66 74 79 70 69 73 6F 6D
//                mediaType = MediaType.MP4
//            } else if (signature.startsWith("1A45DFA3")) { // or webm, but assume mkv, also not that big a deal as only happens when filename is not found
//                mediaType = MediaType.MKV
//            } else if (signature.startsWith("52494646") && signature.drop(16).startsWith("41564920")) { // 52 49 46 46 ** ** ** ** 41 56 49 20
//                mediaType = MediaType.AVI
//            } else if (signature.startsWith("494433") || signature.startsWith("FFFB")
//                || signature.startsWith("FFF3") || signature.startsWith("FFF2")) {
//                mediaType = MediaType.MP3
//            } else if (signature.startsWith("4F676753")) {
//                mediaType = MediaType.OGG
//            } else if (signature.startsWith("52494646") && signature.drop(16).startsWith("57415645")) { // 52 49 46 46 ** ** ** ** 57 41 56 45
//                mediaType = MediaType.WAV
//            }
//            if (mediaType == MediaType.UNKNOWN) {
//                println("Unable to find filetype from signature")
//            } else {
//                println("Found Filetype from signature $mediaType")
//            }
//        } else {
//            println("Found Filetype from extension $mediaType")
//        }
//
//        return mediaType
//    }
//    fun getFilenameFromUri(uri: Uri): String? {
//        var filename: String? = null
//        val queryCursor = context.contentResolver.query(uri, null, null, null, null)
//        queryCursor?.let { cursor ->
//            if (cursor.moveToFirst()) {
//                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (nameIndex != -1) {
//                    filename = cursor.getString(nameIndex)
//                }
//            }
//            cursor.close()
//        }
//        return filename
//    }
//
//    private fun getFileHexSignature(inputStream: InputStream): String {
//        val arr = ByteArray(8)
//        inputStream.read(arr)
//        val builder = StringBuilder()
//        for (byte in arr) { // foreach byte in head
//            val hex = "%02X".format(byte) // format as hex
//            builder.append(hex)
//        }
//        return builder.toString()
//    }
//
//    enum class MediaType {
//        MP4, MKV, WEBM, AVI, // videos
//        JPEG, PNG, GIF, // images
//        MP3, OGG, AAC, WAV, // audios
//        UNKNOWN
//    }
//}