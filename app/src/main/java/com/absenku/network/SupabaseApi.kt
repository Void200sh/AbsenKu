package com.absenku.network

import com.absenku.data.AttendanceRecord
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface SupabaseApi {

    @Headers(
        "apikey: \${SUPABASE_ANON_KEY}",
        "Authorization: Bearer \${SUPABASE_ANON_KEY}",
        "Content-Type: application/json",
        "Prefer: return=representation"
    )
    @POST("rest/v1/attendance_records")
    suspend fun insertAttendance(
        @Body record: AttendanceRecord
    ): retrofit2.Response<Any>

    @Headers(
        "apikey: \${SUPABASE_ANON_KEY}",
        "Authorization: Bearer \${SUPABASE_ANON_KEY}"
    )
    @GET("rest/v1/attendance_records")
    suspend fun getAttendance(
        @Query("select") select: String = "*",
        @Query("user_id") userId: String = "eq.${BuildConfig.USER_ID}",
        @Query("order") order: String = "timestamp.desc"
    ): retrofit2.Response<List<AttendanceRecord>>

    // Upload photo to Supabase Storage
    @Multipart
    @POST("storage/v1/object/attendance_photos")
    suspend fun uploadPhoto(
        @Part("file") file: MultipartBody.Part,
        @Part("upsert") upsert: RequestBody,
        @Part("content-type") contentType: RequestBody
    ): retrofit2.Response<Any>

    companion object {
        const val BASE_URL = "https://your-project.supabase.co/"
    }
}