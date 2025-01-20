package com.example.financialledgerapp

// 소비 내역을 저장할 데이터 클래스
data class Expense(
    val id: Long,        // ID
    val amount: Double,  // 금액
    val detail: String,  // 상세 내역
    val transactionType: String     // 지출/수입 유형 (ex: "expense", "income")
)

