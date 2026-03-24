package com.example.aspirepath

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class SavedCvListActivity : AppCompatActivity() {

    data class SavedCvItem(
        val id: String?,
        val name: String,
        val uri: String?,
        val path: String?,
        val templateId: String?,
        val cvDataBase64: String?,
        val updatedAt: Timestamp?
    )

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: SavedCvAdapter
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_cv_list)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Your Saved CVs"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recyclerView = findViewById(R.id.recyclerSavedCvs)
        tvEmpty = findViewById(R.id.tvEmptySavedCvs)

        adapter = SavedCvAdapter { item -> openCv(item) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadSavedCvs()
    }

    override fun onResume() {
        super.onResume()
        loadSavedCvs()
    }

    private fun loadSavedCvs() {
        val user = auth.currentUser ?: run {
            showEmpty()
            return
        }

        db.collection("users").document(user.uid)
            .collection("savedCVs")
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    SavedCvItem(
                        id = doc.id,
                        name = name,
                        uri = doc.getString("uri"),
                        path = doc.getString("path"),
                        templateId = doc.getString("templateId"),
                        cvDataBase64 = doc.getString("cvDataBase64"),
                        updatedAt = doc.getTimestamp("updatedAt")
                    )
                }.sortedByDescending { it.updatedAt?.seconds ?: 0L }

                if (items.isNotEmpty()) {
                    showList(items)
                } else {
                    loadLegacyLatestCv(user.uid)
                }
            }
            .addOnFailureListener {
                loadLegacyLatestCv(user.uid)
            }
    }

    private fun loadLegacyLatestCv(uid: String) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val latest = doc.get("latestCV") as? Map<*, *>
                val name = latest?.get("name") as? String
                if (name.isNullOrBlank()) {
                    showEmpty()
                    return@addOnSuccessListener
                }

                val item = SavedCvItem(
                    id = null,
                    name = name,
                    uri = latest["uri"] as? String,
                    path = latest["path"] as? String,
                    templateId = latest["templateId"] as? String,
                    cvDataBase64 = latest["cvDataBase64"] as? String,
                    updatedAt = latest["updatedAt"] as? Timestamp
                )
                showList(listOf(item))
            }
            .addOnFailureListener {
                showEmpty()
            }
    }

    private fun showList(items: List<SavedCvItem>) {
        tvEmpty.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        adapter.submit(items)
    }

    private fun showEmpty() {
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
    }

    private fun openCv(item: SavedCvItem) {
        val intent = Intent(this, CVPreviewActivity::class.java).apply {
            putExtra("OPEN_SAVED_CV", true)
            putExtra("SAVED_CV_ID", item.id)
            putExtra("SAVED_CV_URI", item.uri ?: item.path?.let { Uri.fromFile(File(it)).toString() })
            putExtra("SAVED_TEMPLATE_ID", item.templateId ?: "modern")
            putExtra("SAVED_CV_DATA_BASE64", item.cvDataBase64)
            val uri = item.uri?.let { Uri.parse(it) } ?: item.path?.let { Uri.fromFile(File(it)) }
            if (uri?.scheme == "file") {
                putExtra("SAVED_CV_PATH", uri.path)
            }
        }
        startActivity(intent)
    }

    class SavedCvAdapter(
        private val onClick: (SavedCvItem) -> Unit
    ) : RecyclerView.Adapter<SavedCvAdapter.SavedCvViewHolder>() {

        private val items = mutableListOf<SavedCvItem>()

        fun submit(newItems: List<SavedCvItem>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCvViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_saved_cv, parent, false)
            return SavedCvViewHolder(view)
        }

        override fun onBindViewHolder(holder: SavedCvViewHolder, position: Int) {
            holder.bind(items[position], onClick)
        }

        override fun getItemCount(): Int = items.size

        class SavedCvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val card = itemView.findViewById<CardView>(R.id.cardSavedCv)
            private val tvName = itemView.findViewById<TextView>(R.id.tvSavedCvName)
            private val tvMeta = itemView.findViewById<TextView>(R.id.tvSavedCvMeta)

            fun bind(item: SavedCvItem, onClick: (SavedCvItem) -> Unit) {
                tvName.text = item.name
                tvMeta.text = item.updatedAt?.toDate()?.let {
                    "Saved on " + SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(it)
                } ?: "Saved CV"

                card.setOnClickListener { onClick(item) }
            }
        }
    }
}