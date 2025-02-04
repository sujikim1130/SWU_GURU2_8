package com.example.guru_8.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_8.data.Expense
import com.example.guru_8.R

// RecyclerView 어댑터: 지출 항목을 리스트 형태로 표시
class SpendingAdapter(private val spendingList: List<Expense>) :
    RecyclerView.Adapter<SpendingAdapter.SpendingViewHolder>() {

    // ViewHolder 클래스: 각 항목의 뷰를 관리
    inner class SpendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val amountText: TextView = itemView.findViewById(R.id.amountText) // 금액 표시
        private val categoryText: TextView = itemView.findViewById(R.id.categoryText) // 카테고리 표시

        // 개별 항목 데이터 바인딩 함수
        fun bind(expense: Expense) {
            amountText.text = "${expense.amount.toInt()}원" // 금액을 원화 단위로 변환하여 표시
            categoryText.text = expense.category // 카테고리 텍스트 설정
        }
    }

    // 새로운 ViewHolder를 생성하는 메서드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spending, parent, false) // 레이아웃 XML을 뷰로 변환
        return SpendingViewHolder(view) // 변환된 뷰를 ViewHolder에 전달
    }

    // ViewHolder와 데이터를 연결하는 메서드
    override fun onBindViewHolder(holder: SpendingViewHolder, position: Int) {
        holder.bind(spendingList[position]) // 현재 위치의 데이터를 ViewHolder에 바인딩
    }

    // 리스트의 전체 항목 수 반환
    override fun getItemCount() = spendingList.size
}
