package com.absenku

import android.app.Application
import androidx.room.Room
import com.absenku.data.AttendanceDatabase
import com.absenku.network.SupabaseApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AbsenKuApplication : Application() {

    companion object {
        lateinit var instance: AbsenKuApplication
    }

    private var _database: AttendanceDatabase? = null
    private var _supabaseApi: SupabaseApi? = null

    val database: AttendanceDatabase
        get() {
            if (_database == null) {
                _database = Room.databaseBuilder(
                    this,
                    AttendanceDatabase::class.java,
                    "absenku.db"
                ).fallbackToDestructiveMigration().build()
            }
            return _database!!
        }

    val supabaseApi: SupabaseApi
        get() {
            if (_supabaseApi == null) {
                val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
                val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()

                _supabaseApi = Retrofit.Builder()
                    .baseUrl(BuildConfig.SUPABASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(SupabaseApi::class.java)
            }
            return _supabaseApi!!
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
        installCrashLogger()
    }

    /**
     * Debug black box: writes any uncaught crash to the public Downloads
     * folder so it can be collected without adb. Safe no-op on failure.
     */
    private fun installCrashLogger() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = java.io.StringWriter()
                throwable.printStackTrace(java.io.PrintWriter(sw))
                val report = "Thread: ${thread.name}\n\n$sw"
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    val values = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.Downloads.DISPLAY_NAME,
                            "absenku_crash_${System.currentTimeMillis()}.txt")
                        put(android.provider.MediaStore.Downloads.MIME_TYPE, "text/plain")
                    }
                    val uri = contentResolver.insert(
                        android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    uri?.let {
                        contentResolver.openOutputStream(it)?.use { os ->
                            os.write(report.toByteArray())
                        }
                    }
                } else {
                    val dir = android.os.Environment.getExternalStoragePublicDirectory(
                        android.os.Environment.DIRECTORY_DOWNLOADS)
                    dir.mkdirs()
                    java.io.File(dir, "absenku_crash_${System.currentTimeMillis()}.txt")
                        .writeText(report)
                }
            } catch (_: Exception) {
                // never let the logger itself crash
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}