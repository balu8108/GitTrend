package com.bala.gittrend.repository

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.bala.gittrend.models.ProjectInfo
import com.bala.gittrend.models.ProjectOwnerDetails
import com.bala.gittrend.models.ProjectOwnerWithProjects
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Transaction
    @Query("SELECT * FROM ProjectOwnerDetails")
    fun getProjectOwnerWithProjects(): Flow<List<ProjectOwnerWithProjects>>

    @Transaction
    @Insert(onConflict = REPLACE)
    suspend fun insertProjectOwnerDetails(projectOwnerDetails: List<ProjectOwnerDetails>)

    @Transaction
    @Query("DELETE FROM ProjectOwnerDetails")
    suspend fun deleteProjectOwnerDetails()

    @Transaction
    @Insert(onConflict = REPLACE)
    suspend fun insertProjectInfos(projectInfos: List<ProjectInfo>)

    @Transaction
    @Query("DELETE FROM ProjectInfo")
    suspend fun deleteProjectInfos()
}