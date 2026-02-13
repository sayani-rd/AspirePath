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
import com.example.aspirepath.models.JobItem
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Locale

class JobsAdapter(private val context: Context, private var jobList: List<JobItem>) :
    RecyclerView.Adapter<JobsAdapter.JobViewHolder>() {

    class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJobTitle: TextView = view.findViewById(R.id.tvJobTitle)
        val tvCompany: TextView = view.findViewById(R.id.tvCompany)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvSalary: TextView = view.findViewById(R.id.tvSalary)
        val tvPosted: TextView = view.findViewById(R.id.tvPosted)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val btnApply: MaterialButton = view.findViewById(R.id.btnApply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.tvJobTitle.text = job.title
        holder.tvCompany.text = job.company
        holder.tvLocation.text = job.location
        holder.tvSalary.text = job.salary
        holder.tvPosted.text = job.postedDate
        holder.tvDescription.text = job.description

        holder.btnApply.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.url))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    fun updateList(newList: List<JobItem>) {
        jobList = newList
        notifyDataSetChanged()
    }
}
