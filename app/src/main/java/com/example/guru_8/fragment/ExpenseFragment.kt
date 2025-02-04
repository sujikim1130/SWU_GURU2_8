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

/**
 * 사용자의 지출 내역을 관리하는 프래그먼트
 */
class ExpenseFragment : Fragment() {

    private lateinit var editTextAmount: EditText // 금액 입력 필드
    private lateinit var editTextDetail: EditText // 지출 상세 입력 필드
    private lateinit var radioGroupTransactionType: RadioGroup // 수입/지출 선택 라디오 그룹
    private lateinit var categoryGroup: GridLayout // 카테고리 선택 버튼 그룹
    private lateinit var buttonAdd: Button // 추가 버튼
    private lateinit var recyclerViewExpenses: RecyclerView // 지출 목록 RecyclerView
    private lateinit var dbManager: DataBaseHelper // 데이터베이스 관리자
    private lateinit var expenseAdapter: ExpenseAdapter // 지출 목록 어댑터

    private var selectedDate: String? = null // 선택한 날짜 저장

    companion object {
        private const val ARG_DATE = "selected_date" // 선택한 날짜 전달 키

        /**
         * 선택한 날짜를 가진 새 ExpenseFragment 인스턴스를 반환
         */
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
            selectedDate = it.getString(ARG_DATE)
        }

        if (selectedDate == null) {
            val activity = requireActivity() as? FragmentActivity
            selectedDate = activity?.getSelectedDate()
        }

        // 선택한 날짜가 없으면 현재 날짜로 설정
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

        // 데이터베이스 및 RecyclerView 설정
        dbManager = DataBaseHelper(requireContext())
        recyclerViewExpenses.layoutManager = LinearLayoutManager(requireContext())

        // 삭제 기능을 포함한 어댑터 설정
        expenseAdapter = ExpenseAdapter(emptyList()) { expenseId ->
            dbManager.deleteExpense(expenseId) // 데이터베이스에서 삭제
            updateExpenseList() // UI 갱신
            Toast.makeText(requireContext(), "지출이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }

        recyclerViewExpenses.adapter = expenseAdapter

        // 선택한 날짜의 지출 내역 불러오기
        updateExpenseList()

        // 추가 버튼 클릭 이벤트 설정
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

    /**
     * RecyclerView를 최신 데이터로 업데이트하는 함수
     */
    private fun updateExpenseList() {
        if (selectedDate != null) {
            val expenses = dbManager.getAllExpensesForUser(selectedDate!!)
            expenseAdapter.updateList(expenses)
        }
    }

    /**
     * 카테고리 단일 선택을 설정하는 함수
     */
    private fun setupCategorySelection() {
        for (i in 0 until categoryGroup.childCount) {
            val view = categoryGroup.getChildAt(i)
            if (view is RadioButton) {
                view.setOnClickListener {
                    clearCategorySelection() // 기존 선택 해제
                    view.isChecked = true    // 현재 선택 유지
                }
            }
        }
    }

    /**
     * 기존 선택된 카테고리를 해제하는 함수
     */
    private fun clearCategorySelection() {
        for (i in 0 until categoryGroup.childCount) {
            val view = categoryGroup.getChildAt(i)
            if (view is RadioButton) {
                view.isChecked = false
            }
        }
    }

    /**
     * 선택된 카테고리를 반환하는 함수
     */
    private fun getSelectedCategory(): String {
        for (i in 0 until categoryGroup.childCount) {
            val view = categoryGroup.getChildAt(i)
            if (view is RadioButton && view.isChecked) {
                return view.text.toString()
            }
        }
        return "기타"
    }

    /**
     * 카테고리 선택을 초기화하는 함수
     */
    private fun resetCategorySelection() {
        view?.findViewById<RadioButton>(R.id.categoryOpt8)?.isChecked = true // 기본값: '기타'
    }
}
