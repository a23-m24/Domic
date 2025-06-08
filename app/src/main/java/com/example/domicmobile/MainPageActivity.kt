package com.example.domicmobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
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

    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)



        client = DomicApiClient.getInstance()
        lifecycleScope.launch {
            try {
                Log.d("MyTag", "OK")

                val user = client.getUser()
                val deadlines = client.getStudentDeadlines()
                val courses = client.getStudentCourses()
                if (deadlines.deadlines.isEmpty() == false) {
                    findViewById<LinearLayout>(R.id.deadlines).visibility = View.VISIBLE
                    for (i in deadlines.deadlines.size - 1 downTo 0) {
                        val deadlineContainer = findViewById<LinearLayout>(R.id.deadlines)
                        val deadlineText = TextView(this@MainPageActivity, null, 0, R.style.deadlineItem)
                        val courseButton = Button(this@MainPageActivity, null, 0, R.style.deadlineButton)
                        var textValue: String = deadlines.deadlines[i].date + " (" + deadlines.deadlines[i].coefficient.toString() + ")"
                        var buttonValue: String = deadlines.deadlines[i].course_name
                        if (deadlines.deadlines[i].unit_name != null) {
                            buttonValue += ". " + deadlines.deadlines[i].unit_name
                            courseButton.setOnClickListener {
                                val courseId = deadlines.deadlines[i].course_id.toInt()
                                val moduleId = deadlines.deadlines[i].module_id
                                val unitId = deadlines.deadlines[i].unit_id

                                val intent = Intent(this@MainPageActivity, UnitActivity::class.java)
                                intent.putExtra("courseId", courseId)
                                intent.putExtra("moduleId", moduleId)
                                intent.putExtra("unitId", unitId)
                                startActivity(intent)
                            }
                        } else {
                            courseButton.setOnClickListener {
                                val intent =
                                    Intent(this@MainPageActivity, ModuleActivity::class.java)
                                intent.putExtra(
                                    "courseId",
                                    deadlines.deadlines[i].course_id.toInt()
                                )
                                intent.putExtra("type", "course")
                                startActivity(intent)
                            }
                        }
                        deadlineText.text = textValue
                        courseButton.text = buttonValue
                        courseButton.background = null

                        deadlineText.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        courseButton.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        deadlineContainer.addView(deadlineText)
                        deadlineContainer.addView(courseButton)
                    }
                }
                if (courses.courses.isEmpty() == false) {
                    val courseContainer = findViewById<LinearLayout>(R.id.courses)
                    for (i in 0..courses.courses.size - 1) {
                        val courseButton = Button(this@MainPageActivity, null, 0, R.style.moduleBn)

                        courseButton.setOnClickListener {
                            val intent = Intent(this@MainPageActivity, ModuleActivity::class.java)
                            intent.putExtra("type", "course")
                            intent.putExtra("courseId", courses.courses[i].id.toInt())
                            startActivity(intent)
                        }

                        courseButton.text = courses.courses[i].name
                        courseButton.background = getDrawable(R.drawable.shape_module)

                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        val marginInPx = (15 * resources.displayMetrics.density).toInt() // 15dp в пиксели
                        layoutParams.setMargins(0, marginInPx, 0, 0) // Только верхний margin 15dp

                        courseButton.layoutParams = layoutParams
                        courseContainer.addView(courseButton)
                    }
                } else {
                }
            } catch (e: Exception) {
                Log.e("MyTag", "Ошибка при получении пользователя: ${e.message}")
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