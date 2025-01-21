package com.example.yourapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_8.R

class LoginActivity : AppCompatActivity() {

    private lateinit var tvPasswordPrompt: TextView
    private lateinit var tvPasswordDisplay: TextView
    private val passwordBuilder = StringBuilder()
    private val correctPassword = "1234" // 올바른 비밀번호 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvPasswordPrompt = findViewById(R.id.tvPasswordPrompt)
        tvPasswordDisplay = findViewById(R.id.tvPasswordDisplay)

        val buttons = listOf(
            findViewById<Button>(R.id.btn0),
            findViewById<Button>(R.id.btn1),
            findViewById<Button>(R.id.btn2),
            findViewById<Button>(R.id.btn3),
            findViewById<Button>(R.id.btn4),
            findViewById<Button>(R.id.btn5),
            findViewById<Button>(R.id.btn6),
            findViewById<Button>(R.id.btn7),
            findViewById<Button>(R.id.btn8),
            findViewById<Button>(R.id.btn9)
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                if (passwordBuilder.length < 4) { // 최대 4자리 제한
                    passwordBuilder.append(button.text)
                    updatePasswordDisplay()

                    if (passwordBuilder.length == 4) {
                        checkPassword()
                    }
                }
            }
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            passwordBuilder.clear()
            updatePasswordDisplay()
        }
    }

    private fun updatePasswordDisplay() {
        tvPasswordDisplay.text = passwordBuilder.toString()
    }

    private fun checkPassword() {
        val enteredPassword = passwordBuilder.toString()
        if (enteredPassword == correctPassword) {
            // 비밀번호가 맞을 경우
            Toast.makeText(this, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            tvPasswordPrompt.text = "로그인이 완료되었습니다."
        } else {
            // 비밀번호가 틀렸을 경우
            Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
            tvPasswordPrompt.text = "비밀번호가 틀렸습니다."
        }
        passwordBuilder.clear() // 비밀번호 초기화
        updatePasswordDisplay()
    }
}
