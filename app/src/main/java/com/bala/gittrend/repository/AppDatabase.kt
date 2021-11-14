package com.bala.gittrend.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bala.gittrend.models.ProjectInfo
import com.bala.gittrend.models.ProjectOwnerDetails

@Database(entities = [ProjectOwnerDetails::class, ProjectInfo::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}