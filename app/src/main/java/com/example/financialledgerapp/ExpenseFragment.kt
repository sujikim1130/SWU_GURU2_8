package com.example.financialledgerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financialledgerapp.DataBase.Companion.COLUMN_AMOUNT
import com.example.financialledgerapp.DataBase.Companion.COLUMN_CATEGORY
import com.example.financialledgerapp.DataBase.Companion.COLUMN_DATE
import com.example.financialledgerapp.DataBase.Companion.COLUMN_DESCRIPTION
import com.example.financialledgerapp.DataBase.Companion.COLUMN_EXPENSE_ID
import com.example.financialledgerapp.DataBase.Companion.COLUMN_USER_ID_FK
import com.example.financialledgerapp.DataBase.Companion.TABLE_EXPENSES
import java.text.SimpleDateFormat
import java.util.*
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ExpenseFragment : Fragment() {

    private lateinit var editTextAmount: EditText
    private lateinit var editTextDetail: EditText
    private lateinit var radioGroupTransactionType: RadioGroup
    private lateinit var categoryGroup: GridLayout
    private lateinit var buttonAdd: Button
    private lateinit var recyclerViewExpenses: RecyclerView
    private lateinit var dbManager: DataBase
    private lateinit var expenseAdapter: ExpenseAdapter
    private var userId: Long = -1 // 로그인된 사용자 ID (예시 값)

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

        // 로그인된 사용자 ID 설정 (예시: 현재 로그인된 사용자 정보 가져오기)
        userId = getCurrentUserId()

        // 추가 버튼 클릭 리스너
        buttonAdd.setOnClickListener {
            val amountText = editTextAmount.text.toString()
            val detailText = editTextDetail.text.toString()

            // 금액과 상세 내역 입력 검증
            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), "유효한 금액을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (detailText.isEmpty()) {
                Toast.makeText(requireContext(), "상세 내역을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 지출/수입 선택
            val transactionType = when (radioGroupTransactionType.checkedRadioButtonId) {
                R.id.radioExpense -> "지출"
                R.id.radioIncome -> "수입"
                else -> "지출"
            }

                // 선택된 카테고리 가져오기
            val selectedCategory = getSelectedCategory()

                // DB에 새로운 항목 추가 (카테고리 포함)
            dbManager.addExpense(userId, amount, getCurrentDate(), selectedCategory, detailText)

            // 리스트 업데이트
            val updatedExpenses = dbManager.getAllExpenses()
            expenseAdapter.updateList(updatedExpenses)

            // 입력 필드 초기화
            editTextAmount.text.clear()
            editTextDetail.text.clear()
            radioGroupTransactionType.clearCheck()
            radioGroupTransactionType.check(R.id.radioExpense) // 기본 선택: '지출'
            resetCategorySelection() // 카테고리 선택 초기화
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
    // 현재 날짜 가져오기
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // 로그인된 사용자 ID 가져오는 함수 (예시)
    private fun getCurrentUserId(): Long {
        // 실제로 로그인 시스템을 구현하여 사용자 ID를 가져오도록 수정 필요
        return 1L // 기본값 예시
    }

fun getAllExpensesForUser(userId: Long): List<Map<String, Any>> {
    val db = readableDatabase
    val expenses = mutableListOf<Map<String, Any>>()

    val cursor = db.query(
        TABLE_EXPENSES,
        arrayOf(COLUMN_EXPENSE_ID, COLUMN_USER_ID_FK, COLUMN_AMOUNT, COLUMN_DATE, COLUMN_CATEGORY, COLUMN_DESCRIPTION),
        "$COLUMN_USER_ID_FK = ?",
        arrayOf(userId.toString()),
        null,
        null,
        null
    )

    if (cursor.moveToFirst()) {
        do {
            val expense = mapOf(
                "id" to cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)),
                "userId" to cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK)),
                "amount" to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                "date" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                "category" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                "description" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            )
            expenses.add(expense)
        } while (cursor.moveToNext())
    }
    cursor.close()
    db.close()
    return expenses
}

