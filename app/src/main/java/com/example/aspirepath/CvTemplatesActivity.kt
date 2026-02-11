package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.model.CvTemplate

class CvTemplatesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cv_templates)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val templates = listOf(
            CvTemplate(1, "Modern", R.drawable.preview_modern),
            CvTemplate(2, "Professional", R.drawable.preview_professional),
            CvTemplate(3, "Creative", R.drawable.preview_creative),
            CvTemplate(4, "Minimal", R.drawable.preview_minimal)
        )

        val rvTemplates = findViewById<RecyclerView>(R.id.rvTemplates)
        rvTemplates.layoutManager = GridLayoutManager(this, 2)
        rvTemplates.adapter = CvTemplateAdapter(templates) { template ->
            val intent = Intent(this, CvEditorActivity::class.java)
            intent.putExtra("TEMPLATE_ID", template.id)
            startActivity(intent)
        }
        
    }



    class CvTemplateAdapter(
        private val templates: List<CvTemplate>,
        private val onClick: (CvTemplate) -> Unit
    ) : RecyclerView.Adapter<CvTemplateAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivPreview: ImageView = view.findViewById(R.id.ivTemplatePreview)
            val tvName: TextView = view.findViewById(R.id.tvTemplateName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cv_template, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val template = templates[position]
            holder.tvName.text = template.name
            holder.ivPreview.setImageResource(template.previewResId) 
            
            // Set distinct background colors to differentiate templates visually
            val color = when (template.id) {
                1 -> 0xFFE3F2FD.toInt() // Light Blue for Modern
                2 -> 0xFFE8F5E9.toInt() // Light Green for Professional
                3 -> 0xFFFCE4EC.toInt() // Light Pink for Creative
                4 -> 0xFFF5F5F5.toInt() // Light Grey for Minimal
                else -> 0xFFE1F5FE.toInt()
            }
            holder.ivPreview.setBackgroundColor(color)
            holder.ivPreview.clearColorFilter() // Show original vector colors
            
            holder.itemView.setOnClickListener { onClick(template) }
        }

        override fun getItemCount() = templates.size
    }
}
