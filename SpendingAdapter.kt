package com.example.guru_8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SpendingAdapter(private val spendingList: MutableList<Pair<String, Double>>) :
    RecyclerView.Adapter<SpendingAdapter.SpendingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_spending, parent, false)
        return SpendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpendingViewHolder, position: Int) { //카테고리와 금액 데이터를 표시
        val (category, amount) = spendingList[position]
        holder.categoryText.text = category
        holder.amountText.text = "${amount}원"
    }

    override fun getItemCount(): Int = spendingList.size

    class SpendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //각 아이템을 저장하고 관리하는 클래스
        val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        val amountText: TextView = itemView.findViewById(R.id.amountText)
    }
}