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

class CompetitiveExamsActivity : AppCompatActivity() {

    private val categories = listOf(
        "Medical",
        "National Level Medical Entrance Exams",
        "Engineering",
        "National Level Engineering Entrance Exams",
        "Fashion",
        "Languages",
        "Law",
        "Humanities and Social Sciences",
        "Banking",
        "Commerce",
        "Defence / Marine"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competitive_exams)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this, ExamCategoryActivity::class.java)
            intent.putExtra("CATEGORY_NAME", category)
            startActivity(intent)
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
            
            // Generate a random-ish color based on position or name hash if needed, 
            // but keeping it simple for now, relying on the circle_background color. 
        }

        override fun getItemCount() = items.size
    }
}
