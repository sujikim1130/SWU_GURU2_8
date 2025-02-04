package com.example.guru_8.data

/**
 * 지출 내역을 저장하는 데이터 클래스
 * @param id 지출 ID
 * @param amount 지출 금액
 * @param detail 지출 상세 내용
 * @param transactionType 거래 유형 (수입/지출)
 * @param category 지출 카테고리
 * @param date 지출 날짜
 * @param isSelectedThumbsUp 긍정적인 반응 여부 (UI에서 사용)
 * @param isSelectedThumbsDown 부정적인 반응 여부 (UI에서 사용)
 */
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

    /**
     * 객체를 문자열로 변환하는 함수
     * 출력 형식: [날짜] 상세내용 ±금액원
     */
    override fun toString(): String {
        val sign = if (transactionType == "수입") "+" else "-"
        return "[$date] $detail $sign ${amount.toInt()}원"
    }
}
