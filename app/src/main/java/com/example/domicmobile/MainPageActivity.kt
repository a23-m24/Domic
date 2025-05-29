package com.example.domicmobile

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView

class MainPageActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        // Инициализация DrawerLayout и NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        // Настройка кнопки меню
        val menuBn = findViewById<ImageButton>(R.id.menu_bn)
        menuBn.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        // Обработка кликов в меню
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

            }
            drawerLayout.closeDrawer(navView)
            true
        }
    }
}