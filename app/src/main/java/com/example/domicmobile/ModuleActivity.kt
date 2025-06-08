package com.example.domicmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.domicmobile.ApiDomic.DomicApiClient
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

private var client: DomicApiClient = DomicApiClient.getInstance()

class ModuleActivity : AppCompatActivity() {

    private suspend fun createCourse(courseId: Int) {
        val course = client.getCourse(courseId)

        Log.d("MyTag", course.toString())

        val moduleContainer = findViewById<LinearLayout>(R.id.modules)

        var courseName = findViewById<TextView>(R.id.course_name)
        courseName.text = course.name
        moduleContainer.visibility = View.VISIBLE
        for (i in 0 until course.modules.size) {
            // Create a horizontal LinearLayout to hold button and ProgressBar
            val buttonContainer = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                val marginInPx = (15 * resources.displayMetrics.density).toInt()
                setPadding(0, marginInPx, 0, 0)
            }

            val moduleButton = Button(this, null, 0, R.style.moduleBn).apply {
                text = course.modules[i].name
                isEnabled = !course.modules[i].blocked
                background = getDrawable(
                    if (course.modules[i].blocked) R.drawable.shape_module_blocked
                    else R.drawable.shape_module
                )
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f // Weight to fill remaining space
                )
            }

            if (!course.modules[i].blocked) {
                moduleButton.setOnClickListener {
                    val intent = Intent(this, ModuleActivity::class.java)
                    intent.putExtra("type", "module")
                    intent.putExtra("moduleId", course.modules[i].id.toInt())
                    intent.putExtra("courseId", course.id.toInt())
                    startActivity(intent)
                }
            }

            buttonContainer.addView(moduleButton)

            // Add vertical ProgressBar if markable
            if (course.modules[i].markable) {
                val progressBar = ProgressBar(this, null, 0, R.style.VerticalProgressBar).apply {
                    progress = course.modules[i].progress
                    layoutParams = LinearLayout.LayoutParams(
                        (4 * resources.displayMetrics.density).toInt(), // 4dp width
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                }
                buttonContainer.addView(progressBar)
            }

            moduleContainer.addView(buttonContainer)
        }
    }

    private suspend fun createModule(courseId: Int, moduleId: Int) {
        val course = client.getCourse(courseId)
        val module = client.getModule(courseId, moduleId)

        Log.d("MyTag", module.toString())

        var courseName = findViewById<TextView>(R.id.course_name)
        courseName.text = course.name

        if (module.nested_modules.isNotEmpty()) {
            val moduleContainer = findViewById<LinearLayout>(R.id.modules)
            moduleContainer.visibility = View.VISIBLE
            for (i in 0 until module.nested_modules.size) {
                Log.d("MyTag", module.nested_modules[i].toString())

                // Create a horizontal LinearLayout to hold button and ProgressBar
                val buttonContainer = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.HORIZONTAL
                    val marginInPx = (15 * resources.displayMetrics.density).toInt()
                    setPadding(0, marginInPx, 0, 0)
                }

                val moduleButton = Button(this, null, 0, R.style.moduleBn).apply {
                    text = module.nested_modules[i].name
                    isEnabled = !module.nested_modules[i].blocked
                    background = getDrawable(
                        if (module.nested_modules[i].blocked) R.drawable.shape_module_blocked
                        else R.drawable.shape_module
                    )
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f // Weight to fill remaining space
                    )
                }

                if (!module.nested_modules[i].blocked) {
                    moduleButton.setOnClickListener {
                        val intent = Intent(this, ModuleActivity::class.java)
                        intent.putExtra("type", "module")
                        intent.putExtra("courseId", courseId)
                        intent.putExtra("moduleId", module.nested_modules[i].id.toInt())
                        startActivity(intent)
                    }
                }

                buttonContainer.addView(moduleButton)

                // Add vertical ProgressBar if markable
                if (module.nested_modules[i].markable) {
                    val progressBar = ProgressBar(this, null, 0, R.style.VerticalProgressBar).apply {
                        progress = module.nested_modules[i].progress
                        layoutParams = LinearLayout.LayoutParams(
                            (4 * resources.displayMetrics.density).toInt(), // 4dp width
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                    }
                    buttonContainer.addView(progressBar)
                }

                moduleContainer.addView(buttonContainer)
            }
        }
        if (module.units.isNotEmpty()) {
            val unitContainer = findViewById<LinearLayout>(R.id.units)
            unitContainer.visibility = View.VISIBLE
            for (i in 0 until module.units.size) {
                Log.d("MyTag", client.getUnit(courseId, moduleId, module.units[i].id.toInt()).toString())

                // Create a horizontal LinearLayout to hold button and ProgressBar
                val buttonContainer = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.HORIZONTAL
                    val marginInPx = (15 * resources.displayMetrics.density).toInt()
                    setPadding(0, marginInPx, 0, 0)
                }

                val unitButton = Button(this, null, 0, R.style.moduleBn).apply {
                    text = module.units[i].name
                    isEnabled = !module.units[i].blocked
                    background = getDrawable(
                        if (module.units[i].blocked) R.drawable.shape_module_blocked
                        else R.drawable.shape_module
                    )
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f // Weight to fill remaining space
                    )
                }

                if (!module.units[i].blocked) {
                    unitButton.setOnClickListener {
                        val intent = Intent(this, UnitActivity::class.java)
                        intent.putExtra("courseId", courseId)
                        intent.putExtra("moduleId", moduleId)
                        intent.putExtra("unitId", module.units[i].id.toInt())
                        startActivity(intent)
                    }
                }

                buttonContainer.addView(unitButton)

                // Add vertical ProgressBar if markable
                if (module.units[i].markable) {
                    val progressBar = ProgressBar(this, null, 0, R.style.VerticalProgressBar).apply {
                        progress = module.units[i].progress
                        layoutParams = LinearLayout.LayoutParams(
                            (4 * resources.displayMetrics.density).toInt(), // 4dp width
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                    }
                    buttonContainer.addView(progressBar)
                }

                unitContainer.addView(buttonContainer)
            }
        }
    }

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_module)

        lifecycleScope.launch {
            try {
                if (intent.getStringExtra("type") == "course")
                    createCourse(intent.getIntExtra("courseId", 0))
                else if (intent.getStringExtra("type") == "module")
                    createModule(intent.getIntExtra("courseId", 0), intent.getIntExtra("moduleId", 0))
            } catch (e: Exception) {
                Log.e("MyTag", "Ошибка при получении курса: ${e.message}")
            }
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
                    val intent = Intent(this,ChatActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_dang -> {
                    val intent = Intent(this,DeptActivity::class.java)
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