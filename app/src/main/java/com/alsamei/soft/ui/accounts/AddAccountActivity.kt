package com.alsamei.soft.ui.accounts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alsamei.soft.database.DatabaseHelper
import com.alsamei.soft.databinding.ActivityAddAccountBinding
import com.alsamei.soft.models.Account

class AddAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAccountBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        binding.btnSave.setOnClickListener {
            val code = binding.etCode.text.toString().trim()
            val name = binding.etName.text.toString().trim()
            val type = binding.etType.text.toString().trim()
            if (code.isEmpty() || name.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, "يرجى ملء الحقول الأساسية", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val account = Account(code = code, name = name, type = type)
            db.insertAccount(account)
            Toast.makeText(this, "تم حفظ الحساب", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}