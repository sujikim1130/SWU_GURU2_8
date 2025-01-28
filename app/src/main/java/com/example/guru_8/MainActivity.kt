package com.example.guru_8

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 버튼 클릭 이벤트
        binding.btnLogin.setOnClickListener {
            // 간단한 로그인 완료 메시지
            Toast.makeText(this, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()

            // FragmentActivity로 전환
            val intent = Intent(this, FragmentActivity::class.java)
            startActivity(intent)
        }

        // 회원가입으로 이동 버튼 클릭 이벤트
        binding.btnGoToSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
