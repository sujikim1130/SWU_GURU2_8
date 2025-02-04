package com.example.guru_8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.fragment.ExpenseFragment
import com.example.guru_8.fragment.MainCalenderFragment
import com.example.guru_8.fragment.StatsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

// 프래그먼트를 관리하는 메인 액티비티
class FragmentActivity : AppCompatActivity() {

    private var selectedDate: String? = null // ✅ 선택한 날짜를 저장하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 초기 화면 설정 (MainCalenderFragment로 시작)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainCalenderFragment.newInstance(selectedDate))
                .commit()
        }

        // 하단 네비게이션 바 클릭 리스너 설정
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment = when (menuItem.itemId) {
                R.id.nav_home -> MainCalenderFragment.newInstance(selectedDate) // ✅ 수정됨 (선택한 날짜 반영)
                R.id.nav_stats -> ExpenseFragment.newInstance(selectedDate) // ✅ 수정됨 (선택한 날짜 반영)
                R.id.nav_settings -> StatsFragment() // 설정 화면
                else -> null
            }

            // 선택된 프래그먼트 표시
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
            true
        }
    }

    // ✅ 날짜를 저장하는 함수
    fun setSelectedDate(date: String) {
        selectedDate = date
    }

    // ✅ 저장된 날짜를 반환하는 함수
    fun getSelectedDate(): String? {
        return selectedDate
    }
}