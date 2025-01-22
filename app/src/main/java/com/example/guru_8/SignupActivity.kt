package com.example.guru_8

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 버튼 클릭 이벤트
        binding.btnSignup.setOnClickListener {
            // 회원가입 로직 추가 (예: Firebase 인증)
        }

        // 로그인으로 돌아가기 버튼 클릭 이벤트
        binding.btnGoToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
