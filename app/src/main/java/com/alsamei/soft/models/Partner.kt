package com.alsamei.soft.models

data class Partner(
    val id: Long = 0,
    val name: String,
    val type: String, // customer or supplier
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val accountId: Long? = null
)