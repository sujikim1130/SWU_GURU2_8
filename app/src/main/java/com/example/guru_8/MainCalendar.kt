package com.example.guru_8

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.R

class MainCalendar : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var diaryTextView: TextView
    private lateinit var contextEditText: EditText
    private lateinit var textView2: TextView
    private lateinit var saveBtn: Button
    private lateinit var chaBtn: Button
    private lateinit var delBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_calendar)

        // 뷰 초기화
        calendarView = findViewById(R.id.calendarView)
        diaryTextView = findViewById(R.id.diaryTextView)
        contextEditText = findViewById(R.id.contextEditText)
        textView2 = findViewById(R.id.textView2)
        saveBtn = findViewById(R.id.save_Btn)
        chaBtn = findViewById(R.id.cha_Btn)
        delBtn = findViewById(R.id.del_Btn)

        // 초기 visibility 설정
        textView2.visibility = View.GONE
        chaBtn.visibility = View.GONE
        delBtn.visibility = View.GONE

        // 달력 날짜 선택 이벤트
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            diaryTextView.text = "선택된 날짜: $year-${month + 1}-$dayOfMonth"
            contextEditText.text.clear()
            textView2.visibility = View.GONE
            saveBtn.visibility = View.VISIBLE
            chaBtn.visibility = View.GONE
            delBtn.visibility = View.GONE
        }

        // 저장 버튼 클릭
        saveBtn.setOnClickListener {
            val content = contextEditText.text.toString()
            textView2.text = content
            textView2.visibility = View.VISIBLE
            contextEditText.visibility = View.GONE
            saveBtn.visibility = View.GONE
            chaBtn.visibility = View.VISIBLE
            delBtn.visibility = View.VISIBLE
        }

        // 수정 버튼 클릭
        chaBtn.setOnClickListener {
            contextEditText.visibility = View.VISIBLE
            textView2.visibility = View.GONE
            saveBtn.visibility = View.VISIBLE
            chaBtn.visibility = View.GONE
            delBtn.visibility = View.GONE
        }

        // 삭제 버튼 클릭
        delBtn.setOnClickListener {
            contextEditText.text.clear()
            textView2.text = ""
            textView2.visibility = View.GONE
            contextEditText.visibility = View.VISIBLE
            saveBtn.visibility = View.VISIBLE
            chaBtn.visibility = View.GONE
            delBtn.visibility = View.GONE
        }
    }
}