package com.example.aspirepath

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class SuccessStoriesAdapter(private val items: List<SuccessStoryItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_STORY = 1
    }

    // List of aesthetically pleasing pastel colors
    private val pastelColors = listOf(
        "#E3F2FD", // Light Blue
        "#E8F5E9", // Light Green
        "#FFF3E0", // Light Orange
        "#F3E5F5", // Light Purple
        "#FFEBEE", // Light Pink
        "#E0F2F1", // Light Teal
        "#FFFDE7", // Light Yellow
        "#ECEFF1", // Light Blue Grey
        "#FCE4EC", // Pink
        "#E0F7FA"  // Cyan
    )

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SuccessStoryItem.Header -> TYPE_HEADER
            is SuccessStoryItem.Story -> TYPE_STORY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_success_story_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_success_story_content, parent, false)
            StoryViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        
        if (holder is HeaderViewHolder && item is SuccessStoryItem.Header) {
            holder.tvHeader.text = item.title
            
        } else if (holder is StoryViewHolder && item is SuccessStoryItem.Story) {
            holder.tvName.text = item.name
            holder.tvDescription.text = item.description
            
            // Set Initial
            if (item.name.isNotEmpty()) {
                holder.tvInitial.text = item.name.first().toString().uppercase()
            }

            // Apply pastel background color cyclically
            val colorHex = pastelColors[position % pastelColors.size]
            holder.cardView.setCardBackgroundColor(Color.parseColor(colorHex))
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeader: TextView = itemView.findViewById(R.id.tvHeader)
    }

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView as CardView
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvInitial: TextView = itemView.findViewById(R.id.tvInitial)
    }
}
