package com.onepercentbetter.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.onepercentbetter.core.database.model.TaskEntity
import com.onepercentbetter.core.database.model.TaskWithCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * DAO for [TaskDao] access
 */
@Dao
interface TaskDao {

    @Transaction
    @Query(value = "SELECT * FROM task WHERE date = :dateSelected")
    fun getTasksForDate(dateSelected: Instant): Flow<List<TaskWithCategory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTopics(entity: TaskEntity): Long
}
