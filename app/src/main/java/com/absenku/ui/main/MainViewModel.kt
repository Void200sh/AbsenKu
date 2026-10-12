package com.absenku.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.absenku.data.AttendanceRecord
import com.absenku.data.AttendanceRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AttendanceRepository(
        com.absenku.AbenKuApplication.instance.database.attendanceDao(),
        application
    )

    val allRecords: LiveData<List<AttendanceRecord>> = repository.allRecords
    val todayRecords: LiveData<List<AttendanceRecord>> = repository.todayRecords
    val todayStats = Transformations.map(repository.getTodayStats()) { pair ->
        "${pair.first} / ${pair.second + pair.first}"
    }
    val monthStats = Transformations.map(repository.getMonthStats()) { pair ->
        "${pair.first} / ${pair.second + pair.first}"
    }

    val greetingPrefix = MutableLiveData<String>()
    val userName = MutableLiveData<String>()

    init {
        updateGreeting()
        userName.value = "void200"
    }

    private fun updateGreeting() {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        greetingPrefix.value = when {
            hour in 5..11 -> "Selamat Pagi"
            hour in 11..15 -> "Selamat Siang"
            hour in 15..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }
}