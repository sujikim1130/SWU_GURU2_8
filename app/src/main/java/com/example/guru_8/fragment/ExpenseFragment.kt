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

// 지출 내역을 관리하는 프래그먼트
class ExpenseFragment : Fragment() {

    // UI 요소 선언
    private lateinit var editTextAmount: EditText
    private lateinit var editTextDetail: EditText
    private lateinit var radioGroupTransactionType: RadioGroup
    private lateinit var categoryGroup: GridLayout
    private lateinit var buttonAdd: Button
    private lateinit var recyclerViewExpenses: RecyclerView
    private lateinit var dbManager: DataBaseHelper
    private lateinit var expenseAdapter: ExpenseAdapter

    private var selectedDate: String? = null  // 선택한 날짜 저장 변수

    companion object {
        private const val ARG_DATE = "selected_date" // 인자 키 값 정의

        // 날짜를 전달받아 새로운 인스턴스를 생성하는 메서드
        fun newInstance(date: String?): ExpenseFragment {
            val fragment = ExpenseFragment()
            val args = Bundle()
            args.putString(ARG_DATE, date)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = it.getString(ARG_DATE) // 전달받은 날짜 설정
        }

        if (selectedDate == null) {
            val activity = requireActivity() as? FragmentActivity
            selectedDate = activity?.getSelectedDate()
        }

        // 날짜가 없으면 현재 날짜로 설정
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

        // 선택한 날짜 표시
        val textSelectedDate: TextView = view.findViewById(R.id.selectedDateTextView)
        textSelectedDate.text = "선택한 날짜: $selectedDate"

        // 카테고리 단일 선택 설정
        setupCategorySelection()

        // 데이터베이스 관리 객체 초기화
        dbManager = DataBaseHelper(requireContext())

        // RecyclerView 설정
        recyclerViewExpenses.layoutManager = LinearLayoutManager(requireContext())

        // RecyclerView 어댑터 설정 (삭제 기능 포함)
        expenseAdapter = ExpenseAdapter(emptyList()) { expenseId ->
            dbManager.deleteExpense(expenseId) // DB에서 삭제
            updateExpenseList() // 리스트 갱신
            Toast.makeText(requireContext(), "지출이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }

        recyclerViewExpenses.adapter = expenseAdapter
        updateExpenseList() // 초기 리스트 로드

        // 추가 버튼 클릭 이벤트 설정
        buttonAdd.setOnClickListener {
            saveExpenseToDatabase()
        }

        return view
    }

    // 데이터베이스에 지출 저장
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

            // 입력 필드 초기화
            editTextAmount.text.clear()
            editTextDetail.text.clear()
            radioGroupTransactionType.clearCheck()
            radioGroupTransactionType.check(R.id.radioExpense)
            resetCategorySelection()
        } else {
            Toast.makeText(requireContext(), "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show()
        }
    }

    // 선택한 날짜에 맞는 지출 내역 업데이트
    private fun updateExpenseList() {
        if (selectedDate != null) {
            val expenses = dbManager.getAllExpensesForUser(selectedDate!!)
            expenseAdapter.updateList(expenses)
        }
    }

    // 카테고리 단일 선택 설정
    private fun setupCategorySelection() {
        for (i in 0 until categoryGroup.childCount) {
            val view = categoryGroup.getChildAt(i)
            if (view is RadioButton) {
                view.setOnClickListener {
                    clearCategorySelection()
                    view.isChecked = true
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

    // 선택된 카테고리 반환
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
