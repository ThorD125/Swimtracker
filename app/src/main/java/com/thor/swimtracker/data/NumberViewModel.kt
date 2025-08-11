package com.thor.swimtracker.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NumberViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDb.get(app).numberDao()

    val entries: Flow<List<NumberEntry>> = dao.observeAll()

    fun save(value: Int, date: String) {
        viewModelScope.launch {
            dao.upsert(NumberEntry(value = value, date = date))
        }
    }

    fun addTestEntries() {
        val sdf = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()

        viewModelScope.launch {
            for (i in 1..100) {
                cal.timeInMillis = System.currentTimeMillis()
                cal.add(Calendar.DAY_OF_YEAR, -i)
                val dateStr = sdf.format(cal.time)
                dao.upsert(NumberEntry(value = i + 10, date = dateStr))
            }
        }
    }
}