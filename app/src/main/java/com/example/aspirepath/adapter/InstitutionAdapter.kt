package com.example.aspirepath.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.R
import com.example.aspirepath.models.Institution

import com.example.aspirepath.utils.ViewExtensions.applyPopEffect

class InstitutionAdapter(private var institutions: List<Institution>) :
    RecyclerView.Adapter<InstitutionAdapter.InstitutionViewHolder>() {

    class InstitutionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textViewName)
        val location: TextView = itemView.findViewById(R.id.textViewLocation)
        val taluka: TextView = itemView.findViewById(R.id.textViewTaluka)
        val streams: TextView = itemView.findViewById(R.id.textViewStreams)
        val contact: TextView = itemView.findViewById(R.id.textViewContact)
        val email: TextView = itemView.findViewById(R.id.textViewEmail)
        val website: TextView = itemView.findViewById(R.id.textViewWebsite)
        val contactLayout: LinearLayout = itemView.findViewById(R.id.layoutContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstitutionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_institution, parent, false)
        return InstitutionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstitutionViewHolder, position: Int) {
        val institution = institutions[position]

        holder.itemView.applyPopEffect()

        holder.name.text = institution.name
        holder.location.text = institution.location
        holder.taluka.text = institution.taluka
        
        if (institution.streamsOrPrograms.isNotEmpty()) {
            holder.streams.text = "Streams/Programs: ${institution.streamsOrPrograms}"
            holder.streams.visibility = View.VISIBLE
        } else {
            holder.streams.visibility = View.GONE
        }

        var contactInfoVisible = false

        if (institution.contactNumber.isNotEmpty()) {
            holder.contact.text = "📞 ${institution.contactNumber}"
            holder.contact.visibility = View.VISIBLE
            contactInfoVisible = true
        } else {
            holder.contact.visibility = View.GONE
        }

        if (institution.email.isNotEmpty()) {
            holder.email.text = "📧 ${institution.email}"
            holder.email.visibility = View.VISIBLE
            contactInfoVisible = true
        } else {
            holder.email.visibility = View.GONE
        }

        holder.contactLayout.visibility = if (contactInfoVisible) View.VISIBLE else View.GONE

        if (institution.websiteUrl.isNotEmpty()) {
            holder.website.visibility = View.VISIBLE
            holder.website.applyPopEffect()
            holder.website.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(institution.websiteUrl))
                    it.context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            holder.website.visibility = View.GONE
        }

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

    override fun getItemCount() = institutions.size

    fun updateList(newList: List<Institution>) {
        institutions = newList
        notifyDataSetChanged()
    }
}
