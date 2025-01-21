package com.example.guru2

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
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

    private lateinit var sharedPreferences: SharedPreferences
    private var spendingLimit = 0
    private var currentSpending = 0
    private val spendingList = mutableListOf<Pair<String, Int>>() // 예: ("식비", 5000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.charactivity_main)

        // 뷰 초기화
        pieChart = findViewById(R.id.pieChart)
        currentSpendingText = findViewById(R.id.currentSpendingText)
        limitInput = findViewById(R.id.limitInput)
        saveLimitButton = findViewById(R.id.saveLimitButton)
        spendingRecyclerView = findViewById(R.id.spendingRecyclerView)

        // SharedPreferences 설정
        sharedPreferences = getSharedPreferences("SpendingPrefs", MODE_PRIVATE)
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
                    // 초과가 아니면 텍스트 색상을 초기화
                    currentSpendingText.setTextColor(Color.BLACK)
                }

                updateSpendingText()
            } else {
                Toast.makeText(this, "유효한 한도를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // RecyclerView 설정
        setupRecyclerView()

        // 예제 데이터 추가
        addSpending("식비", 50000)
        addSpending("교통비", 15000)
        addSpending("쇼핑", 100000)
    }

    // RecyclerView 초기화
    private fun setupRecyclerView() {
        spendingRecyclerView.layoutManager = LinearLayoutManager(this)
        spendingRecyclerView.adapter = SpendingAdapter(spendingList)
    }

    // 지출 추가 함수
    private fun addSpending(category: String, amount: Int) {
        spendingList.add(Pair(category, amount))
        currentSpending += amount
        spendingRecyclerView.adapter?.notifyDataSetChanged()
        updateSpendingText()
        updatePieChart()

        // 한도 초과 경고
        if (spendingLimit > 0 && currentSpending > spendingLimit) {
            val excess = currentSpending - spendingLimit
            Toast.makeText(this, "경고: 지출 한도를 초과했습니다! 초과 금액: ${excess}원", Toast.LENGTH_LONG).show()
            currentSpendingText.setTextColor(Color.RED)
        }
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
