package com.example.financialledgerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat

class ExpenseAdapter(private var expenses: List<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        // 내역 설정 (왼쪽 정렬)
        holder.textDetail.text = expense.detail

        // 금액 설정 (수입이면 "+", 지출이면 "-")
        val amountText = if (expense.transactionType == "수입") {
            "+ ${expense.amount.toInt()}원"
        } else {
            "- ${expense.amount.toInt()}원"
        }
        holder.textAmount.text = amountText

        // 색상 변경: 수입이면 파랑색, 지출이면 빨간색
        val textColor = if (expense.transactionType == "수입") {
            R.color.blue
        } else {
            R.color.red
        }

        // 텍스트 색상 변경
        holder.textDetail.setTextColor(ContextCompat.getColor(holder.itemView.context, textColor))
        holder.textAmount.setTextColor(ContextCompat.getColor(holder.itemView.context, textColor))
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    fun updateList(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()  // RecyclerView 업데이트
    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDetail: TextView = itemView.findViewById(R.id.textDetail)  // 올바른 ID 사용
        val textAmount: TextView = itemView.findViewById(R.id.textAmount)  // 올바른 ID 사용
    }
}
