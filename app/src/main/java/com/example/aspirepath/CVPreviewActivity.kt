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
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.aspirepath.models.CVData
import com.example.aspirepath.utils.CVDataSerializer
import com.example.aspirepath.utils.CVPdfGenerator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class CVPreviewActivity : AppCompatActivity() {

    private lateinit var ivPdfPreview: ImageView
    private lateinit var btnDownload: Button
    private lateinit var btnSaveToProfile: Button
    private lateinit var ivMoreOptions: ImageView
    private lateinit var ivBack: ImageView
    private lateinit var tvHeaderTitle: TextView
    private lateinit var layoutBottomActions: View
    private lateinit var currentPdfFile: File
    private var generatedCvData: CVData? = null
    private var generatedTemplateId: String = "modern"
    private var openedFromProfile: Boolean = false
    private var savedCvDataBase64: String? = null
    private var savedCvDocId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cv_preview)

        ivPdfPreview = findViewById(R.id.ivPdfPreview)
        btnDownload = findViewById(R.id.btnDownload)
        btnSaveToProfile = findViewById(R.id.btnSaveToProfile)
        ivMoreOptions = findViewById(R.id.ivMoreOptions)
        ivBack = findViewById(R.id.ivBack)
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)
        layoutBottomActions = findViewById(R.id.layoutBottomActions)

        openedFromProfile = intent.getBooleanExtra("OPEN_SAVED_CV", false)
        if (openedFromProfile) {
            openSavedCvMode()
            return
        }

        val cvData = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("CV_DATA", CVData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("CV_DATA")
        }

        val templateId = intent.getStringExtra("TEMPLATE_ID") ?: "modern"
        generatedTemplateId = templateId
        generatedCvData = cvData

        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener {
            val intent = Intent(this, CVTemplateSelectionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        if (cvData != null) {
            val generator = CVPdfGenerator(this)
            val file = generator.generatePDF(cvData, templateId)
            
            if (file != null) {
                currentPdfFile = file
                showPdfPreview(file)
            } else {
                Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnDownload.setOnClickListener {
            savePdfToDownloads()
        }

        btnSaveToProfile.setOnClickListener {
            val data = generatedCvData
            if (!::currentPdfFile.isInitialized || data == null) {
                Toast.makeText(this, "Generate CV first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveLatestCvToProfile(currentPdfFile, generatedTemplateId, data)
        }
    }

    private fun openSavedCvMode() {
        tvHeaderTitle.text = "Saved CV"
        layoutBottomActions.visibility = View.GONE
        ivMoreOptions.visibility = View.VISIBLE
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val uriString = intent.getStringExtra("SAVED_CV_URI")
        val path = intent.getStringExtra("SAVED_CV_PATH")
        generatedTemplateId = intent.getStringExtra("SAVED_TEMPLATE_ID") ?: "modern"
        savedCvDataBase64 = intent.getStringExtra("SAVED_CV_DATA_BASE64")
        savedCvDocId = intent.getStringExtra("SAVED_CV_ID")

        val file = when {
            !path.isNullOrBlank() -> File(path)
            !uriString.isNullOrBlank() -> {
                val uri = Uri.parse(uriString)
                if (uri.scheme == "file") File(uri.path ?: "") else null
            }
            else -> null
        }

        if (file == null || !file.exists()) {
            Toast.makeText(this, "Saved CV file not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentPdfFile = file
        showPdfPreview(file)

        ivMoreOptions.setOnClickListener { anchor ->
            showSavedCvMenu(anchor)
        }
    }

    private fun showSavedCvMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, 1, 1, "Edit")
        popup.menu.add(0, 2, 2, "Delete")
        popup.menu.add(0, 3, 3, "Download")
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                1 -> {
                    editSavedCv()
                    true
                }
                2 -> {
                    deleteSavedCv()
                    true
                }
                3 -> {
                    savePdfToDownloads()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun editSavedCv() {
        val encoded = savedCvDataBase64
        if (encoded.isNullOrBlank()) {
            Toast.makeText(this, "Saved CV data not available for editing", Toast.LENGTH_SHORT).show()
            return
        }

        val cvData = CVDataSerializer.decode(encoded)
        if (cvData == null) {
            Toast.makeText(this, "Unable to load CV for editing", Toast.LENGTH_SHORT).show()
            return
        }

        val editIntent = Intent(this, CVDetailsInputActivity::class.java).apply {
            putExtra("CV_DATA", cvData)
            putExtra("TEMPLATE_ID", generatedTemplateId)
        }
        startActivity(editIntent)
    }

    private fun deleteSavedCv() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .let { userDoc ->
                val docId = savedCvDocId
                if (!docId.isNullOrBlank()) {
                    userDoc.collection("savedCVs").document(docId)
                        .delete()
                        .addOnSuccessListener {
                            syncLatestCvAfterDelete(userDoc) {
                                if (::currentPdfFile.isInitialized && currentPdfFile.exists()) {
                                    currentPdfFile.delete()
                                }
                                Toast.makeText(this, "Saved CV deleted", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to delete CV: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Backward compatibility for previously stored single CV
                    userDoc.update("latestCV", FieldValue.delete())
                        .addOnSuccessListener {
                            if (::currentPdfFile.isInitialized && currentPdfFile.exists()) {
                                currentPdfFile.delete()
                            }
                            Toast.makeText(this, "Saved CV deleted", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to delete CV: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun syncLatestCvAfterDelete(userDoc: DocumentReference, onDone: () -> Unit) {
        userDoc.collection("savedCVs")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val top = snapshot.documents.firstOrNull()
                if (top == null) {
                    userDoc.update("latestCV", FieldValue.delete())
                        .addOnCompleteListener { onDone() }
                } else {
                    val cvMap = mapOf(
                        "name" to (top.getString("name") ?: ""),
                        "path" to (top.getString("path") ?: ""),
                        "uri" to (top.getString("uri") ?: ""),
                        "templateId" to (top.getString("templateId") ?: "modern"),
                        "candidateName" to (top.getString("candidateName") ?: ""),
                        "cvDataBase64" to (top.getString("cvDataBase64") ?: ""),
                        "updatedAt" to (top.getTimestamp("updatedAt") ?: com.google.firebase.Timestamp.now())
                    )

                    userDoc.update("latestCV", cvMap)
                        .addOnCompleteListener { onDone() }
                }
            }
            .addOnFailureListener {
                onDone()
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
        if (!::currentPdfFile.isInitialized || !currentPdfFile.exists()) return

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
                        FileInputStream(currentPdfFile).copyTo(outputStream)
                    }
                    Toast.makeText(this, "Saved to Downloads", Toast.LENGTH_SHORT).show()
                    openPdf(it)
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val destFile = File(downloadsDir, fileName)
                FileInputStream(currentPdfFile).copyTo(FileOutputStream(destFile))
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

    private fun saveLatestCvToProfile(file: File, templateId: String, cvData: CVData) {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val cvMap = hashMapOf(
            "name" to file.name,
            "path" to file.absolutePath,
            "uri" to Uri.fromFile(file).toString(),
            "templateId" to templateId,
            "candidateName" to cvData.personalInfo.fullName,
            "cvDataBase64" to CVDataSerializer.encode(cvData),
            "updatedAt" to com.google.firebase.Timestamp.now()
        )

        val userDoc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)

        userDoc.collection("savedCVs")
            .add(cvMap)
            .addOnSuccessListener {
                // Keep latestCV as convenience metadata; this update should not block primary save success.
                userDoc.update("latestCV", cvMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "CV saved to profile", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "CV saved. Latest CV info not updated.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                val message = if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    "Save blocked by Firestore rules. Publish updated rules and sign in again."
                } else {
                    "Failed to save CV: ${e.message}"
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
    }
}
