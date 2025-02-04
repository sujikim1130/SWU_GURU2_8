package com.example.guru_8.fragment

import android.os.Bundle
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

/**
 * 사용자의 지출 통계를 시각적으로 보여주는 프래그먼트
 */
class StatsFragment : Fragment() {

    private lateinit var pieChart: PieChart // 카테고리별 지출을 나타내는 원형 차트
    private lateinit var currentSpendingText: TextView // 현재 지출 금액 표시
    private lateinit var limitInput: EditText // 지출 한도 입력 필드
    private lateinit var saveLimitButton: Button // 한도 저장 버튼
    private lateinit var spendingRecyclerView: RecyclerView // 지출 목록 RecyclerView

    private lateinit var dbManager: DataBaseHelper // 데이터베이스 관리자
    private var spendingLimit = 0 // 설정된 지출 한도
    private var currentSpending = 0 // 현재 총 지출 금액
    private val spendingList = mutableListOf<Expense>() // 지출 내역 리스트

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

        // 한도 저장 버튼 클릭 이벤트 설정
        saveLimitButton.setOnClickListener {
            val limitText = limitInput.text.toString()
            if (limitText.isNotEmpty() && limitText.toIntOrNull() != null) {
                spendingLimit = limitText.toInt()
                sharedPreferences.edit().putInt("spendingLimit", spendingLimit).apply()
                Toast.makeText(requireContext(), "한도가 저장되었습니다: ${spendingLimit}원", Toast.LENGTH_SHORT).show()
                updateSpendingText()
            } else {
                Toast.makeText(requireContext(), "유효한 한도를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        setupRecyclerView()
        loadSpendingDataFromDatabase()

        // 다른 프래그먼트에서 데이터 갱신 요청을 받을 경우 처리
        setFragmentResultListener("updateStats") { _, _ ->
            requireActivity().runOnUiThread {
                loadSpendingDataFromDatabase()
            }
        }
    }

    /**
     * RecyclerView 설정 함수
     */
    private fun setupRecyclerView() {
        spendingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        spendingRecyclerView.adapter = SpendingAdapter(spendingList)
    }

    /**
     * 데이터베이스에서 지출 내역을 가져와 리스트 및 차트를 갱신하는 함수
     */
    private fun loadSpendingDataFromDatabase() {
        val expenses = dbManager.getAllExpenses()

        currentSpending = 0
        spendingList.clear()

        for (expense in expenses) {
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

    /**
     * 현재 지출 금액을 UI에 업데이트하는 함수
     */
    private fun updateSpendingText() {
        currentSpendingText.text = "현재 지출: ${currentSpending}원 | 한도: ${spendingLimit}원"
    }

    /**
     * 원형 차트를 업데이트하는 함수
     */
    private fun updatePieChart() {
        val categoryMap = spendingList.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        if (categoryMap.isEmpty()) {
            pieChart.clear()
            pieChart.invalidate()
            return
        }

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
