package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar

class ShippingInstitutesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping_institutes)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toolbar.setNavigationOnClickListener {
             finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.rvShippingInstitutes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        val adapter = ShippingInstitutesAdapter(this, ShippingInstituteData.institutes)
        recyclerView.adapter = adapter
    }
}
