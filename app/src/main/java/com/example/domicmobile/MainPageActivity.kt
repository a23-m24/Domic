package com.example.domicmobile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.domicmobile.ApiDomic.DomicApiClient
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

private lateinit var client: DomicApiClient

class MainPageActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        val homePage: LinearLayout = findViewById(R.id.home_page)
        val coursesPage: LinearLayout = findViewById(R.id.courses_page)
        val dangPage: LinearLayout = findViewById(R.id.dang_page)
        val cahtPage: LinearLayout = findViewById(R.id.chat_page)
        val settingsPage: LinearLayout = findViewById(R.id.settings_page)
        client = DomicApiClient.getInstance()
        lifecycleScope.launch {
            try {
                val user = client.getUser()
                val deadlines = client.getStudentDeadlines()
                val courses = client.getStudentCourses()
                if (deadlines.deadlines.isEmpty() == false){
                    findViewById<LinearLayout>(R.id.deadlines).visibility = View.VISIBLE
                    for (i in deadlines.deadlines.size..0 step -1){
                        val deadlineContainer = findViewById<LinearLayout>(R.id.deadlines)
                        val deadlineText = TextView(this@MainPageActivity)
                        var value: String = deadlines.deadlines[i].date + " (" + deadlines.deadlines[i].coefficient.toString() + ") - " + deadlines.deadlines[i].course_name
                        if(deadlines.deadlines[i].unit_name != null)
                            value += " " + deadlines.deadlines[i].unit_name
                        deadlineText.text = value

                        deadlineText.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        deadlineContainer.addView(deadlineText)
                    }
                }
                for (i in 0..courses.courses.size){
                    val courseContainer = findViewById<LinearLayout>(R.id.courses)
                    val courseButton = Button(this@MainPageActivity, null, 0, R.style.moduleBn)
                    courseButton.text = courses.courses[i].name
                    courseButton.background = getDrawable(R.drawable.shape_module)

                    courseButton.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    courseContainer.addView(courseButton)
                }
            } catch (e: Exception) {
                Log.e("MyTag", "Ошибка при получении пользователя: ${e.message}")
            }
        }
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val menuBn = findViewById<ImageButton>(R.id.menu_bn)
        menuBn.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        fun onOptionsItemSelected(item: MenuItem): Boolean {

            when (item.itemId) {
                R.id.nav_home -> {

                    return true
                }
                R.id.nav_chat -> {
                    // Обработка нажатия "Долги по курсам"
                    return true
                }
                R.id.nav_dang -> {
                    // Обработка нажатия "Мессенджер"
                    return true
                }
                R.id.nav_courses -> {
                    // Обработка нажатия "Хранилище файлов"
                    return true
                }
                R.id.nav_settings -> {
                    // Обработка нажатия "Настройки"
                    return true
                }
                else -> return super.onOptionsItemSelected(item)
            }
        }
    }
}