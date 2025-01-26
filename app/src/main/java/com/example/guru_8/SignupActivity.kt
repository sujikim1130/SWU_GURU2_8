package com.example.guru_8

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString()
            val nickname = binding.etNickname.text.toString()
            val phone = binding.etPhone.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

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

        binding.btnGoToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUserToDatabase(name: String, nickname: String, phone: String, email: String, password: String): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USERNAME, name)
            put(DatabaseHelper.COLUMN_NICKNAME, nickname)
            put(DatabaseHelper.COLUMN_PHONE, phone)
            put(DatabaseHelper.COLUMN_EMAIL, email)
            put(DatabaseHelper.COLUMN_PASSWORD, password)
        }

        val newRowId = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        return newRowId != -1L
    }
}
