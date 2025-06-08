package com.example.domicmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.domicmobile.ApiDomic.DomicApiClient
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

private lateinit var client: DomicApiClient
private lateinit var drawerLayout: DrawerLayout

class DeptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dept)

        val debts = findViewById<LinearLayout>(R.id.debts)
        client = DomicApiClient.getInstance()

        lifecycleScope.launch {
            try {
                val debt_courses = client.getStudentDebts()
                debts.removeAllViews() // Clear existing views to avoid duplicates

                for (course in debt_courses.debt_courses) {
                    // Create container for each course
                    val courseContainer = LinearLayout(this@DeptActivity).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        background = getDrawable(R.drawable.shape_module)
                        setPadding(
                            (16 * resources.displayMetrics.density).toInt(),
                            (8 * resources.displayMetrics.density).toInt(),
                            (16 * resources.displayMetrics.density).toInt(),
                            (8 * resources.displayMetrics.density).toInt()
                        )
                    }

                    // Add course name as a TextView (optional, for clarity)
                    val courseText = TextView(this@DeptActivity, null, 0, R.style.h2).apply {
                        text = course.name
                        setTextAppearance(this@DeptActivity, R.style.module)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    val courseId = course.id.toInt()
                    courseContainer.addView(courseText)

                    // Create buttons for each unit
                    for (unit in course.units) {
                        val button = Button(this@DeptActivity).apply {
                            text = "${unit.name}\nДедлайн: ${unit.date}\nОценка: ${unit.user_mark}/${unit.max_mark}"
                            setTextAppearance(this@DeptActivity, R.style.debt)
                            background = getDrawable(R.drawable.shape_debt)
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
                            }
                        }
                        button.setOnClickListener {
                            val intent = Intent(this@DeptActivity, UnitActivity::class.java)
                            intent.putExtra("courseId", courseId)
                            intent.putExtra("moduleId", unit.module_id)
                            intent.putExtra("unitId", unit.id.toInt())
                            startActivity(intent)
                        }
                        courseContainer.addView(button)
                    }

                    debts.addView(courseContainer)
                }
            } catch (e: Exception) {
                Log.e("MyTag", "Ошибка при получении данных: ${e.message}")
            }
        }

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
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