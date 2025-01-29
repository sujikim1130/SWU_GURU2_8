package com.example.guru_8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.fragment.ExpenseFragment
import com.example.guru_8.fragment.MainCalenderFragment
import com.example.guru_8.fragment.StatsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_for_fragment)

        // BottomNavigationView와 연결
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 첫 화면으로 홈 프래그먼트 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainCalenderFragment())
            .commit()

        // 탭 선택 이벤트 처리
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment = when (menuItem.itemId) {
                R.id.nav_home -> MainCalenderFragment()
                R.id.nav_stats -> ExpenseFragment()
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
}
