package com.example.guru_8.data

// 지출 데이터를 저장하는 데이터 클래스
data class Expense(
    val id: Long, // 고유 ID
    val amount: Double, // 지출 금액
    val detail: String, // 지출 내역 상세 정보
    val transactionType: String, // 거래 유형 (수입/지출)
    val category: String, // 카테고리
    val date: String, // 지출 날짜
    var isSelectedThumbsUp: Boolean = false, // '좋아요' 선택 여부
    var isSelectedThumbsDown: Boolean = false // '싫어요' 선택 여부
) {

    // 객체를 문자열로 변환할 때 사용되는 메서드
    override fun toString(): String {
        // 출력 형식: [날짜] 상세 내역 (+500원 또는 -500원)
        val sign = if (transactionType == "수입") "+" else "-"
        return "[$date] $detail $sign ${amount.toInt()}원"
    }
}
