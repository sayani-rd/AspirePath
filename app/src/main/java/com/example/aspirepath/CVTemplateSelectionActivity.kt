package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.aspirepath.models.CVTemplate

class CVTemplateSelectionActivity : AppCompatActivity() {

    private lateinit var recyclerViewTemplates: RecyclerView
    private lateinit var btnContinue: Button
    private lateinit var adapter: TemplateAdapter
    private var selectedTemplateId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cv_template_selection)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
             onBackPressedDispatcher.onBackPressed()
        }

        recyclerViewTemplates = findViewById(R.id.recyclerViewTemplates)
        btnContinue = findViewById(R.id.btnContinueToDetails)

        val templates = listOf(
            CVTemplate("professional", "Professional", "Minimalist with sidebar layout.", R.drawable.preview_professional),
            CVTemplate("modern", "Modern", "Clean, corporate style with blue accents.", R.drawable.preview_modern),
            CVTemplate("creative", "Creative", "Colorful with icons and visual elements.", R.drawable.preview_creative),
            CVTemplate("academic", "Academic", "Traditional format focused on education.", R.drawable.preview_academic),
            CVTemplate("simple", "Simple", "Basic black and white format.", R.drawable.preview_simple)
        )

        adapter = TemplateAdapter(templates) { template ->
            selectedTemplateId = template.id
            btnContinue.isEnabled = true
        }

        recyclerViewTemplates.layoutManager = GridLayoutManager(this, 2)
        recyclerViewTemplates.adapter = adapter

        btnContinue.setOnClickListener {
            val id = selectedTemplateId ?: "modern"
            val intent = Intent(this, CVDetailsInputActivity::class.java)
            intent.putExtra("TEMPLATE_ID", id)
            startActivity(intent)
        }
    }

    class TemplateAdapter(
        private val templates: List<CVTemplate>,
        private val onTemplateSelected: (CVTemplate) -> Unit
    ) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

        private var selectedPosition = -1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cv_template, parent, false)
            return TemplateViewHolder(view)
        }

        override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
            val template = templates[position]
            holder.bind(template, position == selectedPosition)
            
            holder.itemView.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)
                onTemplateSelected(template)
            }
        }

        override fun getItemCount() = templates.size

        class TemplateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvName: TextView = itemView.findViewById(R.id.tvTemplateName)
            private val tvDesc: TextView = itemView.findViewById(R.id.tvTemplateDescription)
            private val ivPreview: ImageView = itemView.findViewById(R.id.ivTemplatePreview)
            private val selectionOverlay: View = itemView.findViewById(R.id.selectionOverlay)
            private val cardView: CardView = itemView as CardView

            fun bind(template: CVTemplate, isSelected: Boolean) {
                tvName.text = template.name
                tvDesc.text = template.description
                
                // Load drawable preview image
                ivPreview.setImageResource(template.previewResId)

                if (isSelected) {
                    // cardView.setCardElevation(8f) 
                    selectionOverlay.visibility = View.VISIBLE
                } else {
                    // cardView.setCardElevation(4f)
                    selectionOverlay.visibility = View.GONE
                }
            }
        }
    }
}
