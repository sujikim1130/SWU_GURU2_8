package com.example.financialledgerapp

data class Expense(
    val id: Long,
    val amount: Double,
    val detail: String,
    val transactionType: String,
    val category: String
) {
    // 'toString()' 메서드 오버라이드
    override fun toString(): String {
        // 출력 형식: [물 - 500원] (수입이면 +, 지출이면 -)
        val sign = if (transactionType == "수입") "+" else "-"
        return "$detail $sign ${amount.toInt()}원"
    }
}
