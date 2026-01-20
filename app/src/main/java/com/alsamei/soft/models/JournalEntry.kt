package com.alsamei.soft.models

data class JournalEntry(
    val id: Long = 0,
    val refNo: String,
    val date: String,
    val description: String,
    val lines: List<JournalLine>
)