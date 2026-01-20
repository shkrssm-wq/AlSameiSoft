package com.alsamei.soft.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.alsamei.soft.models.Account
import com.alsamei.soft.models.JournalLine
import com.alsamei.soft.models.JournalEntry
import com.alsamei.soft.models.Partner

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "alsamei_soft.db"
        const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // users
        db.execSQL("""
            CREATE TABLE users (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              username TEXT UNIQUE,
              password_hash TEXT,
              full_name TEXT,
              phone TEXT,
              email TEXT,
              role_id INTEGER,
              avatar_uri TEXT,
              created_at TEXT
            );
        """
        )

        // roles
        db.execSQL("""
            CREATE TABLE roles (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT,
              description TEXT
            );
        """
        )

        // accounts (chart)
        db.execSQL("""
            CREATE TABLE accounts (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              code TEXT,
              name TEXT,
              type TEXT,
              parent_id INTEGER,
              opening_balance REAL DEFAULT 0,
              currency_id INTEGER
            );
        """
        )

        // partners (customers/suppliers)
        db.execSQL("""
            CREATE TABLE partners (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT,
              type TEXT,
              phone TEXT,
              email TEXT,
              address TEXT,
              account_id INTEGER
            );
        """
        )

        // journals
        db.execSQL("""
            CREATE TABLE journals (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              ref_no TEXT,
              date TEXT,
              description TEXT,
              created_by INTEGER,
              posted INTEGER DEFAULT 0,
              created_at TEXT
            );
        """
        )

        // journal_lines
        db.execSQL("""
            CREATE TABLE journal_lines (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              journal_id INTEGER,
              account_id INTEGER,
              partner_id INTEGER,
              debit REAL DEFAULT 0,
              credit REAL DEFAULT 0,
              currency_id INTEGER,
              rate REAL DEFAULT 1
            );
        """
        )

        // Additional tables (invoices, invoice_lines, payments, items, inventory_movements, currencies, cashboxes) can be added later
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // handle migrations
    }

    // Journal methods
    fun addJournalEntry(refNo: String, date: String, description: String, lines: List<JournalLine>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val totalDebit = lines.sumOf { it.debit }
            val totalCredit = lines.sumOf { it.credit }
            if (kotlin.math.abs(totalDebit - totalCredit) > 0.0001) throw IllegalArgumentException("Debits must equal Credits")
            val cv = ContentValues().apply {
                put("ref_no", refNo)
                put("date", date)
                put("description", description)
                put("created_at", System.currentTimeMillis().toString())
            }
            val journalId = db.insert("journals", null, cv)
            for (line in lines) {
                val lcv = ContentValues().apply {
                    put("journal_id", journalId)
                    put("account_id", line.accountId)
                    put("partner_id", line.partnerId)
                    put("debit", line.debit)
                    put("credit", line.credit)
                    put("currency_id", line.currencyId)
                    put("rate", line.rate)
                }
                db.insert("journal_lines", null, lcv)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    // Accounts CRUD
    fun insertAccount(account: Account): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("code", account.code)
            put("name", account.name)
            put("type", account.type)
            put("parent_id", account.parentId)
            put("opening_balance", account.openingBalance)
            put("currency_id", account.currencyId)
        }
        return db.insert("accounts", null, cv)
    }

    fun getAllAccounts(): List<Account> {
        val db = readableDatabase
        val cursor = db.query("accounts", null, null, null, null, null, "id DESC")
        val list = mutableListOf<Account>()
        cursor.use {
            while (it.moveToNext()) {
                val acc = Account(
                    id = it.getLong(it.getColumnIndexOrThrow("id")),
                    code = it.getString(it.getColumnIndexOrThrow("code")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    type = it.getString(it.getColumnIndexOrThrow("type")),
                    parentId = if (!it.isNull(it.getColumnIndexOrThrow("parent_id"))) it.getLong(it.getColumnIndexOrThrow("parent_id")) else null,
                    openingBalance = it.getDouble(it.getColumnIndexOrThrow("opening_balance")),
                    currencyId = it.getLong(it.getColumnIndexOrThrow("currency_id"))
                )
                list.add(acc)
            }
        }
        return list
    }

    fun updateAccount(account: Account): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("code", account.code)
            put("name", account.name)
            put("type", account.type)
            put("parent_id", account.parentId)
            put("opening_balance", account.openingBalance)
            put("currency_id", account.currencyId)
        }
        return db.update("accounts", cv, "id = ?", arrayOf(account.id.toString()))
    }

    fun deleteAccount(id: Long): Int {
        val db = writableDatabase
        return db.delete("accounts", "id = ?", arrayOf(id.toString()))
    }

    // Partners CRUD
    fun insertPartner(partner: Partner): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("name", partner.name)
            put("type", partner.type)
            put("phone", partner.phone)
            put("email", partner.email)
            put("address", partner.address)
            put("account_id", partner.accountId)
        }
        return db.insert("partners", null, cv)
    }

    fun getAllPartners(): List<Partner> {
        val db = readableDatabase
        val cursor = db.query("partners", null, null, null, null, null, "id DESC")
        val list = mutableListOf<Partner>()
        cursor.use {
            while (it.moveToNext()) {
                val p = Partner(
                    id = it.getLong(it.getColumnIndexOrThrow("id")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    type = it.getString(it.getColumnIndexOrThrow("type")),
                    phone = it.getStringOrNull(it, "phone"),
                    email = it.getStringOrNull(it, "email"),
                    address = it.getStringOrNull(it, "address"),
                    accountId = if (!it.isNull(it.getColumnIndexOrThrow("account_id"))) it.getLong(it.getColumnIndexOrThrow("account_id")) else null
                )
                list.add(p)
            }
        }
        return list
    }

    fun updatePartner(partner: Partner): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("name", partner.name)
            put("type", partner.type)
            put("phone", partner.phone)
            put("email", partner.email)
            put("address", partner.address)
            put("account_id", partner.accountId)
        }
        return db.update("partners", cv, "id = ?", arrayOf(partner.id.toString()))
    }

    fun deletePartner(id: Long): Int {
        val db = writableDatabase
        return db.delete("partners", "id = ?", arrayOf(id.toString()))
    }

    // helper extension for nullable strings
    private fun android.database.Cursor.getStringOrNull(cursor: android.database.Cursor, column: String): String? {
        val idx = cursor.getColumnIndexOrThrow(column)
        return if (cursor.isNull(idx)) null else cursor.getString(idx)
    }
}