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

// 지출 통계를 보여주는 프래그먼트
class StatsFragment : Fragment() {

    // UI 요소 선언
    private lateinit var pieChart: PieChart
    private lateinit var currentSpendingText: TextView
    private lateinit var limitInput: EditText
    private lateinit var saveLimitButton: Button
    private lateinit var spendingRecyclerView: RecyclerView

    private lateinit var dbManager: DataBaseHelper // 데이터베이스 관리 객체
    private var spendingLimit = 0 // 설정된 지출 한도
    private var currentSpending = 0 // 현재 지출 금액
    private val spendingList = mutableListOf<Expense>() // 지출 내역 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI 요소 초기화
        pieChart = view.findViewById(R.id.pieChart)
        currentSpendingText = view.findViewById(R.id.currentSpendingText)
        limitInput = view.findViewById(R.id.limitInput)
        saveLimitButton = view.findViewById(R.id.saveLimitButton)
        spendingRecyclerView = view.findViewById(R.id.spendingRecyclerView)

        dbManager = DataBaseHelper(requireContext())

        // SharedPreferences에서 지출 한도 불러오기
        val sharedPreferences = requireActivity().getSharedPreferences("SpendingPrefs", 0)
        spendingLimit = sharedPreferences.getInt("spendingLimit", 0)
        updateSpendingText()

        // 한도 저장 버튼 클릭 이벤트 설정
        saveLimitButton.setOnClickListener {
            val limitText = limitInput.text.toString()
            if (limitText.isNotEmpty() && limitText.toIntOrNull() != null) {
                spendingLimit = limitText.toInt()
                sharedPreferences.edit().putInt("spendingLimit", spendingLimit).apply()
                Toast.makeText(requireContext(), "한도가 저장되었습니다: ${spendingLimit}원", Toast.LENGTH_SHORT).show()
                updateSpendingText()

                // 현재 지출이 한도를 초과하면 경고 메시지 표시
                if (currentSpending > spendingLimit) {
                    val excess = currentSpending - spendingLimit
                    Toast.makeText(requireContext(), "현재 지출이 한도를 초과했습니다! 초과 금액: ${excess}원", Toast.LENGTH_LONG).show()
                    currentSpendingText.setTextColor(Color.RED) // 텍스트 색상 변경
                } else {
                    currentSpendingText.setTextColor(Color.BLACK) // 초과하지 않으면 기본 색상 유지
                }
            } else {
                Toast.makeText(requireContext(), "유효한 한도를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        setupRecyclerView()
        loadSpendingDataFromDatabase()

        // 다른 프래그먼트에서 데이터 변경 요청을 받을 때 처리
        setFragmentResultListener("updateStats") { _, _ ->
            Log.d("StatsFragment", "🟢 setFragmentResultListener 호출됨 - 데이터 갱신 시작")
            requireActivity().runOnUiThread {
                loadSpendingDataFromDatabase()
            }
        }
    }

    // RecyclerView 설정
    private fun setupRecyclerView() {
        spendingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        spendingRecyclerView.adapter = SpendingAdapter(spendingList)
    }

    // 데이터베이스에서 지출 내역 불러오기
    private fun loadSpendingDataFromDatabase() {
        val expenses = dbManager.getAllExpenses() // 🔥 날짜 조건 없이 전체 데이터 불러오기

        Log.d("StatsFragment", "🔵 불러온 전체 지출 개수: ${expenses.size}")

        currentSpending = 0
        spendingList.clear()

        for (expense in expenses) {
            Log.d("StatsFragment", "🟣 불러온 지출 항목: ${expense.category}, ${expense.amount} 원, 날짜: ${expense.date}")
            if (expense.transactionType == "지출") {
                spendingList.add(expense)
                currentSpending += expense.amount.toInt()
            }
        }

        requireActivity().runOnUiThread {
            spendingRecyclerView.adapter?.notifyDataSetChanged()
            updateSpendingText()
            updatePieChart()
        }
    }

    // 현재 지출 정보 업데이트
    private fun updateSpendingText() {
        currentSpendingText.text = "현재 지출: ${currentSpending}원 | 한도: ${spendingLimit}원"
    }

    // 지출 내역을 바탕으로 파이 차트 업데이트
    private fun updatePieChart() {
        val categoryMap = spendingList.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        if (categoryMap.isEmpty()) {
            Log.d("StatsFragment", "🔴 카테고리별 데이터가 없음. 차트를 업데이트하지 않습니다.")
            pieChart.clear()
            pieChart.invalidate()
            return
        }

        val entries = categoryMap.map { PieEntry(it.value.toFloat(), it.key) }
        if (entries.isEmpty()) {
            Log.d("StatsFragment", "🔴 PieChart 데이터가 없음.")
            pieChart.clear()
            pieChart.invalidate()
            return
        }

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