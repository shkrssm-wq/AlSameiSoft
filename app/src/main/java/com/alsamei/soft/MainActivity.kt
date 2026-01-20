package com.alsamei.soft

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alsamei.soft.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAccounts.setOnClickListener {
            // TODO: Launch AccountsActivity
        }
        binding.btnPartners.setOnClickListener {
            // TODO: Launch PartnersActivity
        }
        binding.btnSales.setOnClickListener {
            // TODO: Launch SalesActivity
        }
        binding.btnReports.setOnClickListener {
            // TODO: Launch ReportsActivity
        }
    }
}