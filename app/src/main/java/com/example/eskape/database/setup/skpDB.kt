package com.example.eskape.database.setup

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.eskape.data.skpData
import com.example.eskape.database.dao.SkpDao

@Database(
    entities     = [skpData::class],
    version      = 1,
    exportSchema = false
)
abstract class skpDB : RoomDatabase() {
    abstract fun skpDao(): SkpDao
}