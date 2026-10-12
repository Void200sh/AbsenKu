package com.absenku.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("type")
    val type: String, // "check_in" or "check_out"

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("location")
    val location: String?,

    @SerializedName("photo_path")
    val photoPath: String?,

    @SerializedName("synced")
    val synced: Boolean = false,

    @SerializedName("server_id")
    val serverId: String? = null
)