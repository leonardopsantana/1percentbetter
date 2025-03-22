package com.onepercentbetter.core.database.di

import com.onepercentbetter.core.database.OpbDatabase
import com.onepercentbetter.core.database.dao.CategoryDao
import com.onepercentbetter.core.database.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesCategoryDao(
        database: OpbDatabase,
    ): CategoryDao = database.categoryDao()

    @Provides
    fun providesTaskDao(
        database: OpbDatabase,
    ): TaskDao = database.taskDao()
}
