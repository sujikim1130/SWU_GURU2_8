package com.example.financialledgerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.graphics.PorterDuff

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

        // Thumbs Up 버튼 상태
        holder.thumbsUp.setSelected(expense.isSelectedThumbsUp)
        holder.thumbsUp.setOnClickListener {
            expense.isSelectedThumbsUp = !expense.isSelectedThumbsUp
            expense.isSelectedThumbsDown = false  // thumbsDown 해제
            notifyItemChanged(position)
        }

        // Thumbs Down 버튼 상태
        holder.thumbsDown.setSelected(expense.isSelectedThumbsDown)
        holder.thumbsDown.setOnClickListener {
            expense.isSelectedThumbsDown = !expense.isSelectedThumbsDown
            expense.isSelectedThumbsUp = false  // thumbsUp 해제
            notifyItemChanged(position)
        }

        // 색상 변경 (thumbsUp, thumbsDown 선택 상태에 따라)
        if (expense.isSelectedThumbsUp) {
            holder.thumbsUp.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.blue), PorterDuff.Mode.SRC_IN)
        } else {
            holder.thumbsUp.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.gray), PorterDuff.Mode.SRC_IN)
        }

        if (expense.isSelectedThumbsDown) {
            holder.thumbsDown.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red), PorterDuff.Mode.SRC_IN)
        } else {
            holder.thumbsDown.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.gray), PorterDuff.Mode.SRC_IN)
        }
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    fun updateList(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()  // RecyclerView 업데이트
    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDetail: TextView = itemView.findViewById(R.id.textDetail)
        val textAmount: TextView = itemView.findViewById(R.id.textAmount)

        val thumbsUp: ImageView = itemView.findViewById(R.id.thumbsUp)  // thumbsUp 버튼
        val thumbsDown: ImageView = itemView.findViewById(R.id.thumbsDown)  // thumbsDown 버튼
    }
}
