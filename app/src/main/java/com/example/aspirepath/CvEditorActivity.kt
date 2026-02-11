package com.example.aspirepath

import android.content.ContentValues
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aspirepath.model.CvData
import com.example.aspirepath.model.CvTemplate
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException

class CvEditorActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etUniversity: TextInputEditText
    private lateinit var etDegree: TextInputEditText
    private lateinit var etYear: TextInputEditText
    private lateinit var etExperience: TextInputEditText
    private lateinit var etSkills: TextInputEditText
    private lateinit var btnGeneratePdf: Button
    private lateinit var layoutColorPicker: LinearLayout

    private var selectedTemplateId: Int = 1
    private var selectedColorHex: String = "#2196F3" // Default Blue
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cv_editor)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        selectedTemplateId = intent.getIntExtra("TEMPLATE_ID", 1)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etUniversity = findViewById(R.id.etUniversity)
        etDegree = findViewById(R.id.etDegree)
        etYear = findViewById(R.id.etYear)
        etExperience = findViewById(R.id.etExperience)
        etSkills = findViewById(R.id.etSkills)
        btnGeneratePdf = findViewById(R.id.btnGeneratePdf)
        layoutColorPicker = findViewById(R.id.layoutColorPicker)

        setupColorPicker()
        loadUserData()

        btnGeneratePdf.setOnClickListener {
            saveCvData()
            generatePdf()
        }
    }

    private fun setupColorPicker() {
        for (i in 0 until layoutColorPicker.childCount) {
            val view = layoutColorPicker.getChildAt(i)
            view.setOnClickListener { v ->
                // Reset all opacities or borders (simple visual feedback)
                for (j in 0 until layoutColorPicker.childCount) {
                    layoutColorPicker.getChildAt(j).alpha = 0.5f // Dim others
                }
                v.alpha = 1.0f // Highlight selected
                selectedColorHex = v.tag.toString()
            }
            // Initialize: Dim all except default blue
            if (view.tag.toString() != selectedColorHex) {
                view.alpha = 0.5f
            } else {
                view.alpha = 1.0f
            }
        }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser ?: return
        
        // Try to load existing CV data first
        db.collection("users").document(currentUser.uid).collection("cv_data").document("primary_cv")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.toObject(CvData::class.java)
                    if (data != null) {
                        etName.setText(data.name)
                        etEmail.setText(data.email)
                        etPhone.setText(data.phone)
                        etUniversity.setText(data.university)
                        etDegree.setText(data.degree)
                        etYear.setText(data.year)
                        etExperience.setText(data.experience)
                        etSkills.setText(data.skills)
                        
                        // Only overwrite template if not explicitly selected from menu
                        if (intent.getIntExtra("TEMPLATE_ID", -1) == -1) {
                            selectedTemplateId = data.templateId
                        }
                        
                        selectedColorHex = data.colorHex
                        // Update color picker selection UI
                        updateColorPickerUI(data.colorHex)
                    }
                } else {
                    // Fallback to Profile Data if no CV specific data found
                    db.collection("users").document(currentUser.uid).get()
                        .addOnSuccessListener { userDoc ->
                            if (userDoc != null && userDoc.exists()) {
                                etName.setText(userDoc.getString("name"))
                                etEmail.setText(userDoc.getString("email"))
                            }
                        }
                }
            }
    }

    private fun updateColorPickerUI(hex: String) {
        for (i in 0 until layoutColorPicker.childCount) {
            val view = layoutColorPicker.getChildAt(i)
            if (view.tag.toString() == hex) {
                view.alpha = 1.0f
            } else {
                view.alpha = 0.5f
            }
        }
    }

    private fun saveCvData() {
        val currentUser = auth.currentUser ?: return
        
        val cvData = CvData(
            id = "primary_cv",
            userId = currentUser.uid,
            templateId = selectedTemplateId,
            colorHex = selectedColorHex,
            name = etName.text.toString(),
            email = etEmail.text.toString(),
            phone = etPhone.text.toString(),
            university = etUniversity.text.toString(),
            degree = etDegree.text.toString(),
            year = etYear.text.toString(),
            experience = etExperience.text.toString(),
            skills = etSkills.text.toString()
        )

        db.collection("users").document(currentUser.uid).collection("cv_data").document("primary_cv")
            .set(cvData)
            .addOnSuccessListener {
                Toast.makeText(this, "CV Saved to Profile", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save to Profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generatePdf() {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 dimensions
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        val themeColor = Color.parseColor(selectedColorHex)

        when (selectedTemplateId) {
            1 -> drawModernTemplate(canvas, paint, themeColor)
            2 -> drawProfessionalTemplate(canvas, paint, themeColor)
            3 -> drawCreativeTemplate(canvas, paint, themeColor)
            4 -> drawMinimalTemplate(canvas, paint, themeColor)
            else -> drawModernTemplate(canvas, paint, themeColor)
        }

        pdfDocument.finishPage(page)

        // Save PDF
        savePdfToStorage(pdfDocument, etName.text.toString())
    }

    private fun drawModernTemplate(canvas: Canvas, paint: Paint, themeColor: Int) {
        // Modern: Left colored sidebar for contact/skills, content on right
        
        // Sidebar
        paint.color = themeColor
        canvas.drawRect(0f, 0f, 180f, 842f, paint)

        // Content
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val phone = etPhone.text.toString()
        val skills = etSkills.text.toString()
        
        // Sidebar Text - White
        paint.color = Color.WHITE
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("CONTACT", 20f, 100f, paint)
        
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f
        canvas.drawText(email, 20f, 130f, paint)
        canvas.drawText(phone, 20f, 150f, paint)
        
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 14f
        canvas.drawText("SKILLS", 20f, 220f, paint)
        
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f
        val skillList = skills.split(",")
        var skillY = 250f
        for (skill in skillList) {
            canvas.drawText("• ${skill.trim()}", 20f, skillY, paint)
            skillY += 20f
        }

        // Right Side - Main Content
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 30f
        canvas.drawText(name.uppercase(), 220f, 80f, paint)
        
        paint.color = Color.DKGRAY
        paint.textSize = 16f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(etDegree.text.toString(), 220f, 110f, paint)

        // Line
        paint.color = Color.LTGRAY
        paint.strokeWidth = 2f
        canvas.drawLine(220f, 130f, 550f, 130f, paint)

        // Experience
        var yPos = 180f
        paint.color = themeColor
        paint.textSize = 18f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("WORK EXPERIENCE", 220f, yPos, paint)
        
        yPos += 30f
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        
        val expLines = etExperience.text.toString().split("\n")
        for (line in expLines) {
            if (line.isNotEmpty()) {
                // Initial word wrap logic (very basic)
                if (line.length > 50) {
                   canvas.drawText(line.substring(0, 50) + "-", 220f, yPos, paint)
                   yPos += 20f
                   canvas.drawText(line.substring(50), 220f, yPos, paint)
                } else {
                   canvas.drawText(line, 220f, yPos, paint)
                }
                yPos += 20f
            }
        }
        
        // Education
        yPos += 40f
        paint.color = themeColor
        paint.textSize = 18f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("EDUCATION", 220f, yPos, paint)
        
        yPos += 30f
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(etUniversity.text.toString(), 220f, yPos, paint)
        
        yPos += 20f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("${etDegree.text} - ${etYear.text}", 220f, yPos, paint)
    }

    private fun drawProfessionalTemplate(canvas: Canvas, paint: Paint, themeColor: Int) {
        // Professional: Top header block with color, centered text
        
        // Top Header
        paint.color = themeColor
        canvas.drawRect(0f, 0f, 595f, 150f, paint)
        
        // Name and Title
        paint.color = Color.WHITE
        paint.textSize = 32f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(etName.text.toString(), 297.5f, 70f, paint)
        
        paint.textSize = 18f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(etDegree.text.toString(), 297.5f, 100f, paint)
        
        paint.textSize = 12f
        canvas.drawText("${etEmail.text} | ${etPhone.text}", 297.5f, 130f, paint)
        
        // Reset Alignment
        paint.textAlign = Paint.Align.LEFT
        
        // Sections
        var yPos = 200f
        
        // Experience
        paint.color = themeColor
        paint.textSize = 18f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("PROFESSIONAL EXPERIENCE", 50f, yPos, paint)
        
        // Line
        paint.strokeWidth = 1f
        canvas.drawLine(50f, yPos + 10f, 545f, yPos + 10f, paint)
        
        yPos += 40f
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        
        val expLines = etExperience.text.toString().split("\n")
        for (line in expLines) {
             canvas.drawText("• $line", 50f, yPos, paint)
             yPos += 25f
        }
        
        // Education
        yPos += 30f
        paint.color = themeColor
        paint.textSize = 18f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("EDUCATION", 50f, yPos, paint)
        canvas.drawLine(50f, yPos + 10f, 545f, yPos + 10f, paint)
        
        yPos += 40f
        paint.color = Color.BLACK
        paint.textSize = 16f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(etUniversity.text.toString(), 50f, yPos, paint)
        
        yPos += 20f
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("${etDegree.text} (${etYear.text})", 50f, yPos, paint)

        // Skills
        yPos += 50f
        paint.color = themeColor
        paint.textSize = 18f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("SKILLS", 50f, yPos, paint)
        canvas.drawLine(50f, yPos + 10f, 545f, yPos + 10f, paint)
        
        yPos += 40f
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(etSkills.text.toString(), 50f, yPos, paint)
    }

    private fun drawCreativeTemplate(canvas: Canvas, paint: Paint, themeColor: Int) {
        // Creative: Large Name, split layout but cleaner, big circles for bullets?
        // Let's go with a split top/bottom. 
        // Top left name big, top right contact. Bottom columns. or something distinctive.
        
        // Background for Name
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, 595f, 842f, paint)
        
        // Big Left Header Color Bar
        paint.color = themeColor
        canvas.drawRect(30f, 30f, 30f + 10f, 812f, paint)
        
        var xStart = 60f
        
        // Name
        paint.color = themeColor
        paint.textSize = 40f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText(etName.text.toString(), xStart, 80f, paint)
        
        paint.color = Color.DKGRAY
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText(etDegree.text.toString(), xStart, 110f, paint)
        
        // Contact (Right aligned-ish or just below)
        paint.textSize = 12f
        paint.color = Color.GRAY
        canvas.drawText(etEmail.text.toString(), xStart, 140f, paint)
        canvas.drawText(etPhone.text.toString(), xStart, 160f, paint)
        
        var yPos = 220f
        
        // Section Headers are colored bubbles
        fun drawSectionHeader(title: String, y: Float) {
            paint.color = themeColor
            val width = paint.measureText(title) + 40
            // Pill shape
            canvas.drawRoundRect(xStart, y - 25, xStart + 200, y + 10, 20f, 20f, paint)
            
            paint.color = Color.WHITE
            paint.textSize = 16f
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText(title, xStart + 20, y, paint)
        }

        drawSectionHeader("EXPERIENCE", yPos)
        yPos += 40f
        
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        
         val expLines = etExperience.text.toString().split("\n")
        for (line in expLines) {
            if (line.isNotEmpty()) {
                canvas.drawText(line, xStart, yPos, paint)
                yPos += 20f
            }
        }
        
        yPos += 40f
        drawSectionHeader("EDUCATION", yPos)
        yPos += 40f
        
        paint.color = Color.BLACK
        paint.textSize = 16f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(etUniversity.text.toString(), xStart, yPos, paint)
        
        yPos += 25f
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Graduated: ${etYear.text}", xStart, yPos, paint)
        
        yPos += 40f
        drawSectionHeader("SKILLS", yPos)
        yPos += 40f
        
        paint.color = Color.BLACK
        paint.textSize = 14f
        canvas.drawText(etSkills.text.toString(), xStart, yPos, paint)
    }

    private fun drawMinimalTemplate(canvas: Canvas, paint: Paint, themeColor: Int) {
        // Minimal: Monochrome mostly, single color accent on name or lines. Very clean.
        
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, 595f, 842f, paint)
        
        // Name Centered Small Caps
        paint.color = Color.BLACK // Name is black in minimal usually
        paint.textSize = 36f
        paint.typeface = Typeface.SERIF
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(etName.text.toString().uppercase(), 297.5f, 100f, paint)
        
        // Divider line colored
        paint.color = themeColor
        paint.strokeWidth = 3f
        canvas.drawLine(250f, 120f, 345f, 120f, paint)
        
        // Contact
        paint.color = Color.DKGRAY
        paint.textSize = 12f
        paint.typeface = Typeface.SANS_SERIF
        canvas.drawText("${etEmail.text}  •  ${etPhone.text}", 297.5f, 150f, paint)
        
        paint.textAlign = Paint.Align.LEFT
        
        var yPos = 220f
        val leftMargin = 80f
        
        // Section Title function
        fun drawSection(title: String) {
            paint.color = themeColor
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 14f
            paint.letterSpacing = 0.2f
            canvas.drawText(title.uppercase(), leftMargin, yPos, paint)
            paint.letterSpacing = 0f
            yPos += 30f
        }
        
        drawSection("Experience")
        paint.color = Color.BLACK
        paint.typeface = Typeface.SERIF
        paint.textSize = 14f
        
        val expLines = etExperience.text.toString().split("\n")
        for (line in expLines) {
            canvas.drawText(line, leftMargin, yPos, paint)
            yPos += 20f
        }
        
        yPos += 40f
        drawSection("Education")
        paint.color = Color.BLACK
        paint.typeface = Typeface.SERIF // Serif for minimal elegance
        paint.textSize = 16f
        canvas.drawText(etUniversity.text.toString(), leftMargin, yPos, paint)
        yPos += 20f
        paint.textSize = 14f
        canvas.drawText("${etDegree.text}, ${etYear.text}", leftMargin, yPos, paint)
        
        yPos += 40f
        drawSection("Skills")
        paint.color = Color.BLACK
        paint.typeface = Typeface.SERIF
        canvas.drawText(etSkills.text.toString(), leftMargin, yPos, paint)
    }

    private fun savePdfToStorage(pdfDocument: PdfDocument, userName: String) {
        val fileName = "CV_${userName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    Toast.makeText(this, "CV saved to Downloads", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Save not supported on this Android version in demo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving CV: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}
