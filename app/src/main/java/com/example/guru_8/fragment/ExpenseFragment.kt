package com.example.guru_8.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_8.FragmentActivity
import com.example.guru_8.data.DataBaseHelper
import com.example.guru_8.adapters.ExpenseAdapter
import com.example.guru_8.R
import java.text.SimpleDateFormat
import java.util.*

class ExpenseFragment : Fragment() {

    private lateinit var editTextAmount: EditText
    private lateinit var editTextDetail: EditText
    private lateinit var radioGroupTransactionType: RadioGroup
    private lateinit var categoryGroup: GridLayout
    private lateinit var buttonAdd: Button
    private lateinit var recyclerViewExpenses: RecyclerView
    private lateinit var dbManager: DataBaseHelper
    private lateinit var expenseAdapter: ExpenseAdapter

    private var selectedDate: String? = null  // ✅ 선택한 날짜 저장 변수

    companion object {
        private const val ARG_DATE = "selected_date" // ✅ ARG_DATE 정의 추가

        fun newInstance(date: String?): ExpenseFragment {
            val fragment = ExpenseFragment()
            val args = Bundle()
            args.putString(ARG_DATE, date) // ✅ 여기서 "selected_date" 키로 값 저장
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = it.getString(ARG_DATE)
        }

        if (selectedDate == null) {
            val activity = requireActivity() as? FragmentActivity
            selectedDate = activity?.getSelectedDate()
        }

        // ✅ selectedDate가 여전히 null이면 현재 날짜로 설정
        if (selectedDate == null) {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            selectedDate = currentDate
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_expense, container, false)

        // UI 요소 초기화
        editTextAmount = view.findViewById(R.id.editTextAmount)
        editTextDetail = view.findViewById(R.id.editTextDetail)
        radioGroupTransactionType = view.findViewById(R.id.radioGroupTransactionType)
        categoryGroup = view.findViewById(R.id.categoryOption)
        buttonAdd = view.findViewById(R.id.buttonAdd)
        recyclerViewExpenses = view.findViewById(R.id.recyclerViewExpenses)

        // 선택한 날짜를 화면에 표시
        val textSelectedDate: TextView = view.findViewById(R.id.selectedDateTextView)
        textSelectedDate.text = "선택한 날짜: $selectedDate"

        // 카테고리 단일 선택 설정
        setupCategorySelection()

        // DBManager 초기화
        dbManager = DataBaseHelper(requireContext())

        // RecyclerView와 Adapter 설정
        recyclerViewExpenses.layoutManager = LinearLayoutManager(requireContext())

        // ✅ 삭제 기능 연결 (ExpenseAdapter에 onDeleteClick 추가)
        expenseAdapter = ExpenseAdapter(emptyList()) { expenseId ->
            dbManager.deleteExpense(expenseId) // DB에서 삭제
            updateExpenseList() // 리스트 갱신
            Toast.makeText(requireContext(), "지출이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }

        recyclerViewExpenses.adapter = expenseAdapter

        // ✅ 선택한 날짜에 맞는 지출 목록 불러오기
        updateExpenseList()

        // 추가 버튼 클릭 리스너
        buttonAdd.setOnClickListener {
            saveExpenseToDatabase()
        }

        return view
    }


    private fun saveExpenseToDatabase() {
        val amountText = editTextAmount.text.toString()
        val detailText = editTextDetail.text.toString()

        if (amountText.isNotEmpty() && detailText.isNotEmpty() && selectedDate != null) {
            val amount = amountText.toDouble()

            val transactionType = when (radioGroupTransactionType.checkedRadioButtonId) {
                R.id.radioExpense -> "지출"
                R.id.radioIncome -> "수입"
                else -> "지출"
            }

            val selectedCategory = getSelectedCategory()

            Log.d("ExpenseFragment", "🟢 저장될 지출: $selectedCategory, $amount 원, 날짜: $selectedDate")

            dbManager.addExpense(amount, detailText, transactionType, selectedCategory, selectedDate!!)

            updateExpenseList()

            editTextAmount.text.clear()
            editTextDetail.text.clear()
            radioGroupTransactionType.clearCheck()
            radioGroupTransactionType.check(R.id.radioExpense)
            resetCategorySelection()
        } else {
            Toast.makeText(requireContext(), "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ 선택한 날짜에 맞는 지출 목록 업데이트
    private fun updateExpenseList() {
        if (selectedDate != null) {
            val expenses = dbManager.getAllExpensesForUser(selectedDate!!)
            expenseAdapter.updateList(expenses)
        }
    }

    // 카테고리 단일 선택 유지 함수
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

    // 기존 선택 해제 함수
    private fun clearCategorySelection() {
        for (i in 0 until categoryGroup.childCount) {
            val view = categoryGroup.getChildAt(i)
            if (view is RadioButton) {
                view.isChecked = false
            }
        }
    }

    // 선택된 카테고리 가져오기 함수
    private fun getSelectedCategory(): String {
        for (i in 0 until categoryGroup.childCount) {
            val view = categoryGroup.getChildAt(i)
            if (view is RadioButton && view.isChecked) {
                return view.text.toString()
            }
        }
        return "기타"
    }

    // 카테고리 선택 초기화
    private fun resetCategorySelection() {
        view?.findViewById<RadioButton>(R.id.categoryOpt8)?.isChecked = true // 기본값: '기타'
    }

}

