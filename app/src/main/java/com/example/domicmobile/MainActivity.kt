package com.example.domicmobile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.domicmobile.ApiDomic.DomicApiClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var client: DomicApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем клиент перед проверкой сохраненных данных
        client = DomicApiClient.getInstance()

        // Проверяем, есть ли сохраненные данные для автоматического входа
        sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("auth_token", null)
        val rememberMe = sharedPreferences.getBoolean("remember_me", false)

        if (rememberMe && savedToken != null) {
            // Если есть сохраненный токен и стоит галочка "Запомнить меня"
            client.setUserToken(savedToken)
            navigateToMainPage()
            return
        }

        setContentView(R.layout.activity_main)
        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val rememberMeCheckBox = findViewById<CheckBox>(R.id.rememberMeCheckBox)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val rememberMe = rememberMeCheckBox.isChecked

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@MainActivity, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginButton.isEnabled = false // Отключаем кнопку во время запроса
            loginButton.text = "Вход..."

            lifecycleScope.launch {
                try {
                    val token = client.login(username, password, rememberMe)
                    if (token != null) {
                        // Сохраняем данные, если стоит галочка "Запомнить меня"
                        with(sharedPreferences.edit()) {
                            if (rememberMe) {
                                putString("auth_token", token)
                                putBoolean("remember_me", true)
                            } else {
                                remove("auth_token")
                                remove("remember_me")
                            }
                            apply()
                        }
                        navigateToMainPage()
                    } else {
                        showLoginError("Неверный логин или пароль")
                    }
                } catch (e: Exception) {
                    Log.e("LoginError", "Ошибка входа", e)
                    showLoginError("Ошибка соединения. Проверьте интернет")
                } finally {
                    loginButton.isEnabled = true
                    loginButton.text = "Войти"
                }
            }
        }
    }

    private fun navigateToMainPage() {
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoginError(message: String = "Вход не удался") {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}