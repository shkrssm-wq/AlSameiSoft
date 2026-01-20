package com.alsamei.soft.ui.accounts

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.alsamei.soft.database.DatabaseHelper
import com.alsamei.soft.databinding.ActivityAccountsBinding
import com.alsamei.soft.models.Account

class AccountsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountsBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        binding.btnAddAccount.setOnClickListener {
            startActivity(Intent(this, AddAccountActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadAccounts()
    }

    private fun loadAccounts() {
        val list = db.getAllAccounts()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list.map { "${it.code} - ${it.name}" })
        binding.listViewAccounts.adapter = adapter
    }
}