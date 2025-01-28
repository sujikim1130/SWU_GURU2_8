package com.example.guru_8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialledgerapp.R

class SpendingAdapter(private val spendingList: List<Expense>) : //지출항목을 리스트 형태로 표시
    RecyclerView.Adapter<SpendingAdapter.SpendingViewHolder>() {

    inner class SpendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //지출 금액과 카테고리를 표시
        private val amountText: TextView = itemView.findViewById(R.id.amountText)
        private val categoryText: TextView = itemView.findViewById(R.id.categoryText)

        fun bind(expense: Expense) {
            amountText.text = "${expense.amount.toInt()}원"
            categoryText.text = expense.category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spending, parent, false)
        return SpendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpendingViewHolder, position: Int) {
        holder.bind(spendingList[position])
    }

    override fun getItemCount() = spendingList.size
}
