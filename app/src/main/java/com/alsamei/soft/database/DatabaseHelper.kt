package com.alsamei.soft.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

    // Example method: addJournalEntry (transactional)
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
}

data class JournalLine(
    val accountId: Long,
    val partnerId: Long? = null,
    val debit: Double = 0.0,
    val credit: Double = 0.0,
    val currencyId: Long? = null,
    val rate: Double = 1.0
)