package com.thor.swim.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "number_entries",
    indices = [Index(value = ["date"], unique = true)]
)
data class NumberEntry(
    @PrimaryKey val date: String,
    val value: Int,
    val createdAt: Long = System.currentTimeMillis()
)
