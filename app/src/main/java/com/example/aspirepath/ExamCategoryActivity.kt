package com.example.aspirepath

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExamCategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_category)

        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Exams"
        
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = categoryName
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val exams = ExamData.getExams(categoryName)
        
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewExams)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ExamAdapter(exams)
    }

    inner class ExamAdapter(private val items: List<Exam>) : RecyclerView.Adapter<ExamAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvExamName)
            val tvPurpose: TextView = view.findViewById(R.id.tvPurpose)
            val tvEligibility: TextView = view.findViewById(R.id.tvEligibility)
            val tvExamDate: TextView = view.findViewById(R.id.tvExamDate)
            val tvApply: TextView = view.findViewById(R.id.tvApply)
            val tvWebsite: TextView = view.findViewById(R.id.tvWebsite)
            
            val layoutPurpose: LinearLayout = view.findViewById(R.id.layoutPurpose)
            val layoutEligibility: LinearLayout = view.findViewById(R.id.layoutEligibility)
            val layoutExamDate: LinearLayout = view.findViewById(R.id.layoutExamDate)
            val layoutApply: LinearLayout = view.findViewById(R.id.layoutApply)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_exam, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvName.text = item.name
            holder.tvWebsite.text = item.website

            bindField(holder.layoutPurpose, holder.tvPurpose, item.purpose)
            bindField(holder.layoutEligibility, holder.tvEligibility, item.eligibility)
            bindField(holder.layoutExamDate, holder.tvExamDate, item.examDate)
            bindField(holder.layoutApply, holder.tvApply, item.applyMode)
        }
        
        private fun bindField(layout: LinearLayout, textView: TextView, value: String) {
            if (value.isBlank()) {
                layout.visibility = View.GONE
            } else {
                layout.visibility = View.VISIBLE
                textView.text = value
            }
        }

        override fun getItemCount() = items.size
    }
}
