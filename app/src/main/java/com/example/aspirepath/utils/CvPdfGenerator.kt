package com.example.aspirepath.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.aspirepath.model.CvData
import java.io.IOException

object CvPdfGenerator {

    fun generateAndSavePdf(context: Context, data: CvData) {
        val pdfDocument = PdfDocument()
        // Standard A4 size in points (approx 595 x 842)
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() 
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var themeColor = Color.BLACK
        try {
            themeColor = Color.parseColor(data.colorHex)
        } catch (e: Exception) {
            themeColor = Color.BLUE // Default fallback
        }

        when (data.templateId) {
            1 -> drawModernTemplate(canvas, paint, themeColor, data)
            2 -> drawProfessionalTemplate(canvas, paint, themeColor, data)
            3 -> drawCreativeTemplate(canvas, paint, themeColor, data)
            4 -> drawMinimalTemplate(canvas, paint, themeColor, data)
            else -> drawModernTemplate(canvas, paint, themeColor, data)
        }

        pdfDocument.finishPage(page)

        // Save PDF
        savePdfToStorage(context, pdfDocument, data.name)
    }

    private fun drawModernTemplate(canvas: Canvas, paint: Paint, themeColor: Int, data: CvData) {
        // Modern: Left colored sidebar for contact/skills, content on right
        
        // Sidebar
        paint.color = themeColor
        canvas.drawRect(0f, 0f, 180f, 842f, paint)

        // Content
        val name = data.name ?: ""
        val email = data.email ?: ""
        val phone = data.phone ?: ""
        val skills = data.skills ?: ""
        
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
        canvas.drawText(data.degree ?: "", 220f, 110f, paint)

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
        
        val expLines = (data.experience ?: "").split("\n")
        for (line in expLines) {
            if (line.isNotEmpty()) {
                // Initial word wrap logic (very basic)
                if (line.length > 50) {
                   canvas.drawText(line.substring(0, 50) + "-", 220f, yPos, paint)
                   yPos += 20f
                   if (line.length > 50) {
                        try {
                            canvas.drawText(line.substring(50), 220f, yPos, paint)
                        } catch(e: Exception) {}
                   }
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
        canvas.drawText(data.university ?: "", 220f, yPos, paint)
        
        yPos += 20f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("${data.degree ?: ""} - ${data.year ?: ""}", 220f, yPos, paint)
    }

    private fun drawProfessionalTemplate(canvas: Canvas, paint: Paint, themeColor: Int, data: CvData) {
        // Professional: Top header block with color, centered text
        
        // Top Header
        paint.color = themeColor
        canvas.drawRect(0f, 0f, 595f, 150f, paint)
        
        // Name and Title
        paint.color = Color.WHITE
        paint.textSize = 32f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText((data.name ?: "").toString(), 297.5f, 70f, paint)
        
        paint.textSize = 18f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText((data.degree ?: "").toString(), 297.5f, 100f, paint)
        
        paint.textSize = 12f
        canvas.drawText("${data.email ?: ""} | ${data.phone ?: ""}", 297.5f, 130f, paint)
        
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
        
        val expLines = (data.experience ?: "").split("\n")
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
        canvas.drawText(data.university ?: "", 50f, yPos, paint)
        
        yPos += 20f
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("${data.degree ?: ""} (${data.year ?: ""})", 50f, yPos, paint)

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
        canvas.drawText(data.skills ?: "", 50f, yPos, paint)
    }

    private fun drawCreativeTemplate(canvas: Canvas, paint: Paint, themeColor: Int, data: CvData) {
        // Creative: Large Name, split layout but cleaner, big circles for bullets?
        
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
        canvas.drawText((data.name ?: "").toString(), xStart, 80f, paint)
        
        paint.color = Color.DKGRAY
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText((data.degree ?: "").toString(), xStart, 110f, paint)
        
        // Contact (Right aligned-ish or just below)
        paint.textSize = 12f
        paint.color = Color.GRAY
        canvas.drawText((data.email ?: "").toString(), xStart, 140f, paint)
        canvas.drawText((data.phone ?: "").toString(), xStart, 160f, paint)
        
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
        
        val expLines = (data.experience ?: "").split("\n")
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
        canvas.drawText(data.university ?: "", xStart, yPos, paint)
        
        yPos += 25f
        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Graduated: ${data.year ?: ""}", xStart, yPos, paint)
        
        yPos += 40f
        drawSectionHeader("SKILLS", yPos)
        yPos += 40f
        
        paint.color = Color.BLACK
        paint.textSize = 14f
        canvas.drawText(data.skills ?: "", xStart, yPos, paint)
    }

    private fun drawMinimalTemplate(canvas: Canvas, paint: Paint, themeColor: Int, data: CvData) {
        // Minimal: Monochrome mostly, single color accent on name or lines. Very clean.
        
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, 595f, 842f, paint)
        
        // Name Centered Small Caps
        paint.color = Color.BLACK // Name is black in minimal usually
        paint.textSize = 36f
        paint.typeface = Typeface.SERIF
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText((data.name ?: "").uppercase(), 297.5f, 100f, paint)
        
        // Divider line colored
        paint.color = themeColor
        paint.strokeWidth = 3f
        canvas.drawLine(250f, 120f, 345f, 120f, paint)
        
        // Contact
        paint.color = Color.DKGRAY
        paint.textSize = 12f
        paint.typeface = Typeface.SANS_SERIF
        canvas.drawText("${data.email ?: ""}  •  ${data.phone ?: ""}", 297.5f, 150f, paint)
        
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
        
        val expLines = (data.experience ?: "").split("\n")
        for (line in expLines) {
            canvas.drawText(line, leftMargin, yPos, paint)
            yPos += 20f
        }
        
        yPos += 40f
        drawSection("Education")
        paint.color = Color.BLACK
        paint.typeface = Typeface.SERIF // Serif for minimal elegance
        paint.textSize = 16f
        canvas.drawText(data.university ?: "", leftMargin, yPos, paint)
        yPos += 20f
        paint.textSize = 14f
        canvas.drawText("${data.degree ?: ""}, ${data.year ?: ""}", leftMargin, yPos, paint)
        
        yPos += 40f
        drawSection("Skills")
        paint.color = Color.BLACK
        paint.typeface = Typeface.SERIF
        canvas.drawText(data.skills ?: "", leftMargin, yPos, paint)
    }

    private fun savePdfToStorage(context: Context, pdfDocument: PdfDocument, userName: String?) {
        val safeName = (userName ?: "User").replace(" ", "_")
        val fileName = "CV_${safeName}_${System.currentTimeMillis()}.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    Toast.makeText(context, "CV saved to Downloads", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Save not supported on this Android version in demo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving CV: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}
