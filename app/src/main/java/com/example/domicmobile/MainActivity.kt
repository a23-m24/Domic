package com.example.domicmobile


import android.content.Intent
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


private lateinit var client: DomicApiClient

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val rememberMeCheckBox = findViewById<CheckBox>(R.id.rememberMeCheckBox)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPasswordButton = findViewById<Button>(R.id.forgotPasswordButton)

        client = DomicApiClient.getInstance()

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            lifecycleScope.launch {
                val token = client.login(username, password)
                if (token != null) {
                    val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d("MyTag","Вход не успешен" )
                    Toast.makeText(this@MainActivity, "Вход не удался", Toast.LENGTH_SHORT).show()
                }
            }
        }

        forgotPasswordButton.setOnClickListener {

        }
    }
}