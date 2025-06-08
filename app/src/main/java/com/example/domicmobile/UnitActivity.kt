package com.example.domicmobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
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

class UnitActivity : AppCompatActivity() {
    private lateinit var client: DomicApiClient
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit)

        client = DomicApiClient.getInstance()

        val courseId = intent.getIntExtra("courseId", 0)
        val moduleId = intent.getIntExtra("moduleId", 0)
        val unitId = intent.getIntExtra("unitId", 0)

        if (courseId == 0 || moduleId == 0 || unitId == 0) {
            Log.e("MyTag", "Недопустимые идентификаторы: courseId=$courseId, moduleId=$moduleId, unitId=$unitId")
            return
        }

        lifecycleScope.launch {
            try {
                val course = client.getCourse(courseId)
                val unit = client.getUnit(courseId, moduleId, unitId)

                val courseName = findViewById<TextView>(R.id.course_name)
                courseName.text = course.name

                val unitName = findViewById<TextView>(R.id.unit_name)
                unitName.text = unit.name

                if (unit.markable) {
                    val unitMarks = findViewById<LinearLayout>(R.id.unit_marks)
                    unitMarks.visibility = View.VISIBLE
                    val maxMark = findViewById<TextView>(R.id.max_mark)
                    val userMark = findViewById<TextView>(R.id.user_mark)
                    maxMark.text = "Максимальный балл: ${unit.max_mark}"
                    userMark.text = "Текущий балл: ${unit.user_mark}"
                    val mark = findViewById<ProgressBar>(R.id.progressBar)
                    mark.progress = unit.mark.toInt()
                    val maxProgress = findViewById<ProgressBar>(R.id.progressBar2)
                    maxProgress.progress = unit.max_progress.toInt()
                }

                // Отображение unit_content
                val contentContainer = findViewById<LinearLayout>(R.id.content_container)
                contentContainer.visibility = View.VISIBLE

                // Обработка страниц
                val pageNavContainer = findViewById<LinearLayout>(R.id.page_nav_container)
                if (unit.unit_content.pages.isNotEmpty()) {
                    pageNavContainer.visibility = View.VISIBLE
                    for (page in unit.unit_content.pages) {
                        val pageContainer = LinearLayout(this@UnitActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            orientation = LinearLayout.VERTICAL
                            setPadding(0, 8, 0, 8)
                        }

                        val pageButton = Button(this@UnitActivity, null, 0, R.style.moduleBn).apply {
                            text = page.name
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(16, 0, 16, 0)
                            }
                        }

                        var webView: WebView? = null
                        pageButton.setOnClickListener {
                            if (webView == null) {
                                webView = createWebView(page.html_content)
                                pageContainer.addView(webView)
                            } else {
                                pageContainer.removeView(webView)
                                webView?.destroy()
                                webView = null
                            }
                        }
                        pageButton.background = getDrawable(R.drawable.shape_module)
                        pageContainer.addView(pageButton)
                        pageNavContainer.addView(pageContainer)
                    }
                }

            } catch (e: Exception) {
                Log.e("MyTag", "Ошибка при получении данных: ${e.message}")
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(htmlContent: String?): WebView {
        return WebView(this@UnitActivity).apply {
            // Get screen height and subtract margins, toolbar, etc.
            val displayMetrics = resources.displayMetrics
            val maxHeight = (displayMetrics.heightPixels * 0.6).toInt() // Use 60% of screen height, adjust as needed

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                maxHeight // Set fixed height
            ).apply {
                setMargins(16, 8, 16, 0)
            }
            setBackgroundColor(resources.getColor(android.R.color.white))

            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = true
            settings.javaScriptEnabled = true
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
            isScrollContainer = true

            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_UP -> {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                v.onTouchEvent(event)
                true
            }

            if (htmlContent != null) {
                val styledContent = addStylesIfNeeded(htmlContent)
                loadDataWithBaseURL(null, styledContent, "text/html", "UTF-8", null)
            } else {
                loadDataWithBaseURL(null, "Контент отсутствует", "text/html", "UTF-8", null)
            }
        }
    }

    private fun addStylesIfNeeded(htmlContent: String): String {
        return if (!htmlContent.contains("<style>")) {
            """
            <html>
            <head>
                <style>
                    body { 
                        margin: 0; 
                        border: 0;    
                        border: 1px solid #C2C1C1;
                        border-radius: 10px;
                        background-color: #FFFFFF; 
                        font-family: Montserrat; 
                        padding: 16px;
                        line-height: 1.6;
                        color: black;
                    }
                    
                    .alert { 
                        padding: 12px 16px; 
                        border-radius: 4px; 
                        margin: 12px 0;
                        border-left: 4px solid;
                    }
                    .alert-success { 
                        background-color: #d1e7dd; 
                        border-color: #00a854;
                        color: #0a3622;
                    }
                    .alert-danger { 
                        background-color: #f8d7da; 
                        border-color: #f44336;
                        color: #58151c;
                    }
                    .alert-info { 
                        background-color: #cfe2ff; 
                        border-color: #0288d1;
                        color: #052c65;
                    }
                    .alert-warning { 
                        background-color: #fff3cd; 
                        border-color: #ffca28;
                        color: #664d03;
                    }
                    
                    pre, code {
                        font-family: 'Consolas', 'Courier New', monospace;
                        background-color: #2d2d2d;
                        color: #f8f8f2;
                        border-radius: 6px;
                        border: 1px solid #444;
                    }
                    pre {
                        Robles: 16px;
                        overflow-x: auto;
                        line-height: 1.5;
                        margin: 10px 0;
                    }
                    code {
                        padding: 3px 6px;
                        font-size: 85%;
                    }
                    .keyword { color: #ff79c6; }
                    .string { color: #f1fa8c; }
                    .comment { color: #6272a4; }
                    .number { color: #bd93f9; }
                    .function { color: #50fa7b; }
                    .variable { color: #8be9fd; }
                    
                    .variant {
                        color: black;
                        border: 1px solid #C2C1C1;
                        border-radius: 10px;
                        display: block;
                        margin: 12px 0;
                        padding: 12px
                    }
                    
                    h1, h2, h3, h4 {
                        color: black;
                        margin-top: 24px;
                        margin-bottom: 16px;
                    }
                    
                    ul, ol {
                        padding-left: 24px;
                    }
                    li {
                        margin-bottom: 8px;
                    }
                    
                    hr {
                        border: 0;
                        height: 1px;
                        background-color: #e0e0e0;
                        margin: 24px 0;
                    }
                    
                    .math {
                        font-style: italic;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        background-color: #ffffff; 
                        margin: 12px 0; 
                        font-family: Arial, sans-serif;
                    }
                    th, td {
                        padding: 12px 16px;
                        text-align: left;
                        border-bottom: 1px solid #e0e0e0; 
                    }
                    th {
                        background-color: #f5f5f5; 
                        color: black; 
                        font-weight: bold;
                    }
                    tr:hover {
                        background-color: #f0f0f0; 
                    }
                </style>
            </head>
            <body>
                $htmlContent
            </body>
            </html>
        """.trimIndent()
        } else {
            htmlContent
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