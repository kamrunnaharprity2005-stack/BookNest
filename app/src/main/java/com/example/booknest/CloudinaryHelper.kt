package com.example.booknest

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest

object CloudinaryHelper {

    private const val CLOUD_NAME = "ug2qzb59"
    private const val UPLOAD_PRESET = "hdm7imga"
    private const val API_KEY = "817448716591871"
    private const val API_SECRET = " "

    private var initialized = false

    private fun init(context: Context) {
        if (!initialized) {
            MediaManager.init(context.applicationContext, mapOf("cloud_name" to CLOUD_NAME))
            initialized = true
        }
    }

    fun upload(
        context: Context,
        uri: Uri,
        onSuccess: (url: String, publicId: String) -> Unit,
        onError: (String) -> Unit
    ) {
        init(context)
        val successCb = onSuccess
        val errorCb = onError
        MediaManager.get().upload(uri)
            .unsigned(UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("secure_url") as? String
                    val publicId = resultData?.get("public_id") as? String
                    if (url != null && publicId != null) successCb(url, publicId)
                    else errorCb("Upload failed")
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    errorCb(error?.description ?: "Upload failed")
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    errorCb(error?.description ?: "Upload failed")
                }
            })
            .dispatch()
    }

    fun delete(publicId: String, onDone: (Boolean) -> Unit) {
        if (publicId.isEmpty()) {
            onDone(true)
            return
        }
        Thread {
            var ok = false
            try {
                val timestamp = (System.currentTimeMillis() / 1000).toString()
                val toSign = "public_id=$publicId&timestamp=$timestamp$API_SECRET"
                val signature = MessageDigest.getInstance("SHA-1")
                    .digest(toSign.toByteArray())
                    .joinToString("") { "%02x".format(it) }
                val body = "public_id=${URLEncoder.encode(publicId, "UTF-8")}" +
                        "&timestamp=$timestamp&api_key=$API_KEY&signature=$signature"

                val conn = URL("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/destroy")
                    .openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.outputStream.use { it.write(body.toByteArray()) }
                ok = conn.responseCode in 200..299
                conn.disconnect()
            } catch (_: Exception) {
            }
            Handler(Looper.getMainLooper()).post { onDone(ok) }
        }.start()
    }
}