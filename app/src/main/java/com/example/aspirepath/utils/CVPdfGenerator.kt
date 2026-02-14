package com.example.aspirepath.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.aspirepath.models.CVData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CVPdfGenerator(private val context: Context) {

    private val A4_WIDTH = 595
    private val A4_HEIGHT = 842
    private val SIDEBAR_WIDTH = 200f
    private val CONTENT_WIDTH_2COL = A4_WIDTH - SIDEBAR_WIDTH
    private val MARGIN = 20f

    fun generatePDF(cvData: CVData, templateId: String): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        if (templateId == "modern") {
            // "Professional" template (ID: modern) -> Sarah James Design
            drawProfessionalTemplate(canvas, cvData)
        } else if (templateId == "professional") {
             // "Modern" template (ID: professional) -> "YOUR NAME" / Timeline design
            drawModernTemplate(canvas, cvData)
        } else if (templateId == "academic") {
            drawAcademicTemplate(canvas, cvData)
        } else if (templateId == "simple") {
            drawSimpleTemplate(canvas, cvData)
        } else if (templateId == "creative") {
            drawCreativeTemplate(canvas, cvData)
        } else {
            // Fallback
             drawProfessionalTemplate(canvas, cvData) 
        }

        pdfDocument.finishPage(page)

        // Write the document content
        val fileName = "Resume_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }

        pdfDocument.close()
        return file
    }

    private fun drawCreativeTemplate(canvas: Canvas, data: CVData) {
        // Colors from image (Beige, Muted Green, Soft Brown) - Darkened for visibility
        val beige = Color.parseColor("#F5F1E6")
        val mutedGreen = Color.parseColor("#7D8E58") // Darker Olive
        val softBrown = Color.parseColor("#8B5E3C") // Darker Brown
        val darkGrey = Color.parseColor("#222222") // Almost Black
        val lightGrey = Color.parseColor("#555555") // Mid-Dark Grey
        val white = Color.WHITE

        // Paints
        val namePaint = TextPaint().apply {
            color = darkGrey
            textSize = 32f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val jobTitlePaint = TextPaint().apply {
            color = lightGrey
            textSize = 16f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val sectionHeaderPaint = TextPaint().apply {
            color = softBrown
            textSize = 14f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.1f // Uppercase tracking
        }

        val bodyPaint = TextPaint().apply {
            color = darkGrey
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val boldBodyPaint = TextPaint(bodyPaint).apply {
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        // --- BACKGROUND SHAPES ---
        // Top Left Curve (Vertical Sidebar-ish)
        val path1 = android.graphics.Path()
        path1.moveTo(0f, 0f)
        path1.lineTo(160f, 0f)
        path1.cubicTo(160f, 100f, 100f, 150f, 100f, 300f) // S-curve
        path1.lineTo(100f, A4_HEIGHT.toFloat())
        path1.lineTo(0f, A4_HEIGHT.toFloat())
        path1.close()
        canvas.drawPath(path1, Paint().apply { color = beige; isAntiAlias = true })

        // Top Right organic bubble
        val path2 = android.graphics.Path()
        path2.moveTo(A4_WIDTH.toFloat(), 0f)
        path2.lineTo(A4_WIDTH - 200f, 0f)
        path2.cubicTo(A4_WIDTH - 200f, 100f, A4_WIDTH - 50f, 150f, A4_WIDTH.toFloat(), 200f)
        path2.close()
        canvas.drawPath(path2, Paint().apply { color = softBrown; alpha = 50; isAntiAlias = true }) // Transparent tan

        // Bottom Left organic bubble
        val path3 = android.graphics.Path()
        path3.moveTo(0f, A4_HEIGHT.toFloat())
        path3.lineTo(0f, A4_HEIGHT - 200f)
        path3.cubicTo(50f, A4_HEIGHT - 200f, 150f, A4_HEIGHT - 100f, 200f, A4_HEIGHT.toFloat())
        path3.close()
        canvas.drawPath(path3, Paint().apply { color = mutedGreen; alpha = 100; isAntiAlias = true })

        // --- HEADER CONTENT ---
        // Photo Circle
        val photoX = 100f // Centered on sidebar edge roughly
        val photoY = 100f
        val photoRadius = 50f
        
        // Draw Photo Ring
        canvas.drawCircle(photoX, photoY, photoRadius + 4f, Paint().apply { color = white; isAntiAlias = true })
        canvas.drawCircle(photoX, photoY, photoRadius + 2f, Paint().apply { color = softBrown; style = Paint.Style.STROKE; strokeWidth = 2f; isAntiAlias = true })

        // Photo
        if (data.personalInfo.photoUri.isNotEmpty()) {
             try {
                val uri = Uri.parse(data.personalInfo.photoUri)
                var bitmap: Bitmap? = null
                 if (uri.scheme == "file") {
                    bitmap = BitmapFactory.decodeFile(uri.path)
                } else {
                     val inputStream = context.contentResolver.openInputStream(uri)
                     bitmap = BitmapFactory.decodeStream(inputStream)
                     inputStream?.close()
                }
                if (bitmap != null) {
                    val circleBitmap = getCircularBitmap(bitmap)
                    val photoRect = android.graphics.RectF(photoX - photoRadius, photoY - photoRadius, photoX + photoRadius, photoY + photoRadius)
                    canvas.drawBitmap(circleBitmap, null, photoRect, Paint().apply { isAntiAlias = true })
                } else {
                    canvas.drawCircle(photoX, photoY, photoRadius, Paint().apply { color = Color.LTGRAY })
                }
             } catch (e: Exception) {
                 canvas.drawCircle(photoX, photoY, photoRadius, Paint().apply { color = Color.LTGRAY })
             }
        } else {
             canvas.drawCircle(photoX, photoY, photoRadius, Paint().apply { color = Color.LTGRAY })
        }

        // Name & Title
        val headerTextX = 180f
        canvas.drawText(data.personalInfo.fullName.uppercase(), headerTextX, 80f, namePaint)
        
        // Job Title (Get from experience or summary)
        val jobTitle = data.workExperience.firstOrNull()?.jobTitle ?: "Professional"
        canvas.drawText(jobTitle, headerTextX, 110f, jobTitlePaint)

        // Contact Bar
        val contactY = 160f
        val contactBarRect = android.graphics.RectF(headerTextX, contactY, A4_WIDTH - 20f, contactY + 30f)
        canvas.drawRoundRect(contactBarRect, 15f, 15f, Paint().apply { color = Color.parseColor("#F0E6D2"); isAntiAlias = true }) // Light beige bar
        
        // Contact Text
        val contactPaint = TextPaint().apply { color = darkGrey; textSize = 10f; isAntiAlias = true }
        var currX = headerTextX + 20f
        val iconY = contactY + 20f
        
        if (data.personalInfo.phoneNumber.isNotEmpty()) {
            canvas.drawText("\u260E ${data.personalInfo.phoneNumber}", currX, iconY - 5f, contactPaint)
            currX += 130f
        }
        if (data.personalInfo.email.isNotEmpty()) {
            canvas.drawText("\u2709 ${data.personalInfo.email}", currX, iconY - 5f, contactPaint)
            currX += 180f
        }
        if (data.personalInfo.address.isNotEmpty()) {
             // Just City/Country? Taking full address for now
             val addressShort = if(data.personalInfo.address.length > 20) data.personalInfo.address.substring(0, 20) + "..." else data.personalInfo.address
             canvas.drawText("\u2302 $addressShort", currX, iconY - 5f, contactPaint)
        }

        var y = 220f
        val mainX = 180f // Start of main column
        val mainWidth = A4_WIDTH - mainX - 20f

        // Helper for decorative section header
        fun drawCreativeHeader(title: String, currentY: Float): Float {
            canvas.drawText(title.uppercase(), mainX, currentY, sectionHeaderPaint)
            canvas.drawLine(mainX + 100f, currentY - 5f, A4_WIDTH - 20f, currentY - 5f, Paint().apply { color = softBrown; strokeWidth = 1f })
            return currentY + 20f
        }

        // --- PROFESSIONAL SUMMARY ---
        if (data.professionalSummary.isNotEmpty()) {
            y = drawCreativeHeader("PROFESSIONAL SUMMARY", y)
            y = drawMultilineText(canvas, data.professionalSummary, mainX, y, mainWidth.toInt(), bodyPaint)
            y += 20f
        }

        // --- EXPERIENCE ---
        if (data.workExperience.isNotEmpty()) {
            y = drawCreativeHeader("EXPERIENCE", y)
            
            // Timeline line
            val timelineX = mainX + 5f
            val timelineTopY = y
            
            for (exp in data.workExperience) {
                 val itemY = y
                 
                 // Dot
                 canvas.drawCircle(timelineX, itemY - 4f, 4f, Paint().apply { color = softBrown; style = Paint.Style.FILL })
                 // Ring around dot
                 canvas.drawCircle(timelineX, itemY - 4f, 6f, Paint().apply { color = softBrown; style = Paint.Style.STROKE; strokeWidth = 1f })
                 
                 // Job Title
                 canvas.drawText(exp.jobTitle, timelineX + 20f, itemY, boldBodyPaint)
                 y += 15f
                 
                 // Company | Location | Date
                 val subText = "${exp.companyName} | ${exp.startDate} - ${if(exp.isCurrent) "Present" else exp.endDate}"
                 canvas.drawText(subText, timelineX + 20f, y, bodyPaint) // Muted color?
                 y += 20f
                 
                 // Responsibilities
                 if (exp.responsibilities.isNotEmpty()) {
                     y = drawMultilineText(canvas, exp.responsibilities, timelineX + 20f, y, (mainWidth - 20).toInt(), bodyPaint)
                 }
                 y += 20f
            }
            
            // Draw line connecting dots
            canvas.drawLine(timelineX, timelineTopY, timelineX, y - 20f, Paint().apply { color = softBrown; strokeWidth = 1f })
            y += 10f
        }

        // --- SKILLS ---
        if (data.skills.isNotEmpty()) {
             y = drawCreativeHeader("SKILLS", y)
             
             // Technical Skills (Assuming generic list)
             // Display as bullet list
             for (skill in data.skills) {
                 canvas.drawCircle(mainX + 5f, y - 4f, 2f, Paint().apply { color = darkGrey })
                 canvas.drawText(skill.name, mainX + 15f, y, bodyPaint)
                 y += 15f
             }
             y += 20f
        }

        // --- EDUCATION ---
        if (data.education.isNotEmpty()) {
            y = drawCreativeHeader("EDUCATION", y)
            
            val timelineX = mainX + 5f
            val timelineTopY = y
            
            for (edu in data.education) {
                val itemY = y
                canvas.drawCircle(timelineX, itemY - 4f, 4f, Paint().apply { color = softBrown })
                
                canvas.drawText(edu.degree, timelineX + 20f, itemY, boldBodyPaint)
                y += 15f
                
                val subText = "${edu.institution} | ${edu.yearOfCompletion}"
                canvas.drawText(subText, timelineX + 20f, y, bodyPaint)
                y += 20f
            }
             canvas.drawLine(timelineX, timelineTopY, timelineX, y - 20f, Paint().apply { color = softBrown; strokeWidth = 1f })
        }
        
        // --- HOBBIES ---
        if (data.hobbies.isNotEmpty()) {
            y += 10f
            y = drawCreativeHeader("HOBBIES / INTERESTS", y)
             for (hobby in data.hobbies) {
                 canvas.drawCircle(mainX + 5f, y - 4f, 2f, Paint().apply { color = darkGrey })
                 canvas.drawText(hobby, mainX + 15f, y, bodyPaint)
                 y += 15f
             }
        }
    }

    private fun drawSimpleTemplate(canvas: Canvas, data: CVData) {
        val black = Color.BLACK
        val darkGray = Color.DKGRAY
        val lightGray = Color.parseColor("#E0E0E0") 
        
        val serifFont = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        val serifBold = Typeface.create(Typeface.SERIF, Typeface.BOLD)

        val headerPaint = TextPaint().apply {
            color = black
            textSize = 20f 
            typeface = serifBold
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val namePaint = TextPaint().apply {
            color = black
            textSize = 14f
            typeface = serifBold
            isAntiAlias = true
        }

        val bodyPaint = TextPaint().apply {
            color = black
            textSize = 12f 
            typeface = serifFont
            isAntiAlias = true
        }

        val boldBodyPaint = TextPaint(bodyPaint).apply {
            typeface = serifBold
        }
        
        val sectionHeaderPaint = TextPaint().apply {
            color = black
            textSize = 14f
            typeface = serifBold
            isAntiAlias = true
        }
        
        // Page Border
        val borderPaint = Paint().apply {
            color = black
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        val pageRect = android.graphics.RectF(MARGIN, MARGIN, A4_WIDTH - MARGIN, A4_HEIGHT - MARGIN)
        canvas.drawRect(pageRect, borderPaint)

        var y = MARGIN + 30f
        val leftContentX = MARGIN + 20f
        val rightContentX = A4_WIDTH - MARGIN - 20f
        val centerX = A4_WIDTH / 2f

        // --- TITLE HEADER: RESUME ---
        val titleText = "RESUME"
        val titleRectWidth = 400f
        val titleRectHeight = 30f
        val titleRectLeft = centerX - (titleRectWidth / 2)
        val titleRectTop = y
        val titleRectBottom = y + titleRectHeight
        
        val headerBgPaint = Paint().apply { color = lightGray; style = Paint.Style.FILL }
        val headerBorderPaint = Paint().apply { color = black; style = Paint.Style.STROKE; strokeWidth = 1f }
        
        canvas.drawRect(titleRectLeft, titleRectTop, titleRectLeft + titleRectWidth, titleRectBottom, headerBgPaint)
        canvas.drawRect(titleRectLeft, titleRectTop, titleRectLeft + titleRectWidth, titleRectBottom, headerBorderPaint)
        
        val fontMetrics = headerPaint.fontMetrics
        val textHeight = fontMetrics.descent - fontMetrics.ascent
        val textOffset = (textHeight / 2) - fontMetrics.descent
        canvas.drawText(titleText, centerX, titleRectTop + (titleRectHeight / 2) + textOffset + 3f, headerPaint)
        
        y = titleRectBottom + 30f

        // --- PERSONAL INFO ---
        canvas.drawText(data.personalInfo.fullName.uppercase(), leftContentX, y, namePaint)
        y += 20f
        
        if (data.personalInfo.address.isNotEmpty()) {
            y = drawMultilineText(canvas, data.personalInfo.address, leftContentX, y, 250, bodyPaint)
            y += 5f
        }
        
        canvas.drawText("E-mail: ${data.personalInfo.email}", leftContentX, y, bodyPaint)
        y += 15f
        canvas.drawText("Mobile: ${data.personalInfo.phoneNumber}", leftContentX, y, bodyPaint)
        y += 30f

        fun drawSectionHeader(title: String, currentY: Float): Float {
             val barTop = currentY - 15f
             val barBottom = currentY + 5f
             canvas.drawRect(leftContentX, barTop, rightContentX, barBottom, headerBgPaint)
             
             val symbol = "\u2756" 
             val text = "$symbol  $title"
             canvas.drawText(text, leftContentX + 10f, currentY, sectionHeaderPaint)
             
             return barBottom + 15f
        }

        // --- OBJECTIVE ---
        if (data.professionalSummary.isNotEmpty()) {
            y = drawSectionHeader("Objective", y)
            y = drawMultilineText(canvas, data.professionalSummary, leftContentX + 10f, y, (rightContentX - leftContentX - 10).toInt(), bodyPaint)
            y += 20f
        }

        // --- ACADEMIC DETAILS ---
        if (data.education.isNotEmpty()) {
            y = drawSectionHeader("Academic Details", y)
            for (edu in data.education) {
                val bullet = "\u2022" 
                var text = edu.degree
                if (edu.institution.isNotEmpty()) text += " from ${edu.institution}"
                if (edu.yearOfCompletion.isNotEmpty()) text += " (${edu.yearOfCompletion})"
                
                val lineText = "$bullet  $text"
                y = drawMultilineText(canvas, lineText, leftContentX + 20f, y, (rightContentX - leftContentX - 20).toInt(), bodyPaint)
                y += 5f
            }
            y += 20f
        }

        // --- WORK EXPERIENCE ---
        if (data.workExperience.isNotEmpty()) {
            y = drawSectionHeader("Work Experience", y)
            for (exp in data.workExperience) {
                val labelX = leftContentX + 20f
                val valueX = leftContentX + 120f
                
                canvas.drawText("Organization", labelX, y, boldBodyPaint)
                canvas.drawText(":", labelX + 90f, y, boldBodyPaint)
                canvas.drawText(exp.companyName, valueX, y, bodyPaint)
                y += 15f
                
                canvas.drawText("Designation", labelX, y, boldBodyPaint)
                canvas.drawText(":", labelX + 90f, y, boldBodyPaint)
                canvas.drawText(exp.jobTitle, valueX, y, bodyPaint)
                y += 15f
                
                canvas.drawText("Duration", labelX, y, boldBodyPaint)
                canvas.drawText(":", labelX + 90f, y, boldBodyPaint)
                val duration = "${exp.startDate} - ${if(exp.isCurrent) "Present" else exp.endDate}"
                canvas.drawText(duration, valueX, y, bodyPaint)
                y += 15f
                
                if (exp.responsibilities.isNotEmpty()) {
                    canvas.drawText("Profile", labelX, y, boldBodyPaint)
                    canvas.drawText(":", labelX + 90f, y, boldBodyPaint)
                    y = drawMultilineText(canvas, exp.responsibilities, valueX, y, (rightContentX - valueX).toInt(), bodyPaint)
                } 
                y += 20f
            }
        }
        
        // --- PROJECTS ---
        if (data.projects.isNotEmpty()) {
            y = drawSectionHeader("Academic Project Undertaken", y)
            for (proj in data.projects) {
                val labelX = leftContentX + 20f
                val valueX = leftContentX + 120f
                
                canvas.drawText("Project Title", labelX, y, boldBodyPaint)
                canvas.drawText(":", labelX + 90f, y, boldBodyPaint)
                canvas.drawText(proj.title, valueX, y, bodyPaint)
                y += 15f
                
                if (proj.description.isNotEmpty()) {
                    canvas.drawText("Profile", labelX, y, boldBodyPaint)
                    canvas.drawText(":", labelX + 90f, y, boldBodyPaint)
                    y = drawMultilineText(canvas, proj.description, valueX, y, (rightContentX - valueX).toInt(), bodyPaint)
                }
                 y += 20f
            }
        }
        
        canvas.drawText("1", centerX, A4_HEIGHT - MARGIN - 10f, bodyPaint)
    }

        
    private fun drawModernTemplate(canvas: Canvas, data: CVData) {
        // Colors
        val darkGrey = Color.parseColor("#333333")
        val lightGrey = Color.parseColor("#F5F5F5")
        val white = Color.WHITE
        
        // Paints
        val headerNamePaint = TextPaint().apply {
            color = darkGrey
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val headerTitlePaint = TextPaint().apply {
            color = darkGrey
            textSize = 18f
            isAntiAlias = true
        }
        val contactBarPaint = Paint().apply { color = darkGrey }
        val contactTextPaint = TextPaint().apply {
            color = white
            textSize = 10f
            isAntiAlias = true
        }
        val sectionHeaderPaint = TextPaint().apply {
            color = darkGrey
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.05f
        }
        val bodyPaint = TextPaint().apply {
            color = Color.BLACK
            textSize = 10f
            isAntiAlias = true
        }
        val boldBodyPaint = TextPaint(bodyPaint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        
        // Layout Config
        val leftColumnWidth = 140f // Width for "Year-Year" / Headers
        val rightColumnX = leftColumnWidth + 20f
        val rightColumnWidth = A4_WIDTH - rightColumnX - MARGIN
        
        // --- HEADER ---
        var y = 40f
        
        // Photo Box (Left)
        val photoSize = 100f
        val photoRect = android.graphics.RectF(MARGIN, y, MARGIN + photoSize, y + photoSize)
        val photoBgPaint = Paint().apply { color = darkGrey }
        canvas.drawRect(photoRect, photoBgPaint)
        
        // Draw Photo if exists
        if (data.personalInfo.photoUri.isNotEmpty()) {
             try {
                val uri = Uri.parse(data.personalInfo.photoUri)
                var bitmap: Bitmap? = null
                 if (uri.scheme == "file") {
                    bitmap = BitmapFactory.decodeFile(uri.path)
                } else {
                     val inputStream = context.contentResolver.openInputStream(uri)
                     bitmap = BitmapFactory.decodeStream(inputStream)
                     inputStream?.close()
                }
                
                if (bitmap != null) {
                    val circleBitmap = getCircularBitmap(bitmap)
                     // Center in the box
                    val destRect = android.graphics.RectF(
                        photoRect.centerX() - 35f, 
                        photoRect.centerY() - 35f, 
                        photoRect.centerX() + 35f, 
                        photoRect.centerY() + 35f
                    )
                    canvas.drawBitmap(circleBitmap, null, destRect, Paint().apply { isAntiAlias = true })
                } else {
                     // White Circle placeholder
                     canvas.drawCircle(photoRect.centerX(), photoRect.centerY(), 35f, Paint().apply { color = white })
                }
             } catch (e: Exception) {
                 canvas.drawCircle(photoRect.centerX(), photoRect.centerY(), 35f, Paint().apply { color = white })
             }
        } else {
             canvas.drawCircle(photoRect.centerX(), photoRect.centerY(), 35f, Paint().apply { color = white })
        }
        
        // Name & Title (Right of Photo)
        val nameX = MARGIN + photoSize + 20f
        canvas.drawText(data.personalInfo.fullName.uppercase(), nameX, y + 40f, headerNamePaint)
        
        // Job Title
        val jobTitle = data.workExperience.firstOrNull()?.jobTitle ?: "" 
        if (jobTitle.isNotEmpty()) {
             canvas.drawText(jobTitle, nameX, y + 70f, headerTitlePaint)
        }
        
        y += photoSize + 20f
        
        // Contact Bar
        val barHeight = 25f
        canvas.drawRect(nameX, y - 30f, A4_WIDTH - MARGIN, y - 30f + barHeight, contactBarPaint)
        
        // Contact Text inside Bar
        // Simple horizontal layout: Phone | Address | Email
        val contactY = y - 30f + 16f
        var currentContactX = nameX + 10f
        
        // Icon placeholders (using text for simplicity or small circle)
        // Phone
        if (data.personalInfo.phoneNumber.isNotEmpty()) {
             canvas.drawText("\u260E ${data.personalInfo.phoneNumber}", currentContactX, contactY, contactTextPaint)
             currentContactX += contactTextPaint.measureText("\u260E ${data.personalInfo.phoneNumber}") + 20f
        }
        // Address
        if (data.personalInfo.address.isNotEmpty()) {
             canvas.drawText("\u2302 ${data.personalInfo.address}", currentContactX, contactY, contactTextPaint)
             currentContactX += contactTextPaint.measureText("\u2302 ${data.personalInfo.address}") + 20f
        }
        // Email
        if (data.personalInfo.email.isNotEmpty()) {
             canvas.drawText("\u2709 ${data.personalInfo.email}", currentContactX, contactY, contactTextPaint)
        }

        y += 20f

        // --- BODY SECTIONS ---
        
        // OBJECTIVE
        if (data.professionalSummary.isNotEmpty()) {
            canvas.drawText("OBJECTIVE", MARGIN, y + 10f, sectionHeaderPaint)
            // Text on right column
            drawMultilineText(canvas, data.professionalSummary, rightColumnX, y, rightColumnWidth.toInt(), bodyPaint)
            val height = measureMultilineHeight(data.professionalSummary, rightColumnWidth.toInt(), bodyPaint)
            y += height + 20f
        }
        
        // EXPERIENCE (Timeline)
        if (data.workExperience.isNotEmpty()) {
            canvas.drawText("EXPERIENCE", MARGIN, y + 10f, sectionHeaderPaint)
            y += 25f // Add space below header
            
            val lineStartX = leftColumnWidth
            var lineStartY = y 
            
            for (i in data.workExperience.indices) {
                val exp = data.workExperience[i]
                val startY = y
                
                // Date (Left)
                val dateText = "${exp.startDate} - ${if(exp.isCurrent) "Present" else exp.endDate}"
                drawMultilineText(canvas, dateText, MARGIN, y + 15f, (leftColumnWidth - MARGIN - 10).toInt(), bodyPaint)
                
                // Content (Right)
                // Title
                canvas.drawText(exp.jobTitle, rightColumnX, y + 15f, boldBodyPaint)
                canvas.drawText(exp.companyName, rightColumnX, y + 30f, bodyPaint)
                var contentY = y + 45f
                
                if (exp.responsibilities.isNotEmpty()) {
                    contentY = drawMultilineText(canvas, exp.responsibilities, rightColumnX, contentY, rightColumnWidth.toInt(), bodyPaint)
                    contentY += 5f
                } else {
                    contentY += 10f
                }
                
                // Extend line to next item if not last
                val lineEndY = if (i < data.workExperience.size - 1) contentY + 15f else contentY
                
                // Draw Line Segment and Dot
                canvas.drawLine(lineStartX, lineStartY, lineStartX, lineEndY, Paint().apply { color = Color.GRAY; strokeWidth = 1f })
                canvas.drawRect(lineStartX - 2f, startY + 12f, lineStartX + 2f, startY + 16f, Paint().apply { color = darkGrey }) // Square dot
                
                y = contentY + 15f
                lineStartY = y
            }
            y += 15f
        }
        
        // EDUCATION (Timeline)
        if (data.education.isNotEmpty()) {
             canvas.drawText("EDUCATION", MARGIN, y + 10f, sectionHeaderPaint)
             y += 25f // Add space below header
             
             val lineStartX = leftColumnWidth
             var lineStartY = y
             
             for (i in data.education.indices) {
                 val edu = data.education[i]
                 val startY = y
                 
                 // Date (Left)
                 val dateText = edu.yearOfCompletion
                 canvas.drawText(dateText, MARGIN, y + 15f, bodyPaint)
                 
                 // Content (Right)
                 canvas.drawText(edu.institution, rightColumnX, y + 15f, boldBodyPaint)
                 canvas.drawText("${edu.degree} - ${edu.fieldOfStudy}", rightColumnX, y + 30f, bodyPaint)
                 var contentY = y + 45f
                 
                 // Extend line to next item if not last
                 val lineEndY = if (i < data.education.size - 1) contentY + 15f else contentY
                 
                 // Draw Line and Dot
                 canvas.drawLine(lineStartX, lineStartY, lineStartX, lineEndY, Paint().apply { color = Color.GRAY; strokeWidth = 1f })
                 canvas.drawRect(lineStartX - 2f, startY + 12f, lineStartX + 2f, startY + 16f, Paint().apply { color = darkGrey })
                 
                 y = contentY + 15f
                 lineStartY = y
             }
             y += 15f
        }
        
        // SKILLS
        if (data.skills.isNotEmpty()) {
             canvas.drawText("SKILLS", MARGIN, y + 10f, sectionHeaderPaint)
             
             // Grid or List on right
             // Image shows horizontal list: "• Skill 1   • Skill 2 ..."
             var currentX = rightColumnX
             var currentY = y + 10f
             
             for (skill in data.skills) {
                 val text = "\u2022 ${skill.name}"
                 val width = bodyPaint.measureText(text)
                 
                 if (currentX + width > A4_WIDTH - MARGIN) {
                     currentX = rightColumnX
                     currentY += 15f
                 }
                 
                 canvas.drawText(text, currentX, currentY, bodyPaint)
                 currentX += width + 20f
             }
             y = currentY + 30f
        }
        
        // HOBBIES
        if (data.hobbies.isNotEmpty()) {
             canvas.drawText("HOBBIES", MARGIN, y + 10f, sectionHeaderPaint)
             
             var currentX = rightColumnX
             var currentY = y + 10f
             
             for (hobby in data.hobbies) {
                 val text = "\u2022 $hobby"
                 val width = bodyPaint.measureText(text)
                 
                 if (currentX + width > A4_WIDTH - MARGIN) {
                     currentX = rightColumnX
                     currentY += 15f
                 }
                 
                 canvas.drawText(text, currentX, currentY, bodyPaint)
                 currentX += width + 20f
             }
        }

    }

    private fun drawAcademicTemplate(canvas: Canvas, data: CVData) {
        val darkRed = Color.parseColor("#A93226") // Deep Red/Brown
        val orange = Color.parseColor("#E67E22")   // Orange for top line
        val lightBlue = Color.parseColor("#AED6F1") // Light Blue for bottom line
        val black = Color.BLACK
        
        val serifFont = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        val serifBold = Typeface.create(Typeface.SERIF, Typeface.BOLD)

        val namePaint = TextPaint().apply {
            color = black
            textSize = 36f
            typeface = serifBold
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val contactInfoPaint = TextPaint().apply {
            color = black
            textSize = 12f
            typeface = serifFont
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        
        val sectionHeaderPaint = TextPaint().apply {
            color = darkRed
            textSize = 16f
            typeface = serifBold
            isAntiAlias = true
        }

        val bodyPaint = TextPaint().apply {
            color = black
            textSize = 12f
            typeface = serifFont
            isAntiAlias = true
        }

        val boldBodyPaint = TextPaint(bodyPaint).apply {
            typeface = serifBold
        }
        
        val labelPaint = TextPaint(boldBodyPaint) // For "Grade achieved:", "Main duties performed:"

        var y = 60f
        val centerX = A4_WIDTH / 2f
        val leftMargin = MARGIN + 20f
        val rightMargin = A4_WIDTH - MARGIN - 20f
        val contentWidth = (rightMargin - leftMargin).toInt()

        // --- HEADER ---
        canvas.drawText(data.personalInfo.fullName, centerX, y, namePaint)
        y += 30f
        
        if (data.personalInfo.address.isNotEmpty()) {
            canvas.drawText(data.personalInfo.address, centerX, y, contactInfoPaint)
            y += 20f
        }
        
        val contactLine = "Mobile: ${data.personalInfo.phoneNumber}   E-mail: ${data.personalInfo.email}"
        canvas.drawText(contactLine, centerX, y, contactInfoPaint)
        y += 40f

        // Helper to draw section lines
        fun drawSectionLines(currentY: Float): Float {
            var lineY = currentY + 5f
            // Thick Orange Line
            canvas.drawRect(leftMargin, lineY, rightMargin, lineY + 3f, Paint().apply { color = orange })
            lineY += 6f
            // Thin Blue Line
            canvas.drawRect(leftMargin, lineY, rightMargin, lineY + 1f, Paint().apply { color = lightBlue })
            return lineY + 20f
        }

        // --- PERSONAL PROFILE ---
        if (data.professionalSummary.isNotEmpty()) {
            canvas.drawText("Personal Profile", leftMargin, y, sectionHeaderPaint)
            y = drawSectionLines(y)
            y = drawMultilineText(canvas, data.professionalSummary, leftMargin, y, contentWidth, bodyPaint)
            y += 20f
        }

        // --- ACHIEVEMENTS ---
        if (data.awards.isNotEmpty()) {
            canvas.drawText("Achievements", leftMargin, y, sectionHeaderPaint)
            y = drawSectionLines(y)
            
            for (award in data.awards) {
                // > [insert achievement]
                // Using a simple arrow bullet
                val bullet = "> "
                val text = "$bullet${award.title}"
                y = drawMultilineText(canvas, text, leftMargin, y, contentWidth, bodyPaint)
            }
            y += 20f
        }

        // --- EDUCATION ---
        if (data.education.isNotEmpty()) {
            canvas.drawText("Education", leftMargin, y, sectionHeaderPaint)
            y = drawSectionLines(y)
            
            // Columns: [From-To] (Year) [Course] (Degree) [Institution Name]
            // Let's define column X positions
            val col1X = leftMargin
            val col2X = leftMargin + 100f
            val col3X = leftMargin + 300f
            
            for (edu in data.education) {
                val startY = y
                
                // Row 1
                canvas.drawText(edu.yearOfCompletion, col1X, y, boldBodyPaint)
                
                // Degree (Course) might wrap
                // Draw Degree at Col 2
                // We need to check width to avoid overlap with Col 3
                // Simplified: Just draw text
                canvas.drawText(edu.degree, col2X, y, boldBodyPaint)
                
                // Institution at Col 3
                canvas.drawText(edu.institution, col3X, y, bodyPaint) // Institution plain text? Image says "[Institution Name]"
                
                y += 20f
                
                // Row 2: Grade achieved: [Grade]
                if (edu.grade.isNotEmpty()) {
                    canvas.drawText("Grade achieved:", col1X, y, boldBodyPaint)
                    canvas.drawText(edu.grade, col2X, y, boldBodyPaint)
                    y += 20f
                }
                
                y += 10f // Space between entries
            }
            y += 10f
        }

        // --- WORK EXPERIENCE ---
        if (data.workExperience.isNotEmpty()) {
            canvas.drawText("Work Experience", leftMargin, y, sectionHeaderPaint)
            y = drawSectionLines(y)
            
            val col1X = leftMargin
            val col2X = leftMargin + 100f
            val col3X = leftMargin + 300f
            
            for (exp in data.workExperience) {
                val dates = "${exp.startDate} - ${if(exp.isCurrent) "Present" else exp.endDate}"
                
                // Row 1: Dates, Position, Company
                canvas.drawText(dates, col1X, y, boldBodyPaint)
                canvas.drawText(exp.jobTitle, col2X, y, boldBodyPaint)
                canvas.drawText(exp.companyName, col3X, y, boldBodyPaint) // Plain? Image: "[Company name]"
                y += 20f
                
                // Row 2: Main duties
                canvas.drawText("Main duties performed:", col1X, y, bodyPaint)
                y += 20f
                
                // Row 3: Description/Responsibility
                if (exp.responsibilities.isNotEmpty()) {
                     y = drawMultilineText(canvas, exp.responsibilities, col1X, y, contentWidth, bodyPaint)
                }
                
                y += 20f
            }
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = android.graphics.Rect(0, 0, bitmap.width, bitmap.height)
        
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, bitmap.width / 2f, paint)
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    private fun drawProfessionalTemplate(canvas: Canvas, data: CVData) {
        // Colors
        val sidebarColor = Color.parseColor("#2C3E50") // Dark Blue/Grey
        val nameColor = Color.BLACK
        val positionColor = Color.DKGRAY
        val sectionTitleColor = Color.BLACK
        val sidebarTextColor = Color.WHITE
        val mainTextColor = Color.DKGRAY

        // Paints
        val sidebarPaint = Paint().apply { color = sidebarColor }
        val namePaint = TextPaint().apply { 
            color = nameColor
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val positionPaint = TextPaint().apply {
            color = positionColor
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            letterSpacing = 0.1f
        }
        val sidebarHeaderPaint = TextPaint().apply {
            color = sidebarTextColor
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.1f
        }
        val sidebarTextPaint = TextPaint().apply {
            color = sidebarTextColor
            textSize = 10f
            isAntiAlias = true
        }
        val mainHeaderPaint = TextPaint().apply {
            color = sectionTitleColor
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            letterSpacing = 0.1f
        }
        val mainBodyPaint = TextPaint().apply {
            color = mainTextColor
            textSize = 10f
            isAntiAlias = true
        }
        val boldBodyPaint = TextPaint(mainBodyPaint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        
        // Draw Sidebar Background
        canvas.drawRect(0f, 0f, SIDEBAR_WIDTH, A4_HEIGHT.toFloat(), sidebarPaint)
        
        var sidebarY = 30f // Start Y for Sidebar
        var mainY = 30f    // Start Y for Main Content

        // --- SIDEBAR CONTENT ---
        
        // 1. Photo (if available) - Assuming top of sidebar as per some designs, or we can skip if no photo
        // The image shows photo isn't strictly top-left, but usually it is. 
        // Let's put regular contact info first as per image "CONTACT"
        
        // CONTACT
        drawSidebarHeader(canvas, "CONTACT", MARGIN, sidebarY, sidebarHeaderPaint)
        sidebarY += 20f
        
        // Divider
        canvas.drawLine(MARGIN, sidebarY, SIDEBAR_WIDTH - MARGIN, sidebarY, Paint().apply { color = Color.WHITE; strokeWidth = 1f })
        sidebarY += 15f

        // Contact Details
        sidebarY = drawSidebarText(canvas, data.personalInfo.email, MARGIN, sidebarY, sidebarTextPaint)
        sidebarY = drawSidebarText(canvas, data.personalInfo.phoneNumber, MARGIN, sidebarY, sidebarTextPaint)
        sidebarY = drawSidebarText(canvas, data.personalInfo.address, MARGIN, sidebarY, sidebarTextPaint)
        if (data.personalInfo.linkedInUrl.isNotEmpty()) {
            sidebarY = drawSidebarText(canvas, data.personalInfo.linkedInUrl, MARGIN, sidebarY, sidebarTextPaint)
        }
        sidebarY += 30f

        // SKILLS
        if (data.skills.isNotEmpty()) {
            drawSidebarHeader(canvas, "SKILLS", MARGIN, sidebarY, sidebarHeaderPaint)
            sidebarY += 20f
            canvas.drawLine(MARGIN, sidebarY, SIDEBAR_WIDTH - MARGIN, sidebarY, Paint().apply { color = Color.WHITE; strokeWidth = 1f })
            sidebarY += 15f
            
            for (skill in data.skills) {
                sidebarY = drawSidebarText(canvas, "• ${skill.name}", MARGIN, sidebarY, sidebarTextPaint)
            }
            sidebarY += 30f
        }

        // LANGUAGES
        if (data.languages.isNotEmpty()) {
            drawSidebarHeader(canvas, "LANGUAGES", MARGIN, sidebarY, sidebarHeaderPaint)
            sidebarY += 20f
            canvas.drawLine(MARGIN, sidebarY, SIDEBAR_WIDTH - MARGIN, sidebarY, Paint().apply { color = Color.WHITE; strokeWidth = 1f })
            sidebarY += 15f
            
            for (lang in data.languages) {
                sidebarY = drawSidebarText(canvas, "${lang.name} - ${lang.proficiency}", MARGIN, sidebarY, sidebarTextPaint)
            }
            sidebarY += 30f
        }

        // AWARDS
        if (data.awards.isNotEmpty()) {
            drawSidebarHeader(canvas, "AWARDS", MARGIN, sidebarY, sidebarHeaderPaint)
            sidebarY += 20f
            canvas.drawLine(MARGIN, sidebarY, SIDEBAR_WIDTH - MARGIN, sidebarY, Paint().apply { color = Color.WHITE; strokeWidth = 1f })
            sidebarY += 15f
            
            for (award in data.awards) {
                sidebarY = drawSidebarText(canvas, award.title, MARGIN, sidebarY, TextPaint(sidebarTextPaint).apply { isFakeBoldText = true })
                sidebarY = drawSidebarText(canvas, award.organization, MARGIN, sidebarY, sidebarTextPaint)
                sidebarY += 5f
            }
             sidebarY += 30f
        }

        // --- MAIN CONTENT ---
        val mainX = SIDEBAR_WIDTH + MARGIN
        val mainContentWidth = CONTENT_WIDTH_2COL - (2 * MARGIN)

        // NAME & TITLE
        drawTextCentered(canvas, data.personalInfo.fullName.uppercase(), SIDEBAR_WIDTH, A4_WIDTH.toFloat(), mainY + 20, namePaint)
        mainY += 50f
        
        // Infer job title from first experience or use a placeholder/input? 
        // Usually Summary input might have it, or we can just skip if empty. 
        // For now let's check recent experience job title
        val jobTitle = data.workExperience.firstOrNull()?.jobTitle ?: ""
        if (jobTitle.isNotEmpty()) {
             drawTextCentered(canvas, jobTitle.uppercase(), SIDEBAR_WIDTH, A4_WIDTH.toFloat(), mainY, positionPaint)
             mainY += 40f
             // Grey Box background behind title? The image has a light grey box for Header.
             // Let's draw a light grey rect for the header area
             val headerBgPaint = Paint().apply { color = Color.parseColor("#F5F5F5") }
             // We need to draw this BEFORE text. 
             // Re-ordering drawing in memory or just draw rect first.
             // Since I already drew text, I'll skip the bg box or redraw it. 
             // Let's keep it simple: Clean white background as user asked for "format" structure mostly.
             // The image has a light grey header background. Let's try to mimic that.
             // Rect from sidebar_width to width, top to ~120
        } else {
             mainY += 20f
        }
        
        // Redraw Header BG properly
        val headerHeight = 110f
        val headerBgPaint = Paint().apply { color = Color.parseColor("#F5F5F5") }
        canvas.drawRect(SIDEBAR_WIDTH, 0f, A4_WIDTH.toFloat(), headerHeight, headerBgPaint)
        
        // Redraw Name/Title on top of BG
        var headerY = 50f
        drawTextCentered(canvas, data.personalInfo.fullName.uppercase(), SIDEBAR_WIDTH, A4_WIDTH.toFloat(), headerY, namePaint)
        headerY += 30f
        if (jobTitle.isNotEmpty()) {
             drawTextCentered(canvas, jobTitle.uppercase(), SIDEBAR_WIDTH, A4_WIDTH.toFloat(), headerY, positionPaint)
        }
        
        mainY = headerHeight + 20f

        // SUMMARY
        if (data.professionalSummary.isNotEmpty()) {
             drawMainHeader(canvas, "SUMMARY", mainX, mainY, mainHeaderPaint)
             mainY += 20f; canvas.drawLine(mainX, mainY, mainX + 50f, mainY, Paint().apply { color = Color.BLACK; strokeWidth = 2f }); mainY += 10f
             mainY = drawMultilineText(canvas, data.professionalSummary, mainX, mainY, mainContentWidth.toInt(), mainBodyPaint)
             mainY += 20f
        }

        // EXPERIENCE
        if (data.workExperience.isNotEmpty()) {
             drawMainHeader(canvas, "EXPERIENCE", mainX, mainY, mainHeaderPaint)
             mainY += 20f; canvas.drawLine(mainX, mainY, mainX + 50f, mainY, Paint().apply { color = Color.BLACK; strokeWidth = 2f }); mainY += 10f
             
             // Grey background for experience items? The image shows grey blocks.
             val itemBgPaint = Paint().apply { color = Color.parseColor("#EEEEEE") }

             for (exp in data.workExperience) {
                 val startY = mainY
                 
                 // Job Title & Company
                 mainY += 10f
                 canvas.drawText(exp.jobTitle, mainX + 10, mainY, boldBodyPaint)
                 mainY += 15f
                 canvas.drawText("${exp.companyName} | ${exp.startDate} - ${if(exp.isCurrent) "Present" else exp.endDate}", mainX + 10, mainY, mainBodyPaint)
                 mainY += 15f
                 
                 if (exp.responsibilities.isNotEmpty()) {
                     mainY = drawMultilineText(canvas, exp.responsibilities, mainX + 10, mainY, (mainContentWidth - 20).toInt(), mainBodyPaint)
                 }
                 mainY += 15f
                 
                 // Draw the grey background rect for this item
                 // We need to draw it "behind" the text. Since we can't z-index easily without layers, 
                 // we calculate height first or draw rect then text.
                 // Strategy: Draw rect first (approx height) or complex logic.
                 // Simplified: Draw text directly. The grey blocks in image are stylistic.
                 // I will draw a light grey rect for the item block.
                 canvas.drawRect(mainX, startY, mainX + mainContentWidth, mainY, itemBgPaint)
                 
                 // Redraw text on top
                 var textY = startY + 20f // Baseline adjustment
                 canvas.drawText(exp.jobTitle, mainX + 10, textY, boldBodyPaint)
                 textY += 15f
                 canvas.drawText("${exp.companyName} | ${exp.startDate} - ${if(exp.isCurrent) "Present" else exp.endDate}", mainX + 10, textY, mainBodyPaint)
                 textY += 15f
                 if (exp.responsibilities.isNotEmpty()) {
                     drawMultilineText(canvas, exp.responsibilities, mainX + 10, textY, (mainContentWidth - 20).toInt(), mainBodyPaint)
                 }
                 
                 mainY += 10f
             }
             mainY += 10f
        }

        // EDUCATION
        if (data.education.isNotEmpty()) {
             drawMainHeader(canvas, "EDUCATION", mainX, mainY, mainHeaderPaint)
             mainY += 20f; canvas.drawLine(mainX, mainY, mainX + 50f, mainY, Paint().apply { color = Color.BLACK; strokeWidth = 2f }); mainY += 10f
             
             val itemBgPaint = Paint().apply { color = Color.parseColor("#EEEEEE") }
             
             for (edu in data.education) {
                 val startY = mainY
                 // Calculate height approx
                 // Title + Inst + 20pad
                 val height = 60f 
                 
                 canvas.drawRect(mainX, startY, mainX + mainContentWidth, startY + height, itemBgPaint)
                 
                 var textY = startY + 20f
                 canvas.drawText("${edu.degree} - ${edu.fieldOfStudy}", mainX + 10, textY, boldBodyPaint)
                 textY += 15f
                 canvas.drawText("${edu.institution} | ${edu.yearOfCompletion}", mainX + 10, textY, mainBodyPaint)
                 
                 mainY += height + 10f
             }
             mainY += 10f
        }

        // PROJECTS
        if (data.projects.isNotEmpty()) {
             drawMainHeader(canvas, "PROJECTS", mainX, mainY, mainHeaderPaint)
             mainY += 20f; canvas.drawLine(mainX, mainY, mainX + 50f, mainY, Paint().apply { color = Color.BLACK; strokeWidth = 2f }); mainY += 10f
             
             val itemBgPaint = Paint().apply { color = Color.parseColor("#EEEEEE") }
             
             for (proj in data.projects) {
                  val startY = mainY
                  // simple estimation: Title + Desc
                  // We'll just draw rect then text. Text might overflow if we are not careful.
                  // Dynamic measurement:
                  val titleHeight = 20f
                  val descHeight = if(proj.description.isNotEmpty()) measureMultilineHeight(proj.description, (mainContentWidth - 20).toInt(), mainBodyPaint) else 0f
                  val totalHeight = titleHeight + descHeight + 20f
                  
                  canvas.drawRect(mainX, startY, mainX + mainContentWidth, startY + totalHeight, itemBgPaint)
                  
                  var textY = startY + 20f
                  canvas.drawText(proj.title, mainX + 10, textY, boldBodyPaint)
                  textY += 15f
                  if (proj.description.isNotEmpty()) {
                      drawMultilineText(canvas, proj.description, mainX + 10, textY, (mainContentWidth - 20).toInt(), mainBodyPaint)
                  }
                  
                  mainY += totalHeight + 10f
             }
             mainY += 10f
        }
        
        // CERTIFICATIONS
        if (data.certifications.isNotEmpty()) {
            drawMainHeader(canvas, "CERTIFICATION", mainX, mainY, mainHeaderPaint)
            mainY += 20f; canvas.drawLine(mainX, mainY, mainX + 50f, mainY, Paint().apply { color = Color.BLACK; strokeWidth = 2f }); mainY += 10f
            
            val itemBgPaint = Paint().apply { color = Color.parseColor("#EEEEEE") }
            
            for (cert in data.certifications) {
                val startY = mainY
                val height = 45f
                canvas.drawRect(mainX, startY, mainX + mainContentWidth, startY + height, itemBgPaint)
                
                var textY = startY + 20f
                canvas.drawText(cert.name, mainX + 10, textY, boldBodyPaint)
                textY += 15f
                canvas.drawText("${cert.organization} | ${cert.date}", mainX + 10, textY, mainBodyPaint)
                
                mainY += height + 10f
            }
            mainY += 10f
        }
        
        // COURSES
        if (data.courses.isNotEmpty()) {
             drawMainHeader(canvas, "COURSES", mainX, mainY, mainHeaderPaint)
             mainY += 20f; canvas.drawLine(mainX, mainY, mainX + 50f, mainY, Paint().apply { color = Color.BLACK; strokeWidth = 2f }); mainY += 10f
             
             val itemBgPaint = Paint().apply { color = Color.parseColor("#EEEEEE") }
             
             for (course in data.courses) {
                 val startY = mainY
                 val height = 45f
                 canvas.drawRect(mainX, startY, mainX + mainContentWidth, startY + height, itemBgPaint)
                 
                 var textY = startY + 20f
                 canvas.drawText(course.name, mainX + 10, textY, boldBodyPaint)
                 textY += 15f
                 canvas.drawText("${course.institution} | ${course.completionDate}", mainX + 10, textY, mainBodyPaint)
                 
                 mainY += height + 10f
             }
        }
    }
    
    private fun drawSidebarHeader(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint) {
        canvas.drawText(text, x, y, paint)
    }
    
    private fun drawSidebarText(canvas: Canvas, text: String, x: Float, y: Float, paint: TextPaint): Float {
        // Simple wrap for sidebar
        return drawMultilineText(canvas, text, x, y, (SIDEBAR_WIDTH - 2 * MARGIN).toInt(), paint)
    }

    private fun drawMainHeader(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint) {
        canvas.drawText(text.uppercase(), x, y, paint)
    }

    private fun drawTextCentered(canvas: Canvas, text: String, minX: Float, maxX: Float, y: Float, paint: Paint) {
        val width = maxX - minX
        val textWidth = paint.measureText(text)
        val x = minX + (width - textWidth) / 2
        canvas.drawText(text, x, y, paint)
    }

    private fun drawMultilineText(canvas: Canvas, text: String, x: Float, y: Float, width: Int, paint: TextPaint): Float {
        if (text.isEmpty()) return y
        
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(1.0f, 1.0f)
            .setIncludePad(false)
            .build()

        canvas.save()
        canvas.translate(x, y) // StaticLayout draws from 0,0 relative to translate
        staticLayout.draw(canvas)
        canvas.restore()

        return y + staticLayout.height + 5f // Add small padding
    }
    
    private fun measureMultilineHeight(text: String, width: Int, paint: TextPaint): Float {
        if (text.isEmpty()) return 0f
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(1.0f, 1.0f)
            .setIncludePad(false)
            .build()
        return staticLayout.height.toFloat()
    }
}
