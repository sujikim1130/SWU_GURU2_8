package com.example.financialledgerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpenseFragment : Fragment() {

    private lateinit var editTextAmount: EditText
    private lateinit var editTextDetail: EditText
    private lateinit var radioGroupTransactionType: RadioGroup
    private lateinit var categoryGroup: GridLayout
    private lateinit var buttonAdd: Button
    private lateinit var recyclerViewExpenses: RecyclerView
    private lateinit var dbManager: DataBase
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expense, container, false)

        // UI 요소 초기화
        editTextAmount = view.findViewById(R.id.editTextAmount)
        editTextDetail = view.findViewById(R.id.editTextDetail)
        radioGroupTransactionType = view.findViewById(R.id.radioGroupTransactionType)
        categoryGroup = view.findViewById(R.id.categoryOption)
        buttonAdd = view.findViewById(R.id.buttonAdd)
        recyclerViewExpenses = view.findViewById(R.id.recyclerViewExpenses)

        // 카테고리 단일 선택 설정
        setupCategorySelection()

        // DBManager 초기화
        dbManager = DataBase(requireContext())

        // RecyclerView와 Adapter 설정
        recyclerViewExpenses.layoutManager = LinearLayoutManager(requireContext())
        expenseAdapter = ExpenseAdapter(emptyList()) // 초기 데이터는 비어있음
        recyclerViewExpenses.adapter = expenseAdapter

        // 리스트 갱신
        val expenses = dbManager.getAllExpensesForUser()
        expenseAdapter.updateList(expenses)

        // 추가 버튼 클릭 리스너
        buttonAdd.setOnClickListener {
            val amountText = editTextAmount.text.toString()
            val detailText = editTextDetail.text.toString()

            if (amountText.isNotEmpty() && detailText.isNotEmpty()) {
                val amount = amountText.toDouble()

                // 지출/수입 선택
                val transactionType = when (radioGroupTransactionType.checkedRadioButtonId) {
                    R.id.radioExpense -> "지출"
                    R.id.radioIncome -> "수입"
                    else -> "지출"
                }

                // 선택된 카테고리 가져오기
                val selectedCategory = getSelectedCategory()

                // DB에 새로운 항목 추가 (카테고리 포함)
                dbManager.addExpense(amount, detailText, transactionType, selectedCategory)

                // 리스트 업데이트
                val updatedExpenses = dbManager.getAllExpensesForUser()
                expenseAdapter.updateList(updatedExpenses)

                // 입력 필드 초기화
                editTextAmount.text.clear()
                editTextDetail.text.clear()
                radioGroupTransactionType.clearCheck()
                radioGroupTransactionType.check(R.id.radioExpense) // 기본 선택: '지출'
                resetCategorySelection() // 카테고리 선택 초기화
            } else {
                Toast.makeText(requireContext(), "금액과 상세 내역을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // 단일 선택 유지 함수 추가
    private fun setupCategorySelection() {
        for (i in 0 until categoryGroup.childCount) {
            val view = categoryGroup.getChildAt(i)
            if (view is RadioButton) {
                view.setOnClickListener {
                    clearCategorySelection() // 다른 선택 해제
                    view.isChecked = true    // 현재 선택 유지
                }
            }
        }
    }

    // 기존 선택 해제 함수 추가
    private fun clearCategorySelection() {
        for (i in 0 until categoryGroup.childCount) {
            val view = categoryGroup.getChildAt(i)
            if (view is RadioButton) {
                view.isChecked = false
            }
        }
    }

    // 선택된 카테고리 가져오는 함수 (유지)
    private fun getSelectedCategory(): String {
        for (i in 0 until categoryGroup.childCount) {
            val view = categoryGroup.getChildAt(i)
            if (view is RadioButton && view.isChecked) {
                return view.text.toString()
            }
        }
        return "기타" // 기본값 설정
    }

    // 카테고리 선택 초기화 (유지)
    private fun resetCategorySelection() {
        view?.findViewById<RadioButton>(R.id.categoryOpt8)?.isChecked = true // 기본값: '기타'
    }
}
