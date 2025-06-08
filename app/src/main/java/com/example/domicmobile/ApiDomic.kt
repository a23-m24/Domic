package com.example.domicmobile

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class ApiDomic {

    // Existing Data Classes (unchanged)
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
        val first_semester: List<Course>,
        val second_semester: List<Course>,
        val courses: List<Course>
    )

    data class DebtUnit(
        val id: Int,
        val module_id: Int,
        val name: String,
        val date: String,
        val coefficient: Double,
        val mark: Int,
        val user_mark: Int,
        val max_mark: Int,
        val type: String // "danger", "warning", or "success"
    )

    data class DebtCourse(
        val id: Int,
        val name: String,
        val units: List<DebtUnit>
    )

    data class DebtsResponse(
        val debt_courses: List<DebtCourse>
    )

    data class Announcement(
        val title: String,
        val description: String?,
        val type: Int // 1: info, 2: success, 3: warning, 4: danger
    )

    data class Module(
        val id: Int,
        val name: String,
        val blocked: Boolean,
        val markable: Boolean,
        val progress: Int
    )

    data class Unit(
        val id: Int,
        val module_id: Int,
        val name: String,
        val type: String,
        val blocked: Boolean,
        val markable: Boolean,
        val progress: Int
    )

    data class CourseResponse(
        val id: Int,
        val name: String,
        val description: String,
        val announcements: List<Announcement>,
        val modules: List<Module>,
        val units: List<Unit>
    )

    data class MarkUnit(
        val id: Int,
        val module_id: Int,
        val name: String,
        val module_name: String,
        val mark: Double,
        val user_mark: Double,
        val current_max_mark: Double,
        val max_mark: Double,
        val course_mark: Boolean
    )

    data class MarksResponse(
        val units: List<MarkUnit>,
        val user_total: Double,
        val current_total: Double,
        val total: Double
    )

    data class Breadcrumb(
        val id: Int,
        val name: String
    )

    data class ModuleDeadline(
        val course_id: Int,
        val module_id: Int,
        val unit_id: Int,
        val course_name: String,
        val unit_name: String,
        val date: String,
        val coefficient: Double,
        val course_deadline: Boolean
    )

    data class ModuleResponse(
        val id: Int,
        val name: String,
        val description: String,
        val breadcrumbs: List<Breadcrumb>,
        val deadlines: List<ModuleDeadline>,
        val nested_modules: List<Module>,
        val units: List<Unit>
    )

    data class UnitDeadline(
        val date: String,
        val coefficient: Double,
        val passed: Boolean
    )

    data class FileContent(
        val id: Int,
        val name: String,
        val type: String, // "external", "file", or "html"
        val html_content: String?,
        val visible: Boolean,
        val page_title: String,
        val primary: Boolean,
        val file: FileInfo?
    )

    data class FileInfo(
        val id: Int,
        val name: String
    )

    data class UnitFile(
        val id: Int,
        val name: String,
        val max_size: String?,
        val max_size_bytes: Int?,
        val format: String,
        val file_id: Int?,
        val file_name: String,
        val attached_at: String
    )

    data class TestAccess(
        val open_date: String?,
        val close_date: String?
    )

    data class TestAttempt(
        val id: Int,
        val result: String,
        val started_at: String,
        val finished_at: String,
        val time: String
    )

    data class Test(
        val id: Int,
        val name: String,
        val description: String?,
        val attempts_count: Int,
        val time_limit: String?,
        val mark_method: String,
        val access: TestAccess,
        val attempts: List<TestAttempt>,
        val blocked: Boolean
    )

    data class NavigationUnit(
        val id: Int?,
        val name: String?,
        val type: String?
    )

    data class UnitResponse(
        val id: Int,
        val name: String,
        val type: String,
        val breadcrumbs: List<Breadcrumb>,
        val deadlines: List<UnitDeadline>,
        val unit_content: UnitContent,
        val unit_files: List<UnitFile>,
        val course_deadline_date: String,
        val course_deadline_active: Boolean,
        val unit_block_deadline_date: String,
        val unit_block_deadline_active: Boolean,
        val test: Test,
        val markable: Boolean,
        val mark: Double,
        val user_mark: Double,
        val max_mark: Double,
        val max_progress: Int,
        val average_progress: Int,
        val mark_date: String,
        val comment: String?,
        val previous_unit: NavigationUnit,
        val next_unit: NavigationUnit
    )

    data class UnitContent(
        val external_pages: List<FileContent>,
        val files: List<FileContent>,
        val pages: List<FileContent>
    )

    data class ErrorResponse(
        val message: String
    )

    data class UnreadMessagesResponse(val unread_messages: Int)

    data class AvailableUsersResponse(val users: List<AvailableUser>)

    data class AvailableUser(
        val id: Int,
        val name: String,
        val type: String,
        val group: String,
        val is_online: Boolean
    )

    data class ChatsResponse(val chats: List<Chat>, val unread_messages: Int)

    data class Chat(
        val id: Int,
        val name: String,
        val last_message: LastMessage?,
        val unread_count: Int,
        val is_online: Boolean,
        val no_messages: Boolean,
        val chat_created_at: String
    )

    data class LastMessage(
        val body: String,
        val sent_by: String,
        val created_at: String,
        val is_read: Boolean
    )

    data class CreateChatRequest(val participant_id: Int)

    data class CreateChatResponse(val id: Int, val message: String)

    data class ChatMessagesResponse(val messages: List<DateMessageGroup>)

    data class DateMessageGroup(val date: String, val messages: List<Message>)

    data class Message(
        val id: Int,
        val message: String?,
        val user_id: Int,
        val created_at: String,
        val updated: Boolean,
        val read_at: String?,
        val reply_to: ReplyTo?,
        val files: List<File>
    )

    data class ReplyTo(val id: Int, val message: String, val user: User)

    data class User(val id: Int, val name: String)

    data class File(val id: Int, val name: String)

    data class SendMessageRequest(
        val content: String,
        val files: List<Int>,
        val reply_to: Int?
    )

    data class NewMessageResponse(val formatted_date: String, val message: Message)

    data class EditMessageRequest(val content: String, val files: List<Int>)

    interface DomicApi {
        @POST("auth/login")
        suspend fun login(@Body request: LoginRequest): LoginResponse

        @GET("auth/user")
        suspend fun getUser(): UserResponse

        @GET("student/deadlines")
        suspend fun getDeadlines(): DeadlinesResponse

        @GET("student/courses")
        suspend fun getCourses(): CoursrsResponse

        @GET("student/debts")
        suspend fun getDebts(): DebtsResponse

        @GET("student/courses/{courseId}")
        suspend fun getCourse(@Path("courseId") courseId: Int): CourseResponse

        @GET("student/courses/{courseId}/marks")
        suspend fun getCourseMarks(@Path("courseId") courseId: Int): MarksResponse

        @GET("student/courses/{courseId}/modules/{moduleId}")
        suspend fun getModule(@Path("courseId") courseId: Int, @Path("moduleId") moduleId: Int): ModuleResponse

        @GET("student/courses/{courseId}/modules/{moduleId}/units/{unitId}")
        suspend fun getUnit(@Path("courseId") courseId: Int, @Path("moduleId") moduleId: Int, @Path("unitId") unitId: Int): UnitResponse

        @GET("student/courses/{courseId}/modules/{moduleId}/units/{unitId}/file/download/{fileId}")
        suspend fun downloadUnitFile(@Path("courseId") courseId: Int, @Path("moduleId") moduleId: Int, @Path("unitId") unitId: Int, @Path("fileId") fileId: Int): ByteArray

        @GET("student/courses/{courseId}/modules/{moduleId}/units/{unitId}/file/{contentName}")
        suspend fun getUnitFileContent(@Path("courseId") courseId: Int, @Path("moduleId") moduleId: Int, @Path("unitId") unitId: Int, @Path("contentName") contentName: String): String

        @GET("student/courses/{courseId}/modules/{moduleId}/units/{unitId}/variant")
        suspend fun getUnitVariant(@Path("courseId") courseId: Int, @Path("moduleId") moduleId: Int, @Path("unitId") unitId: Int): String

        @GET("libs/{libName}")
        suspend fun getLibContent(@Path("libName") libName: String): String

        @GET("/messenger/unread-messages")
        suspend fun getUnreadMessages(): UnreadMessagesResponse

        @GET("/messenger/available-users")
        suspend fun getAvailableUsers(): AvailableUsersResponse


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

        fun setUserToken(savedToken: String) {
            this.userToken = savedToken
        }

        fun clearUserToken() {
            this.userToken = null
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
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getDeadlines()
        }

        suspend fun getStudentCourses(): CoursrsResponse {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getCourses()
        }

        suspend fun getStudentDebts(): DebtsResponse {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getDebts()
        }

        suspend fun getCourse(courseId: Int): CourseResponse {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getCourse(courseId)
        }

        suspend fun getCourseMarks(courseId: Int): MarksResponse {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getCourseMarks(courseId)
        }

        suspend fun getModule(courseId: Int, moduleId: Int): ModuleResponse {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getModule(courseId, moduleId)
        }

        suspend fun getUnit(courseId: Int, moduleId: Int, unitId: Int): UnitResponse {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getUnit(courseId, moduleId, unitId)
        }

        suspend fun downloadUnitFile(courseId: Int, moduleId: Int, unitId: Int, fileId: Int): ByteArray {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.downloadUnitFile(courseId, moduleId, unitId, fileId)
        }

        suspend fun getUnitFileContent(courseId: Int, moduleId: Int, unitId: Int, contentName: String): String {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getUnitFileContent(courseId, moduleId, unitId, contentName)
        }

        suspend fun getUnitVariant(courseId: Int, moduleId: Int, unitId: Int): String {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getUnitVariant(courseId, moduleId, unitId)
        }

        suspend fun getLibContent(libName: String): String {
            if (userToken == null) {
                throw Exception("Необходимо выполнить вход")
            }
            return apiService.getLibContent(libName)
        }
    }
}