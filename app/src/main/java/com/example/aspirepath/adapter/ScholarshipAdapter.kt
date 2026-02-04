package com.example.aspirepath.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.R
import com.example.aspirepath.models.Scholarship
import com.google.android.material.button.MaterialButton

class ScholarshipAdapter(
    private val context: Context,
    private val scholarshipList: List<Scholarship>
) : RecyclerView.Adapter<ScholarshipAdapter.ScholarshipViewHolder>() {

    class ScholarshipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvScholarshipName)
        val tvProvider: TextView = itemView.findViewById(R.id.tvProvider)
        val tvDeadline: TextView = itemView.findViewById(R.id.tvDeadline)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val btnGuidelines: MaterialButton = itemView.findViewById(R.id.btnGuidelines)
        val btnApply: MaterialButton = itemView.findViewById(R.id.btnApply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScholarshipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scholarship, parent, false)
        return ScholarshipViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScholarshipViewHolder, position: Int) {
        val scholarship = scholarshipList[position]

        holder.tvName.text = scholarship.name
        holder.tvProvider.text = scholarship.provider
        holder.tvDeadline.text = scholarship.deadline
        holder.tvDescription.text = scholarship.description

        holder.btnGuidelines.setOnClickListener {
            openUrl(scholarship.guidelinesUrl)
        }

        holder.btnApply.setOnClickListener {
            openUrl(scholarship.applyUrl)
        }
    }

    override fun getItemCount(): Int {
        return scholarshipList.size
    }

    private fun openUrl(url: String) {
        if (url.isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
