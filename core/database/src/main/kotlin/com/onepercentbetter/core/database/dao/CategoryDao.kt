package com.onepercentbetter.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onepercentbetter.core.database.model.CategoryEntity

/**
 * DAO for [CategoryEntity] access
 */
@Dao
interface CategoryDao {
    @Query(value = "SELECT * FROM categories")
    suspend fun getCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTopics(entity: CategoryEntity): Long
}
