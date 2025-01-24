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
            val personal_id = binding.etpersonalid.text.toString()
            val username = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (personal_id.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isUserExists(personal_id, email)) {
                Toast.makeText(this, "이미 존재하는 사용자입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!email.contains("@")) {
                Toast.makeText(this, "유효한 이메일 주소를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "비밀번호는 최소 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = saveUserToDatabase(personal_id, username, email, password)
            if (success) {
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                Log.e("SignupActivity", "데이터베이스 삽입 실패: $personal_id, $username, $email")
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUserToDatabase(personal_id: String, username: String, email: String, password: String): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_ID, personal_id)
            put(DatabaseHelper.COLUMN_USERNAME, username)
            put(DatabaseHelper.COLUMN_EMAIL, email)
            put(DatabaseHelper.COLUMN_PASSWORD, password)
        }

        val newRowId = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        return newRowId != -1L
    }

    private fun isUserExists(personal_id: String, email: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COLUMN_USER_ID, DatabaseHelper.COLUMN_EMAIL),
            "${DatabaseHelper.COLUMN_USER_ID} = ? OR ${DatabaseHelper.COLUMN_EMAIL} = ?",
            arrayOf(personal_id, email),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}
