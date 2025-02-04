package com.example.guru_8

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.databinding.ActivityMainBinding

// 앱의 메인 액티비티 (로그인 화면 역할)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // ViewBinding 사용

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 버튼 클릭 이벤트
        binding.btnLogin.setOnClickListener {
            // 간단한 로그인 완료 메시지 출력
            Toast.makeText(this, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()

            // FragmentActivity로 화면 전환
            val intent = Intent(this, FragmentActivity::class.java)
            startActivity(intent)
        }

        // 회원가입 화면으로 이동하는 버튼 클릭 이벤트
        binding.btnGoToSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}