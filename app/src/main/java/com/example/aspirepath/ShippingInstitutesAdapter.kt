package com.example.aspirepath

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import androidx.core.content.ContextCompat

class ShippingInstitutesAdapter(
    private val context: Context,
    private val institutes: List<ShippingInstitute>
) : RecyclerView.Adapter<ShippingInstitutesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvInstituteName)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvWebsite: TextView = view.findViewById(R.id.tvWebsite)
        val tvCourses: TextView = view.findViewById(R.id.tvCourses)
        val tvQualifications: TextView = view.findViewById(R.id.tvQualifications)
        val tvAge: TextView = view.findViewById(R.id.tvAge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shipping_institute, parent, false)
        return ViewHolder(view)
    }

    private val pastelColors = listOf(
        R.color.pastel_blue,
        R.color.pastel_purple,
        R.color.pastel_mint,
        R.color.pastel_orange,
        R.color.pastel_pink,
        R.color.pastel_yellow,
        R.color.pastel_lavender
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val institute = institutes[position]
        
        // Random pastel logic
        // Using position for deterministic color assignment
        val colorRes = pastelColors[position % pastelColors.size]
        (holder.itemView as androidx.cardview.widget.CardView).setCardBackgroundColor(
            ContextCompat.getColor(context, colorRes)
        )

        holder.tvName.text = institute.name
        holder.tvDescription.text = institute.description
        holder.tvLocation.text = institute.location
        holder.tvWebsite.text = institute.website

        // Format lists with bullet points
        holder.tvCourses.text = formatList(institute.courses)
        holder.tvQualifications.text = formatList(institute.qualifications)
        holder.tvAge.text = formatList(institute.ageRequirement)
    }

    private fun formatList(items: List<String>): String {
        return items.joinToString("\n") { "• $it" }
    }

    override fun getItemCount() = institutes.size
}
