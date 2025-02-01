package com.example.guru_8.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_8.data.DataBaseHelper
import com.example.guru_8.data.Expense
import com.example.guru_8.R
import com.example.guru_8.adapters.SpendingAdapter
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

    private lateinit var dbManager: DataBaseHelper
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

        dbManager = DataBaseHelper(requireContext())

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

        setFragmentResultListener("updateStats") { _, _ ->
            Log.d("StatsFragment", "🟢 setFragmentResultListener 호출됨 - 데이터 갱신 시작")
            requireActivity().runOnUiThread {
                loadSpendingDataFromDatabase()
            } // 리스트 및 차트 갱신
        }

    }

    private fun setupRecyclerView() {
        spendingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        spendingRecyclerView.adapter = SpendingAdapter(spendingList)
    }

    private fun loadSpendingDataFromDatabase() {
        val expenses = dbManager.getAllExpensesForUser() // DB 지출 받아오기
        Log.d("StatsFragment", "🔵 불러온 지출 내역 개수: ${expenses.size}")

        currentSpending = 0
        spendingList.clear()

        for (expense in expenses) { //지출 항목만 리스트에 저장
            if (expense.transactionType == "지출") {
                spendingList.add(expense)
                currentSpending += expense.amount.toInt()
            }
        }
        Log.d("StatsFragment", "🟣 현재 지출 금액: $currentSpending")

        requireActivity().runOnUiThread {
            spendingRecyclerView.adapter?.notifyDataSetChanged()
            updateSpendingText()
            updatePieChart()
        }
    }

    private fun updateSpendingText() {
        currentSpendingText.text = "현재 지출: ${currentSpending}원 | 한도: ${spendingLimit}원"
    }

    private fun updatePieChart() {
        val categoryMap = spendingList.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val entries = categoryMap.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, " ")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.centerText = "카테고리별 지출"
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}