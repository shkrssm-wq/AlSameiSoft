package com.alsamei.soft.models

data class Account(
    val id: Long = 0,
    val code: String,
    val name: String,
    val type: String, // asset, liability, equity, income, expense
    val parentId: Long? = null,
    val openingBalance: Double = 0.0,
    val currencyId: Long = 1
)