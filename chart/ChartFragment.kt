package com.example.guru2

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ChartFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var currentSpendingText: TextView
    private lateinit var limitInput: EditText
    private lateinit var saveLimitButton: Button
    private lateinit var spendingRecyclerView: RecyclerView

    private lateinit var dbManager: DBManager
    private var spendingLimit = 0
    private var currentSpending = 0
    private val spendingList = mutableListOf<Pair<String, Double>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 기존 XML 레이아웃  사용
        return inflater.inflate(R.layout.charactivity_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 초기화
        pieChart = view.findViewById(R.id.pieChart)
        currentSpendingText = view.findViewById(R.id.currentSpendingText)
        limitInput = view.findViewById(R.id.limitInput)
        saveLimitButton = view.findViewById(R.id.saveLimitButton)
        spendingRecyclerView = view.findViewById(R.id.spendingRecyclerView)

        // DBManager 초기화
        dbManager = DBManager(requireContext())

        // SharedPreferences 설정
        val sharedPreferences = requireContext().getSharedPreferences("SpendingPrefs", AppCompatActivity.MODE_PRIVATE)
        spendingLimit = sharedPreferences.getInt("spendingLimit", 0)
        updateSpendingText()

        // 한도 저장 버튼 클릭 리스너
        saveLimitButton.setOnClickListener {
            val limitText = limitInput.text.toString()
            if (limitText.isNotEmpty() && limitText.toIntOrNull() != null && limitText.toInt() >= 0) {
                spendingLimit = limitText.toInt()
                sharedPreferences.edit().putInt("spendingLimit", spendingLimit).apply()
                Toast.makeText(requireContext(), "한도가 저장되었습니다: ${spendingLimit}원", Toast.LENGTH_SHORT).show()

                if (currentSpending > spendingLimit) {
                    val excess = currentSpending - spendingLimit
                    Toast.makeText(requireContext(), "경고: 현재 지출이 한도를 초과했습니다! 초과 금액: ${excess}원", Toast.LENGTH_LONG).show()
                    currentSpendingText.setTextColor(Color.RED)
                } else {
                    currentSpendingText.setTextColor(Color.BLACK)
                }
                updateSpendingText()
            } else {
                Toast.makeText(requireContext(), "한도를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // RecyclerView 설정
        setupRecyclerView()

        // 데이터베이스에서 데이터 불러옴
        loadSpendingDataFromDatabase()
    }

    private fun setupRecyclerView() {
        spendingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        spendingRecyclerView.adapter = SpendingAdapter(spendingList)
    }

    private fun loadSpendingDataFromDatabase() {
        val expenses = dbManager.getAllExpenses()
        currentSpending = 0
        spendingList.clear()

        for (expense in expenses) {
            if (expense.transactionType == "expense") {
                spendingList.add(Pair(expense.detail, expense.amount))
                currentSpending += expense.amount.toInt()
            }
        }

        spendingRecyclerView.adapter?.notifyDataSetChanged()
        updateSpendingText()
        updatePieChart()

        Log.d("ChartFragment", "Current Spending: $currentSpending")
        Log.d("ChartFragment", "Spending List: $spendingList")
    }

    private fun updateSpendingText() {
        currentSpendingText.text = "현재 지출: ${currentSpending}원 / 한도: ${spendingLimit}원"
    }

    private fun updatePieChart() {
        val entries = spendingList.map { PieEntry(it.second.toFloat(), it.first) }
        val dataSet = PieDataSet(entries, "지출 비중")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.centerText = "월별 지출"
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}
