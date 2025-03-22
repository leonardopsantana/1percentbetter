package com.onepercentbetter.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.onepercentbetter.core.database.dao.CategoryDao
import com.onepercentbetter.core.database.dao.TaskDao
import com.onepercentbetter.core.database.model.CategoryEntity
import com.onepercentbetter.core.database.model.TaskEntity
import com.onepercentbetter.core.database.util.InstantConverter

@Database(
    entities = [
        TaskEntity::class,
        CategoryEntity::class,
    ],
    version = 11,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class OpbDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao
}
