package com.absenku.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class AttendanceRepository(private val dao: AttendanceDao, private val application: Application) {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    val allRecords: LiveData<List<AttendanceRecord>> = dao.getAll()
    val todayRecords: LiveData<List<AttendanceRecord>> = dao.getToday()
    val unsyncedRecords: LiveData<List<AttendanceRecord>> = dao.getUnsynced()

    fun insert(record: AttendanceRecord) {
        ioScope.launch {
            dao.insert(record)
        }
    }

    fun insertAll(records: List<AttendanceRecord>) {
        ioScope.launch {
            dao.insertAll(records)
        }
    }

    fun update(record: AttendanceRecord) {
        ioScope.launch {
            dao.update(record)
        }
    }

    fun getTodayStats(): LiveData<Pair<Int, Int>> {
        val mediator = MediatorLiveData<Pair<Int, Int>>()
        var checkInCount = 0
        var checkOutCount = 0

        mediator.addSource(dao.getToday()) { records ->
            checkInCount = records.count { it.type == "check_in" }
            checkOutCount = records.count { it.type == "check_out" }
            mediator.value = checkInCount to checkOutCount
        }
        return mediator
    }

    fun getMonthStats(): LiveData<Pair<Int, Int>> {
        val mediator = MediatorLiveData<Pair<Int, Int>>()
        var checkInCount = 0
        var checkOutCount = 0

        mediator.addSource(dao.getAll()) { records ->
            val cal = java.util.Calendar.getInstance()
            val thisMonth = cal.get(java.util.Calendar.MONTH)
            val thisYear = cal.get(java.util.Calendar.YEAR)

            checkInCount = records.count { record ->
                val date = java.util.Date(record.timestamp)
                val c = java.util.Calendar.getInstance()
                c.time = date
                c.get(java.util.Calendar.MONTH) == thisMonth &&
                c.get(java.util.Calendar.YEAR) == thisYear &&
                record.type == "check_in"
            }
            checkOutCount = records.count { record ->
                val date = java.util.Date(record.timestamp)
                val c = java.util.Calendar.getInstance()
                c.time = date
                c.get(java.util.Calendar.MONTH) == thisMonth &&
                c.get(java.util.Calendar.YEAR) == thisYear &&
                record.type == "check_out"
            }
            mediator.value = checkInCount to checkOutCount
        }
        return mediator
    }

    fun markSynced(id: Long, serverId: String) {
        ioScope.launch {
            val record = dao.getById(id)
            record?.let {
                val updated = it.copy(synced = true, serverId = serverId)
                dao.update(updated)
            }
        }
    }
}