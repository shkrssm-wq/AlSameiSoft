package com.alsamei.soft.ui.partners

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.alsamei.soft.database.DatabaseHelper
import com.alsamei.soft.databinding.ActivityPartnersBinding

class PartnersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPartnersBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPartnersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        binding.btnAddPartner.setOnClickListener {
            startActivity(Intent(this, AddPartnerActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadPartners()
    }

    private fun loadPartners() {
        val list = db.getAllPartners()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list.map { "${it.name} - ${it.type}" })
        binding.listViewPartners.adapter = adapter
    }
}