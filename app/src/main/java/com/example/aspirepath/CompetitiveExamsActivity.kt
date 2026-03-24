package com.example.aspirepath

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.utils.UserProfileHelper

class CompetitiveExamsActivity : AppCompatActivity() {

    /** Map each category to the streams it is relevant for. */
    private val categoryStreamMap = mapOf(
        "Medical" to listOf("Science"),
        "National Level Medical Entrance Exams" to listOf("Science"),
        "Engineering" to listOf("Science"),
        "National Level Engineering Entrance Exams" to listOf("Science"),
        "Architecture and Design" to listOf("Science", "Arts", "Commerce"),
        "Fashion" to listOf("Arts", "Commerce", "Science"),
        "Fine Arts" to listOf("Arts"),
        "Languages" to listOf("Arts"),
        "Law" to listOf("Arts", "Commerce", "Science"),
        "Humanities and Social Sciences" to listOf("Arts"),
        "Banking" to listOf("Commerce", "Arts", "Science"),
        "Commerce" to listOf("Commerce"),
        "Defence / Marine" to listOf("Science")
    )

    private val allCategories = categoryStreamMap.keys.toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competitive_exams)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        UserProfileHelper.fetch {
            val filtered = filterCategoriesByStream(
                UserProfileHelper.stream,
                UserProfileHelper.eligibility
            )
            recyclerView.adapter = CategoryAdapter(filtered) { category ->
                val intent = Intent(this, ExamCategoryActivity::class.java)
                intent.putExtra("CATEGORY_NAME", category)
                startActivity(intent)
            }
        }
    }

    private fun filterCategoriesByStream(stream: String, eligibility: String): List<String> {
        // 10th Completed, blank stream, or "Other" → show all
        if (eligibility == "10th Completed" || stream.isBlank() || stream.equals("Other", true)) {
            return allCategories
        }
        return allCategories.filter { category ->
            val streams = categoryStreamMap[category] ?: emptyList()
            streams.isEmpty() || streams.any { it.equals(stream, true) }
        }
    }

    inner class CategoryAdapter(
        private val items: List<String>,
        private val onClick: (String) -> Unit
    ) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvCategoryName)
            val tvIcon: TextView = view.findViewById(R.id.tvIcon)

            init {
                view.setOnClickListener { onClick(items[adapterPosition]) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_exam_category, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvName.text = item
            holder.tvIcon.text = item.first().toString()
            
            // Alternate Background Colors
            val context = holder.itemView.context
            val colors = listOf(
                R.color.pastel_blue,
                R.color.pastel_purple,
                R.color.pastel_mint,
                R.color.pastel_orange,
                R.color.pastel_pink
            )
            val colorRes = colors[position % colors.size]
            (holder.itemView as androidx.cardview.widget.CardView).setCardBackgroundColor(
                androidx.core.content.ContextCompat.getColor(context, colorRes)
            )
        }

        override fun getItemCount() = items.size
    }
}
