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
