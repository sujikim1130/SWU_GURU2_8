package com.example.guru_8.adapters

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_8.data.Expense
import com.example.guru_8.R

// RecyclerView의 어댑터 클래스
class ExpenseAdapter(
    private var expenses: List<Expense>, // 지출 내역 리스트
    private val onDeleteClick: (Long) -> Unit // 삭제 이벤트 핸들러
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // 뷰 홀더가 생성될 때 호출됨
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    // 뷰 홀더에 데이터를 바인딩하는 메서드
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        // 지출 내역 텍스트 설정
        holder.textDetail.text = expense.detail

        // 금액 표시 (수입: +, 지출: -)
        val amountText = if (expense.transactionType == "수입") {
            "+ ${expense.amount.toInt()}원"
        } else {
            "- ${expense.amount.toInt()}원"
        }
        holder.textAmount.text = amountText

        // 금액 및 상세 내역 텍스트 색상 변경 (수입: 파랑, 지출: 빨강)
        val textColor = if (expense.transactionType == "수입") {
            R.color.blue
        } else {
            R.color.red
        }
        holder.textDetail.setTextColor(ContextCompat.getColor(holder.itemView.context, textColor))
        holder.textAmount.setTextColor(ContextCompat.getColor(holder.itemView.context, textColor))

        // "좋아요" 버튼 상태 설정
        holder.thumbsUp.setSelected(expense.isSelectedThumbsUp)
        holder.thumbsUp.setOnClickListener {
            expense.isSelectedThumbsUp = !expense.isSelectedThumbsUp // 상태 변경
            expense.isSelectedThumbsDown = false  // "싫어요" 해제
            notifyItemChanged(position) // UI 업데이트
        }

        // "싫어요" 버튼 상태 설정
        holder.thumbsDown.setSelected(expense.isSelectedThumbsDown)
        holder.thumbsDown.setOnClickListener {
            expense.isSelectedThumbsDown = !expense.isSelectedThumbsDown // 상태 변경
            expense.isSelectedThumbsUp = false  // "좋아요" 해제
            notifyItemChanged(position) // UI 업데이트
        }

        // 버튼 색상 변경 (선택 여부에 따라 다르게 표시)
        if (expense.isSelectedThumbsUp) {
            holder.thumbsUp.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                R.color.blue
            ), PorterDuff.Mode.SRC_IN)
        } else {
            holder.thumbsUp.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                R.color.gray
            ), PorterDuff.Mode.SRC_IN)
        }

        if (expense.isSelectedThumbsDown) {
            holder.thumbsDown.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                R.color.red
            ), PorterDuff.Mode.SRC_IN)
        } else {
            holder.thumbsDown.setColorFilter(ContextCompat.getColor(holder.itemView.context,
                R.color.gray
            ), PorterDuff.Mode.SRC_IN)
        }

        // 삭제 버튼 클릭 이벤트 설정
        holder.deleteButton.setOnClickListener {
            onDeleteClick(expense.id) // 삭제 이벤트 실행
        }
    }

    // 리스트 크기 반환
    override fun getItemCount(): Int {
        return expenses.size
    }

    // 리스트 업데이트 함수 (새로운 데이터로 교체 후 UI 갱신)
    fun updateList(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    // ViewHolder 클래스 (각 아이템의 뷰를 보유)
    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDetail: TextView = itemView.findViewById(R.id.textDetail) // 지출 내역
        val textAmount: TextView = itemView.findViewById(R.id.textAmount) // 금액
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton) // 삭제 버튼
        val thumbsUp: ImageView = itemView.findViewById(R.id.thumbsUp) // 좋아요 버튼
        val thumbsDown: ImageView = itemView.findViewById(R.id.thumbsDown) // 싫어요 버튼
    }
}
