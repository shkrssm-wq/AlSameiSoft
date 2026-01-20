package com.alsamei.soft.security

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("alsamei_auth", Context.MODE_PRIVATE)

    fun login(username: String, password: String): Boolean {
        // Lookup user from DB (not implemented here), compare hash
        val storedHash = "" // load from DB
        return hash(password) == storedHash
    }

    fun hash(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun saveSession(userId: Long) {
        prefs.edit().putLong("user_id", userId).apply()
    }
}