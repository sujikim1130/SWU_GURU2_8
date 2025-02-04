package com.example.guru_8.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_8.data.Expense
import com.example.guru_8.R

/**
 * RecyclerView 어댑터: 지출 내역을 리스트 형태로 표시하며 삭제 기능을 제공
 */
class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onDeleteClick: (Long) -> Unit // 삭제 이벤트 핸들러
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        // 내역 설정
        holder.textDetail.text = expense.detail

        // 금액 표시: 수입(+) / 지출(-)
        val amountText = if (expense.transactionType == "수입") {
            "+ ${expense.amount.toInt()}원"
        } else {
            "- ${expense.amount.toInt()}원"
        }
        holder.textAmount.text = amountText

        // 색상 변경 (수입: 파랑, 지출: 빨강)
        val textColor = if (expense.transactionType == "수입") {
            R.color.blue
        } else {
            R.color.red
        }
        holder.textDetail.setTextColor(ContextCompat.getColor(holder.itemView.context, textColor))
        holder.textAmount.setTextColor(ContextCompat.getColor(holder.itemView.context, textColor))

        // 삭제 버튼 클릭 이벤트
        holder.deleteButton.setOnClickListener {
            onDeleteClick(expense.id) // 삭제 메서드 호출
        }
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    /**
     * 지출 리스트를 갱신하는 함수
     */
    fun updateList(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    /**
     * 개별 지출 항목을 표시하는 ViewHolder
     */
    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDetail: TextView = itemView.findViewById(R.id.textDetail) // 지출 내역 설명
        val textAmount: TextView = itemView.findViewById(R.id.textAmount) // 지출 금액
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton) // 삭제 버튼
    }
}
