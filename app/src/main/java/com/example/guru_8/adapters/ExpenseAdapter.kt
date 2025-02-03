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

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onDeleteClick: (Long) -> Unit // 🛑 삭제 이벤트 핸들러 추가
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        // 내역 설정
        holder.textDetail.text = expense.detail

        // 금액 표시
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

        // 🛑 삭제 버튼 클릭 이벤트 추가
        holder.deleteButton.setOnClickListener {
            onDeleteClick(expense.id) // 삭제 메서드 호출
        }
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    // 🛑 리스트 업데이트 함수
    fun updateList(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDetail: TextView = itemView.findViewById(R.id.textDetail)
        val textAmount: TextView = itemView.findViewById(R.id.textAmount)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton) // 🛑 삭제 버튼 추가
    }
}
