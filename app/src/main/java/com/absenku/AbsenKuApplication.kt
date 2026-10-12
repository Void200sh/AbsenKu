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
        @Suppress("NOTHING_TO_INLINE")
        inline fun <reified T> getService(): T = instance.getService()
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
    }
}