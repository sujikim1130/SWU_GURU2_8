package com.example.financialledgerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 액티비티에서 프래그먼트를 동적으로 추가
        if (savedInstanceState == null) {
            val fragment = ExpenseFragment() // ExpenseFragment 인스턴스 생성
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // 프래그먼트를 표시할 컨테이너
                .commit()
        }
    }
}
