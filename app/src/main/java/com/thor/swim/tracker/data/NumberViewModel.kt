package com.thor.swim.tracker.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.time.ZoneId

class NumberViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDb.get(app).numberDao()

    val entries: Flow<List<NumberEntry>> = dao.observeAll()

    fun save(value: Int, date: LocalDate) {
        viewModelScope.launch {
            dao.upsert(NumberEntry(value = value, date = date))
        }
    }

    fun entryForDate(date: LocalDate): Flow<NumberEntry?> = dao.observeByDate(date)

    fun addTestEntries() {
        val cal = Calendar.getInstance()

        viewModelScope.launch {
            for (i in 1..100) {
                cal.timeInMillis = System.currentTimeMillis()
                cal.add(Calendar.DAY_OF_YEAR, -i)
                val date: LocalDate =
                    cal.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                dao.upsert(NumberEntry(value = i + 10, date = date))
            }
        }
    }

    fun addPreviousWeakEntries() {
        viewModelScope.launch {
            val dates = listOf(
                LocalDate.of(2025, 8, 11),
                LocalDate.of(2025, 8, 12),
                LocalDate.of(2025, 8, 13),
                LocalDate.of(2025, 8, 14),
                LocalDate.of(2025, 8, 15),
                LocalDate.of(2025, 8, 18),
                LocalDate.of(2025, 8, 19),
                LocalDate.of(2025, 8, 20),
                LocalDate.of(2025, 8, 21),
                LocalDate.of(2025, 8, 22),
                LocalDate.of(2025, 8, 25),
                LocalDate.of(2025, 9, 8),
                LocalDate.of(2025, 9, 9),
            )

            dates.forEach { date ->
                dao.upsert(NumberEntry(value = 60, date = date))
            }
        }
    }

}