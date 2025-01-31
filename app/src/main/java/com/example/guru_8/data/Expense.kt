package com.example.guru_8.data

data class Expense(
    val id: Long,
    val amount: Double,
    val detail: String,
    val transactionType: String,
    val category: String,
    val date: String,  // ✅ 추가된 날짜 필드
    var isSelectedThumbsUp: Boolean = false,
    var isSelectedThumbsDown: Boolean = false
) {
    override fun toString(): String {
        val sign = if (transactionType == "수입") "+" else "-"
        return "[$date] $detail $sign ${amount.toInt()}원"
    }
}