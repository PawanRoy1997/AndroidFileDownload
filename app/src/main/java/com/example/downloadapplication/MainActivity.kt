package com.example.downloadapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.example.downloadapplication.R.id.download_btn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.net.URL


class MainActivity : AppCompatActivity() {
    private lateinit var launcher: ActivityResultLauncher<String>
    private lateinit var downloadBtn: Button
    private val fileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downloadBtn = findViewById(download_btn)
        launcher = registerForActivityResult(CreateDocumentContract()) {
            downloadAndSave(it)
        }
    }

    private fun downloadAndSave(data: Pair<String, String>?) {
        if (data == null) return
        val url = URL(data.first)

        val contentResolver = this.contentResolver

        CoroutineScope(Dispatchers.IO).launch {
            DataInputStream(withContext(Dispatchers.IO) {
                url.openStream()
            }).use { inputStream ->
                contentResolver.openOutputStream(Uri.parse(data.second)).use { outputStream ->
                    if (outputStream != null)
                        inputStream.copyTo(outputStream)
                }
            }
        }
        Toast.makeText(this, "Operation Complete", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        downloadBtn.setOnClickListener { downloadFile() }
    }

    private fun downloadFile() {
        launcher.launch(fileUrl)
    }

    class CreateDocumentContract : ActivityResultContract<String, Pair<String, String>>() {
        private lateinit var fileUrl: String

        override fun createIntent(context: Context, input: String): Intent {
            fileUrl = input
            return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                putExtra(Intent.EXTRA_TITLE, "New Document")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    putExtra(
                        DocumentsContract.EXTRA_INITIAL_URI,
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI
                    )
                }
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Pair<String, String> {
            val filePath = intent?.data.toString()
            return Pair(fileUrl, filePath)
        }

    }
}