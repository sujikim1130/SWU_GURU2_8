package com.example.mycalendar2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MonthAdapter : RecyclerView.Adapter<MonthAdapter.Month>() {

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Month {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.month_item, parent, false)
        return Month(view)
    }

    override fun onBindViewHolder(holder: Month, position: Int) {
        val listLayout: RecyclerView = holder.view.findViewById(R.id.month_recycler)
        val titleText: TextView = holder.view.findViewById(R.id.title)
        val addButton: Button = holder.view.findViewById(R.id.add)

        // 달력 초기화
        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.time = Date()
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        tempCalendar.add(Calendar.MONTH, position - Int.MAX_VALUE / 2)

        // 타이틀 설정
        titleText.text = "${tempCalendar.get(Calendar.YEAR)}년 ${tempCalendar.get(Calendar.MONTH) + 1}월"

        // 날짜 리스트 생성
        val dayList = generateDayList(tempCalendar)

        // RecyclerView 설정
        listLayout.layoutManager = GridLayoutManager(holder.view.context, 7)
        listLayout.adapter = DayAdapter(tempCalendar.get(Calendar.MONTH), dayList)
    }

    private fun generateDayList(calendar: Calendar): List<Date> {
        val dayList = mutableListOf<Date>()

        // 1일의 요일 계산
        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)
        tempCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek + 1)

        // 6주간 날짜 추가
        for (i in 0 until 42) { // 6주 * 7일
            dayList.add(tempCalendar.time)
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dayList
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    class Month(val view: View) : RecyclerView.ViewHolder(view)
}