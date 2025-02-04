package com.example.guru_8

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.data.DataBaseHelper
import com.example.guru_8.databinding.ActivitySignupBinding

// 회원가입 기능을 처리하는 액티비티
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding // ViewBinding 사용
    private lateinit var dbHelper: DataBaseHelper // 데이터베이스 헬퍼 객체

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DataBaseHelper(this) // 데이터베이스 초기화

        // 회원가입 버튼 클릭 이벤트 처리
        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString()
            val nickname = binding.etNickname.text.toString()
            val phone = binding.etPhone.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // 입력 필드 검증
            if (name.isBlank() || nickname.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!phone.matches(Regex("010-\\d{4}-\\d{4}"))) {
                Toast.makeText(this, "전화번호는 010-0000-0000 형식이어야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!email.contains("@")) {
                Toast.makeText(this, "유효한 이메일 주소를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!password.matches(Regex("\\d{4}"))) {
                Toast.makeText(this, "비밀번호는 숫자 4자리여야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 데이터베이스에 사용자 정보 저장
            val success = saveUserToDatabase(name, nickname, phone, email, password)
            if (success) {
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                Log.e("SignupActivity", "데이터베이스 삽입 실패: $name, $nickname, $email")
            }
        }

        // 로그인 화면으로 이동하는 버튼 클릭 이벤트 처리
        binding.btnGoToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // 사용자 정보를 데이터베이스에 저장하는 함수
    private fun saveUserToDatabase(name: String, nickname: String, phone: String, email: String, password: String): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_USERNAME, name)
            put(DataBaseHelper.COLUMN_NICKNAME, nickname)
            put(DataBaseHelper.COLUMN_PHONE, phone)
            put(DataBaseHelper.COLUMN_EMAIL, email)
            put(DataBaseHelper.COLUMN_PASSWORD, password)
        }

        val newRowId = db.insert(DataBaseHelper.TABLE_USERS, null, values)
        return newRowId != -1L // 성공 여부 반환
    }
}

