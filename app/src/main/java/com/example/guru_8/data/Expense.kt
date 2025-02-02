package com.example.guru_8.data

data class Expense(
    val id: Long,
    val amount: Double,
    val detail: String,
    val transactionType: String,
    val category: String,
    val date: String,
    var isSelectedThumbsUp: Boolean = false,
    var isSelectedThumbsDown: Boolean = false
    ) {

    override fun toString(): String {
        // 출력 형식: [물 - 500원] (수입이면 +, 지출이면 -)
        val sign = if (transactionType == "수입") "+" else "-"
        return "[$date] $detail $sign ${amount.toInt()}원"
    }
}
