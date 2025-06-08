package com.example.domicmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    private lateinit var chatContainer: LinearLayout
    private val apiClient = ApiDomic.DomicApiClient.getInstance()
    private lateinit var drawerLayout: DrawerLayout
    private var client = ApiDomic.DomicApiClient.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val menuBn = findViewById<ImageButton>(R.id.menu_bn)
        menuBn.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        navView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_chat -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_dang -> {
                    val intent = Intent(this, DeptActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
        val imageButton5 = findViewById<ImageButton>(R.id.imageButton5)
        imageButton5.setOnClickListener { view ->
            val wrapper = ContextThemeWrapper(this, R.style.menuBn)
            val popupMenu = PopupMenu(wrapper, view)
            popupMenu.menuInflater.inflate(R.menu.menu_image_button, popupMenu.menu)


            // Handle logout action
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_logout -> {
                        logout()
                        true
                    }
                    else -> false
                }
            }
            lifecycleScope.launch {
                try {
                    val user = client.getUser()
                    popupMenu.menu.findItem(R.id.menu_profile)?.title = user.shorted_name

                }catch (e: Exception) {
                    Log.e("MyTag", "Ошибка при получении пользователя: ${e.message}")
                }
            }
            popupMenu.show()
        }

    }
    private fun logout() {
        val sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("auth_token")
            remove("remember_me")
            apply()
        }
        client.clearUserToken() // Assuming DomicApiClient has a method to clear the token
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}