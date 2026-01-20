package com.alsamei.soft.ui.partners

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alsamei.soft.database.DatabaseHelper
import com.alsamei.soft.databinding.ActivityAddPartnerBinding
import com.alsamei.soft.models.Partner

class AddPartnerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPartnerBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPartnerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val type = binding.etType.text.toString().trim()
            if (name.isEmpty() || type.isEmpty()) {
                Toast.makeText(this, "يرجى ملء الحقول الأساسية", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val partner = Partner(name = name, type = type, phone = binding.etPhone.text.toString().trim(), email = binding.etEmail.text.toString().trim(), address = binding.etAddress.text.toString().trim())
            db.insertPartner(partner)
            Toast.makeText(this, "تم حفظ الطرف (عميل/مورد)", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}