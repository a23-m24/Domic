package com.example.domicmobile

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

class ApiDomic {

    data class LoginRequest(
        val username: String,
        val password: String,
        val remember_me: Boolean = false
    )

    data class LoginResponse(
        val access_token: String
    )

    data class UserResponse(
        val shorted_name: String,
        val full_name: String,
        val user_type: String
    )

    data class Deadline(
        val course_id: Int,
        val module_id: Int?,
        val unit_id: Int?,
        val course_name: String,
        val unit_name: String?,
        val date: String,
        val coefficient: Double,
        val course_deadline: Boolean
    )

    data class DeadlinesResponse(
        val deadlines: List<Deadline>
    )

    data class Course(
        val id: Int,
        val name: String
    )

    data class CoursrsResponse(
        val courses: List<Course>
    )

    interface DomicApi {
        @POST("auth/login")
        suspend fun login(@Body request: LoginRequest): LoginResponse

        @GET("auth/user")
        suspend fun getUser(): UserResponse

        @GET("student/deadlines")
        suspend fun getDeadlines(): DeadlinesResponse

        @GET("student/courses")
        suspend fun getCourses(): CoursrsResponse
    }

    class DomicApiClient {
        companion object {
            @Volatile
            private var INSTANCE: DomicApiClient? = null

            fun getInstance(): DomicApiClient {
                return INSTANCE ?: synchronized(this) {
                    INSTANCE ?: DomicApiClient().also { INSTANCE = it }
                }
            }
        }
        private val apiService: DomicApi
        private var userToken: String? = null

        init {
            val httpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-Access-Token", "b2b274b2-5bf0-4534-87d5-62cc05a8da93")
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                    userToken?.let {
                        requestBuilder.addHeader("Authorization", "Bearer $it")
                    }
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://domic-plus.vaxonob.ru/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

            apiService = retrofit.create(DomicApi::class.java)
        }

        suspend fun login(username: String, password: String, rememberMe: Boolean = false): String? {
            val request = LoginRequest(username, password, rememberMe)
            val response = apiService.login(request)
            userToken = response.access_token
            return userToken
        }

        suspend fun getUser(): UserResponse {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getUser()
        }
        suspend fun getStudentDeadlines(): DeadlinesResponse {
            return apiService.getDeadlines()
        }

        suspend fun getStudentCourses(): CoursrsResponse {
            return apiService.getCourses()
        }
    }
}