package com.example.downloadapplication

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class ViaDownloadManager : AppCompatActivity() {
    private val fileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_via_download_manager)
        registerReceiver(downloadBroadcastReceiver, IntentFilter())
        findViewById<Button>(R.id.download_btn).setOnClickListener {
            downloadFile()
        }
    }

    private fun downloadFile() {
        val downloadRequest = DownloadManager.Request(Uri.parse(fileUrl)).apply {
            setDescription("Download File")
            setMimeType("application/pdf")
            setTitle("Download")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        }
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(downloadRequest)
    }

    private val downloadBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(this@ViaDownloadManager, intent?.data.toString(), Toast.LENGTH_SHORT)
                .show()
        }

    }
}