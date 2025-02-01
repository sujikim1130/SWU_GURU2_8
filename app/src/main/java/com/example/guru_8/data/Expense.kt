package com.example.guru_8.data

data class Expense(
    val id: Long,
    val amount: Double,
    val detail: String,
    val transactionType: String,
    val category: String,
    var isSelectedThumbsUp: Boolean = false,  // ThumbsUp 선택 여부
    var isSelectedThumbsDown: Boolean = false  // ThumbsDown 선택 여부
) {
    // 'toString()' 메서드 오버라이드
    override fun toString(): String {
        // 출력 형식: [물 - 500원] (수입이면 +, 지출이면 -)
        val sign = if (transactionType == "수입") "+" else "-"
        return "$detail $sign ${amount.toInt()}원"
    }
}
