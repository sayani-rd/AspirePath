package com.example.aspirepath

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.aspirepath.models.CVData
import com.example.aspirepath.utils.CVPdfGenerator
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class CVPreviewActivity : AppCompatActivity() {

    private lateinit var ivPdfPreview: ImageView
    private lateinit var btnDownload: Button
    private lateinit var generatedPdfFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cv_preview)

        ivPdfPreview = findViewById(R.id.ivPdfPreview)
        btnDownload = findViewById(R.id.btnDownload)

        val cvData = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("CV_DATA", CVData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("CV_DATA")
        }

        val templateId = intent.getStringExtra("TEMPLATE_ID") ?: "modern"

        if (cvData != null) {
            val generator = CVPdfGenerator(this)
            val file = generator.generatePDF(cvData, templateId)
            
            if (file != null) {
                generatedPdfFile = file
                showPdfPreview(file)
            } else {
                Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnDownload.setOnClickListener {
            savePdfToDownloads()
        }
    }

    private fun showPdfPreview(file: File) {
        try {
            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fileDescriptor)
            
            if (renderer.pageCount > 0) {
                val page = renderer.openPage(0)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                ivPdfPreview.setImageBitmap(bitmap)
                page.close()
            }
            
            renderer.close()
            fileDescriptor.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to preview PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePdfToDownloads() {
        if (!::generatedPdfFile.isInitialized || !generatedPdfFile.exists()) return

        val fileName = "MyCV_${System.currentTimeMillis()}.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { outputStream ->
                        FileInputStream(generatedPdfFile).copyTo(outputStream)
                    }
                    Toast.makeText(this, "Saved to Downloads", Toast.LENGTH_SHORT).show()
                    openPdf(it)
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val destFile = File(downloadsDir, fileName)
                FileInputStream(generatedPdfFile).copyTo(FileOutputStream(destFile))
                Toast.makeText(this, "Saved to ${destFile.absolutePath}", Toast.LENGTH_LONG).show()
                
                // Trigger media scan
                val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", destFile)
                intent.data = uri
                sendBroadcast(intent)
                
                openPdf(uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openPdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(intent, "Open CV"))
    }
}
