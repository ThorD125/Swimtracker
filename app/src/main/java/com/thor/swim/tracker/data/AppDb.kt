package com.thor.swim.tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [NumberEntry::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun numberDao(): NumberDao

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null
        fun get(context: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDb::class.java, "app.db")
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
