

package com.onepercentbetter.core.database.di

import com.onepercentbetter.core.database.OpbDatabase
import com.onepercentbetter.core.database.dao.NewsResourceDao
import com.onepercentbetter.core.database.dao.NewsResourceFtsDao
import com.onepercentbetter.core.database.dao.RecentSearchQueryDao
import com.onepercentbetter.core.database.dao.TopicDao
import com.onepercentbetter.core.database.dao.TopicFtsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesTopicsDao(
        database: OpbDatabase,
    ): TopicDao = database.topicDao()

    @Provides
    fun providesNewsResourceDao(
        database: OpbDatabase,
    ): NewsResourceDao = database.newsResourceDao()

    @Provides
    fun providesTopicFtsDao(
        database: OpbDatabase,
    ): TopicFtsDao = database.topicFtsDao()

    @Provides
    fun providesNewsResourceFtsDao(
        database: OpbDatabase,
    ): NewsResourceFtsDao = database.newsResourceFtsDao()

    @Provides
    fun providesRecentSearchQueryDao(
        database: OpbDatabase,
    ): RecentSearchQueryDao = database.recentSearchQueryDao()
}
