package com.example.domicmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val rememberMeCheckBox = findViewById<CheckBox>(R.id.rememberMeCheckBox)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPasswordButton = findViewById<Button>(R.id.forgotPasswordButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val rememberMe = rememberMeCheckBox.isChecked


            if(username == "demo" && password == "1234"){
                val intent = Intent(this, MainPageActivity::class.java)
                startActivity(intent)
                return@setOnClickListener
            }


            when {
                username.isEmpty() -> {
                    usernameEditText.error = "Введите имя пользователя"
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    passwordEditText.error = "Введите пароль"
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    passwordEditText.error = "Пароль должен содержать минимум 6 символов"
                    return@setOnClickListener
                }
                else -> {
                    // Здесь обработка логина
                    val message = if (rememberMe) {
                        "Вход выполнен: $username (запомнить)"
                    } else {
                        "Вход выполнен: $username"
                    }
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        forgotPasswordButton.setOnClickListener {

        }
    }
}