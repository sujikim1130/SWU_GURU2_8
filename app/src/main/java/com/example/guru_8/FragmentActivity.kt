package com.example.guru_8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.fragment.ExpenseFragment
import com.example.guru_8.fragment.MainCalenderFragment
import com.example.guru_8.fragment.StatsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 *여러 프래그먼트를 관리하는 액티비티
 * 하단 네비게이션 바를 통해 사용자가 프래그먼트 간 전환
 */
class FragmentActivity : AppCompatActivity() {

    private var selectedDate: String? = null // 선택한 날짜를 저장하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 앱 실행 시 초기 화면 설정 (캘린더 프래그먼트 표시)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainCalenderFragment.newInstance(selectedDate))
                .commit()
        }

        // 네비게이션 바 아이템 클릭 리스너 설정
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment = when (menuItem.itemId) {
                R.id.nav_home -> MainCalenderFragment.newInstance(selectedDate) // 홈 화면
                R.id.nav_stats -> ExpenseFragment.newInstance(selectedDate) // 지출 내역 화면
                R.id.nav_settings -> StatsFragment() // 통계 화면
                else -> null
            }
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
            true
        }
    }

    /**
     * 선택한 날짜를 저장하는 함수
     */
    fun setSelectedDate(date: String) {
        selectedDate = date
    }

    /**
     * 선택한 날짜를 반환하는 함수
     */
    fun getSelectedDate(): String? {
        return selectedDate
    }
}
