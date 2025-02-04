package com.example.guru_8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.fragment.ExpenseFragment
import com.example.guru_8.fragment.MainCalenderFragment
import com.example.guru_8.fragment.StatsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentActivity : AppCompatActivity() {

    private var selectedDate: String? = null // ✅ 선택한 날짜를 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 초기 화면 설정
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainCalenderFragment.newInstance(selectedDate))
                .commit()
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment = when (menuItem.itemId) {
                R.id.nav_home -> MainCalenderFragment.newInstance(selectedDate) // ✅ 수정됨
                R.id.nav_stats -> ExpenseFragment.newInstance(selectedDate) // ✅ 수정됨
                R.id.nav_settings -> StatsFragment()
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

    fun setSelectedDate(date: String) { // ✅ 날짜 저장 함수 추가
        selectedDate = date
    }

    fun getSelectedDate(): String? { // ✅ 저장된 날짜 반환 함수 추가
        return selectedDate
    }
}
