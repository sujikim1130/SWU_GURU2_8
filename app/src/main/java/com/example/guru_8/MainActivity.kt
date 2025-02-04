package com.example.guru_8

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.databinding.ActivityMainBinding

/**
 * 앱의 시작 화면으로 로그인 및 회원가입 기능을 제공
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // View Binding 사용

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 버튼 클릭 이벤트
        binding.btnLogin.setOnClickListener {
            Toast.makeText(this, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()

            // FragmentActivity로 이동하여 메인 화면 표시
            val intent = Intent(this, FragmentActivity::class.java)
            startActivity(intent)
        }

        // 회원가입 버튼 클릭 이벤트
        binding.btnGoToSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
