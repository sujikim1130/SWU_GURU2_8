package com.example.guru_8.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_8.DBManager
import com.example.guru_8.Expense
import com.example.guru_8.R
import com.example.guru_8.SpendingAdapter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class StatsFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var currentSpendingText: TextView
    private lateinit var limitInput: EditText
    private lateinit var saveLimitButton: Button
    private lateinit var spendingRecyclerView: RecyclerView

    private lateinit var dbManager: DBManager
    private var spendingLimit = 0
    private var currentSpending = 0
    private val spendingList = mutableListOf<Expense>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = view.findViewById(R.id.pieChart)
        currentSpendingText = view.findViewById(R.id.currentSpendingText)
        limitInput = view.findViewById(R.id.limitInput)
        saveLimitButton = view.findViewById(R.id.saveLimitButton)
        spendingRecyclerView = view.findViewById(R.id.spendingRecyclerView)

        dbManager = DBManager(requireContext())

        val sharedPreferences = requireActivity().getSharedPreferences("SpendingPrefs", 0)
        spendingLimit = sharedPreferences.getInt("spendingLimit", 0)
        updateSpendingText()

        saveLimitButton.setOnClickListener {
            val limitText = limitInput.text.toString()
            if (limitText.isNotEmpty() && limitText.toIntOrNull() != null) {
                spendingLimit = limitText.toInt()
                sharedPreferences.edit().putInt("spendingLimit", spendingLimit).apply()
                Toast.makeText(requireContext(), "한도가 저장되었습니다: ${spendingLimit}원", Toast.LENGTH_SHORT).show()
                updateSpendingText()

                if (currentSpending > spendingLimit) {
                    val excess = currentSpending - spendingLimit
                    Toast.makeText(requireContext(), "현재 지출이 한도를 초과했습니다! 초과 금액: ${excess}원", Toast.LENGTH_LONG).show()
                    currentSpendingText.setTextColor(Color.RED) // 텍스트 색상 변경
                } else {
                    currentSpendingText.setTextColor(Color.BLACK) // 초과하지 않으면 텍스트 색상 초기화
                }
            } else {
                Toast.makeText(requireContext(), "유효한 한도를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        setupRecyclerView()
        loadSpendingDataFromDatabase()



        /*fun insertTestData() {
            // DBManager를 통해 데이터베이스에 임의의 테스트 데이터 삽입
            dbManager.addExpense(5000.0, "커피", "expense", "식비")
            dbManager.addExpense(20000.0, "영화", "expense", "문화")
            dbManager.addExpense(150000.0, "월급", "income", "수입")

            // 데이터 삽입 후 UI 업데이트를 위해 데이터 다시 로드
            loadSpendingDataFromDatabase()

            Toast.makeText(requireContext(), "테스트 데이터가 추가되었습니다.", Toast.LENGTH_SHORT).show()
        }
        if (spendingList.isEmpty()) {
            insertTestData()
        }*/
    }

    private fun setupRecyclerView() {
        spendingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        spendingRecyclerView.adapter = SpendingAdapter(spendingList)
    }

    private fun loadSpendingDataFromDatabase() {
        val expenses = dbManager.getAllExpenses() // DB 지출 받아오기
        currentSpending = 0
        spendingList.clear()

        for (expense in expenses) { //지출 항목만 리스트에 저장
            if (expense.transactionType == "expense") {
                spendingList.add(expense)
                currentSpending += expense.amount.toInt()
            }
        }

        spendingRecyclerView.adapter?.notifyDataSetChanged()
        updateSpendingText()
        updatePieChart()
    }

    private fun updateSpendingText() {
        currentSpendingText.text = "현재 지출: ${currentSpending}원 / 한도: ${spendingLimit}원"
    }

    private fun updatePieChart() {
        val categoryMap = spendingList.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val entries = categoryMap.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "카테고리별 지출")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.centerText = "카테고리별 지출"
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}
