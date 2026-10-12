package com.absenku.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(records: List<AttendanceRecord>)

    @Update
    suspend fun update(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records ORDER BY timestamp DESC")
    fun getAll(): LiveData<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE synced = 0 ORDER BY timestamp ASC")
    fun getUnsynced(): LiveData<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE date(timestamp/1000, 'unixepoch') = date('now') ORDER BY timestamp DESC")
    fun getToday(): LiveData<List<AttendanceRecord>>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date(timestamp/1000, 'unixepoch') = date('now') AND type = 'check_in'")
    suspend fun getTodayCheckInCount(): Int

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date(timestamp/1000, 'unixepoch') = date('now') AND type = 'check_out'")
    suspend fun getTodayCheckOutCount(): Int

    @Query("SELECT COUNT(*) FROM attendance_records WHERE strftime('%Y-%m', timestamp/1000, 'unixepoch') = strftime('%Y-%m', 'now') AND type = 'check_in'")
    suspend fun getMonthCheckInCount(): Int

    @Query("SELECT * FROM attendance_records WHERE id = :id")
    suspend fun getById(id: Long): AttendanceRecord?
}