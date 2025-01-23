package com.example.guru_8

import android.graphics.Color
import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ChartActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var currentSpendingText: TextView
    private lateinit var limitInput: EditText
    private lateinit var saveLimitButton: Button
    private lateinit var spendingRecyclerView: RecyclerView


    private lateinit var dbManager: DBManager
    private var spendingLimit = 0
    private var currentSpending = 0
    private val spendingList = mutableListOf<Pair<String, Double>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        // 뷰 초기화
        pieChart = findViewById(R.id.pieChart)
        currentSpendingText = findViewById(R.id.currentSpendingText)
        limitInput = findViewById(R.id.limitInput)
        saveLimitButton = findViewById(R.id.saveLimitButton)
        spendingRecyclerView = findViewById(R.id.spendingRecyclerView)

        // DBManager 초기화
        dbManager = DBManager(this)

        // SharedPreferences 설정
        val sharedPreferences = getSharedPreferences("SpendingPrefs", MODE_PRIVATE)
        spendingLimit = sharedPreferences.getInt("spendingLimit", 0)
        updateSpendingText()

        // 한도 저장 버튼 클릭 리스너
        saveLimitButton.setOnClickListener {
            val limitText = limitInput.text.toString()
            if (limitText.isNotEmpty() && limitText.toIntOrNull() != null && limitText.toInt() >= 0) {
                spendingLimit = limitText.toInt()
                sharedPreferences.edit().putInt("spendingLimit", spendingLimit).apply()
                Toast.makeText(this, "한도가 저장되었습니다: ${spendingLimit}원", Toast.LENGTH_SHORT).show()

                // 한도 변경 후 현재 지출이 초과되었는지 확인
                if (currentSpending > spendingLimit) {
                    val excess = currentSpending - spendingLimit
                    Toast.makeText(this, "경고: 현재 지출이 한도를 초과했습니다! 초과 금액: ${excess}원", Toast.LENGTH_LONG).show()
                    currentSpendingText.setTextColor(Color.RED)
                } else {
                    currentSpendingText.setTextColor(Color.BLACK)
                }

                updateSpendingText()
            } else {
                Toast.makeText(this, "한도를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // RecyclerView 설정
        setupRecyclerView()

        // 데이터베이스에서 데이터 불러오기
        loadSpendingDataFromDatabase()
    }

    // RecyclerView 초기화
    private fun setupRecyclerView() {
        spendingRecyclerView.layoutManager = LinearLayoutManager(this)
        spendingRecyclerView.adapter = SpendingAdapter(spendingList)
    }

    // 데이터베이스에서 지출 데이터를 불러오는 함수
    private fun loadSpendingDataFromDatabase() {
        val expenses = dbManager.getAllExpenses()
        currentSpending = 0
        spendingList.clear()

        for (expense in expenses) {
            if (expense.transactionType == "expense") {  // 모든 지출 항목을 처리
                spendingList.add(Pair(expense.detail, expense.amount))
                currentSpending += expense.amount.toInt()
            }
        }

        spendingRecyclerView.adapter?.notifyDataSetChanged()
        updateSpendingText()
        updatePieChart()

        Log.d("ChartActivity", "Current Spending: $currentSpending")
        Log.d("ChartActivity", "Spending List: $spendingList")
    }

    // 현재 지출 텍스트 업데이트
    private fun updateSpendingText() {
        currentSpendingText.text = "현재 지출: ${currentSpending}원 / 한도: ${spendingLimit}원"
    }

    // 원형 차트 업데이트
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
