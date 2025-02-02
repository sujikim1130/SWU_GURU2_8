package com.example.guru_8.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_8.data.DataBaseHelper
import com.example.guru_8.adapters.ExpenseAdapter
import com.example.guru_8.R

class ExpenseFragment : Fragment() {

    private lateinit var editTextAmount: EditText
    private lateinit var editTextDetail: EditText
    private lateinit var radioGroupTransactionType: RadioGroup
    private lateinit var categoryGroup: GridLayout
    private lateinit var buttonAdd: Button
    private lateinit var recyclerViewExpenses: RecyclerView
    private lateinit var dbManager: DataBaseHelper
    private lateinit var expenseAdapter: ExpenseAdapter

    private var selectedDate: String? = null  // ✅ 선택한 날짜 저장 변수 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = it.getString(ARG_DATE)  // ✅ MainCalenderFragment에서 받은 날짜 값 저장
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
            val textSelectedDate: TextView = view.findViewById(R.id.selectedDateTextView) // ✅ 날짜 표시할 TextView 추가
            textSelectedDate.text = "선택한 날짜: $selectedDate" // ✅ UI에 날짜 표시

            // 카테고리 단일 선택 설정
            setupCategorySelection()

            // DBManager 초기화
            dbManager = DataBaseHelper(requireContext())
            // RecyclerView와 Adapter 설정
            recyclerViewExpenses.layoutManager = LinearLayoutManager(requireContext())
            expenseAdapter = ExpenseAdapter(emptyList()) // 초기 데이터는 비어있음
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

                    // 지출/수입 선택
                    val transactionType = when (radioGroupTransactionType.checkedRadioButtonId) {
                        R.id.radioExpense -> "지출"
                        R.id.radioIncome -> "수입"
                        else -> "지출"
                    }

                    // 선택된 카테고리 가져오기
                    val selectedCategory = getSelectedCategory()

                    // ✅ DB에 새로운 항목 추가 (날짜 포함)
                    dbManager.addExpense(amount, detailText, transactionType, selectedCategory, selectedDate!!)

                    // 리스트 업데이트
                    updateExpenseList()

                    // 입력 필드 초기화
                    editTextAmount.text.clear()
                    editTextDetail.text.clear()
                    radioGroupTransactionType.clearCheck()
                    radioGroupTransactionType.check(R.id.radioExpense) // 기본 선택: '지출'
                    resetCategorySelection()
                } else {
                    Toast.makeText(requireContext(), "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show()

            }
        }

        // ✅ 선택한 날짜에 맞는 지출 목록 업데이트
        private fun updateExpenseList() {
            if (selectedDate != null) {
                val expenses = dbManager.getAllExpensesForUser( selectedDate!!)
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

        companion object {
            private const val ARG_DATE = "selected_date"

            fun newInstance(date: String): ExpenseFragment {
                val fragment = ExpenseFragment()
                val args = Bundle()
                args.putString(ARG_DATE, date)
                fragment.arguments = args
                return fragment
            }
        }
    }
