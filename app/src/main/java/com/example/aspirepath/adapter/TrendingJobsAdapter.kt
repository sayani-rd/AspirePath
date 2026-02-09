package com.example.aspirepath.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.R
import com.example.aspirepath.models.TrendingJobItem

class TrendingJobsAdapter(private val items: List<TrendingJobItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_JOB = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TrendingJobItem.Header -> TYPE_HEADER
            is TrendingJobItem.Job -> TYPE_JOB
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_section_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_job_card, parent, false)
                JobViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TrendingJobItem.Header -> (holder as HeaderViewHolder).bind(item)
            is TrendingJobItem.Job -> (holder as JobViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvSectionTitle)

        fun bind(header: TrendingJobItem.Header) {
            tvTitle.text = header.title
        }
    }

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvJobDescription)
        private val tvSkills: TextView = itemView.findViewById(R.id.tvJobSkills)
        private val tvQualifications: TextView = itemView.findViewById(R.id.tvJobQualifications)
        private val tvCertifications: TextView = itemView.findViewById(R.id.tvJobCertifications)
        private val layoutCertifications: View = itemView.findViewById(R.id.layoutCertifications)

        fun bind(job: TrendingJobItem.Job) {
            tvTitle.text = job.title
            tvDescription.text = job.description
            tvSkills.text = job.skills
            tvQualifications.text = job.qualifications

            if (job.certifications.isNullOrEmpty()) {
                layoutCertifications.visibility = View.GONE
            } else {
                layoutCertifications.visibility = View.VISIBLE
                tvCertifications.text = job.certifications
            }
            
            // Alternate background colors (Pastel)
            val context = itemView.context
            val colors = listOf(
                R.color.pastel_blue,
                R.color.pastel_purple,
                R.color.pastel_mint,
                R.color.pastel_orange,
                R.color.pastel_pink
            )
            val colorRes = colors[adapterPosition % colors.size]
            (itemView as androidx.cardview.widget.CardView).setCardBackgroundColor(
                androidx.core.content.ContextCompat.getColor(context, colorRes)
            )
        }
    }
}
