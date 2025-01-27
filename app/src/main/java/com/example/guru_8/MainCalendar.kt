import com.example.guru_8.R
import com.example.guru_8.fragment.HomeFragment

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class MainCalendar : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var progressBar: ProgressBar
    private lateinit var diaryTextView: TextView
    private lateinit var currentSpendingTextView: TextView

    private var spendingLimit = 0
    private var currentSpending = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_calendar)

        // 뷰 초기화
        calendarView = findViewById(R.id.calendarView)
        progressBar = findViewById(R.id.spendingProgressBar)
        diaryTextView = findViewById(R.id.diaryTextView)
        currentSpendingTextView = findViewById(R.id.currentSpendingTextView)

        // ChartActivity에서 데이터 가져오기
        spendingLimit = intent.getIntExtra("spendingLimit", 0)
        currentSpending = intent.getIntExtra("currentSpending", 0)

        // ProgressBar와 텍스트 업데이트
        updateProgressBar()

        // 달력 날짜 선택 이벤트
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            navigateToFragment(HomeFragment.newInstance(selectedDate))
        }
    }

    private fun updateProgressBar() {
        if (spendingLimit > 0) {
            val progress = (currentSpending.toDouble() / spendingLimit * 100).toInt()
            progressBar.max = 100
            progressBar.progress = progress
        }

        // 지출 상태 업데이트
        currentSpendingTextView.text = getString(
            R.string.current_spending_status,
            currentSpending,
            spendingLimit
        )
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }
}