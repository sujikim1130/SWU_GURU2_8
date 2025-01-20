package com.example.financialledgerapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextAmount: EditText
    private lateinit var editTextDetail: EditText
    private lateinit var radioGroupTransactionType: RadioGroup
    private lateinit var buttonAdd: Button
    private lateinit var listViewExpenses: ListView
    private lateinit var dbManager: DBManager
    private lateinit var expenseAdapter: ArrayAdapter<Expense>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 요소 초기화
        editTextAmount = findViewById(R.id.editTextAmount)
        editTextDetail = findViewById(R.id.editTextDetail)
        radioGroupTransactionType = findViewById(R.id.radioGroupTransactionType)
        buttonAdd = findViewById(R.id.buttonAdd)
        listViewExpenses = findViewById(R.id.listViewExpenses)

        // DBManager 초기화
        dbManager = DBManager(this)

        // 리스트뷰 어댑터 설정
        val expenses = dbManager.getAllExpenses()
        expenseAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, expenses)
        listViewExpenses.adapter = expenseAdapter

        // 추가 버튼 클릭 리스너
        buttonAdd.setOnClickListener {
            val amountText = editTextAmount.text.toString()
            val detailText = editTextDetail.text.toString()

            if (amountText.isNotEmpty() && detailText.isNotEmpty()) {
                val amount = amountText.toDouble()

                // 지출/수입 선택
                val transactionType = when (radioGroupTransactionType.checkedRadioButtonId) {
                    R.id.radioExpense -> "지출"  // 기본 선택: 지출
                    R.id.radioIncome -> "수입"
                    else -> "지출"
                }

                // DB에 새로운 항목 추가
                dbManager.addExpense(amount, detailText, transactionType)

                // 리스트 업데이트
                val updatedExpenses = dbManager.getAllExpenses()
                expenseAdapter.clear()
                expenseAdapter.addAll(updatedExpenses)

                // 입력 필드 초기화
                editTextAmount.text.clear()
                editTextDetail.text.clear()
                radioGroupTransactionType.clearCheck()
                radioGroupTransactionType.check(R.id.radioExpense)  // 기본 선택으로 '지출'
            } else {
                Toast.makeText(this, "금액과 상세 내역을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 리스트뷰 항목 클릭 시 수정/삭제 기능 추가 가능 (필요 시 구현)
        listViewExpenses.setOnItemClickListener { _, _, position, _ ->
            val selectedExpense = expenseAdapter.getItem(position)
            // 수정/삭제 처리 (선택된 항목에 대한 처리 추가)
        }
    }
}
