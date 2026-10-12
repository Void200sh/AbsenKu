package com.absenku.ui.checkin

import android.Manifest
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.absenku.data.AttendanceRecord
import com.absenku.data.AttendanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class CheckInViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AttendanceRepository(
        com.absenku.AbenKuApplication.instance.database.attendanceDao(),
        application
    )

    val capturedPhotoPath = MutableLiveData<String>()
    val isProcessing = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val successMessage = MutableLiveData<String>()

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun saveCheckIn(type: String, photoPath: String?, location: String?) {
        isProcessing.value = true

        ioScope.launch {
            val record = AttendanceRecord(
                userId = "void200",
                type = type,
                timestamp = System.currentTimeMillis(),
                location = location,
                photoPath = photoPath,
                synced = false
            )
            repository.insert(record)

            // Simulate API call to Supabase
            // TODO: Implement actual Supabase sync

            isProcessing.postValue(false)
            successMessage.postValue("${type.replace("_", " ").capitalize()} berhasil dicatat!")
        }
    }

    fun capturePhoto(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(
            getApplication().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
            "ABSENKU_${timeStamp}.jpg"
        )
        return imageFile
    }
}