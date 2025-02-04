package com.example.guru_8.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_8.data.Expense
import com.example.guru_8.R

/**
 * RecyclerView 어댑터: 지출 내역을 리스트 형태로 표시하는 클래스
 */
class SpendingAdapter(private val spendingList: List<Expense>) :
    RecyclerView.Adapter<SpendingAdapter.SpendingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendingViewHolder {
        // 개별 항목의 뷰 생성
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spending, parent, false)
        return SpendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpendingViewHolder, position: Int) {
        // 해당 위치의 데이터를 ViewHolder에 바인딩
        holder.bind(spendingList[position])
    }

    override fun getItemCount(): Int {
        return spendingList.size // 리스트 크기 반환
    }

    /**
     * 개별 지출 항목을 표시하는 ViewHolder
     */
    inner class SpendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val amountText: TextView = itemView.findViewById(R.id.amountText) // 지출 금액 표시
        private val categoryText: TextView = itemView.findViewById(R.id.categoryText) // 지출 카테고리 표시

        /**
         * UI에 데이터를 설정하는 함수
         */
        fun bind(expense: Expense) {
            amountText.text = "${expense.amount.toInt()}원" // 금액 표시
            categoryText.text = expense.category // 카테고리 표시
        }
    }
}
