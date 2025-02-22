

package com.onepercentbetter.core.data.di

import com.onepercentbetter.core.data.repository.DefaultRecentSearchRepository
import com.onepercentbetter.core.data.repository.DefaultSearchContentsRepository
import com.onepercentbetter.core.data.repository.NewsRepository
import com.onepercentbetter.core.data.repository.OfflineFirstNewsRepository
import com.onepercentbetter.core.data.repository.OfflineFirstTopicsRepository
import com.onepercentbetter.core.data.repository.OfflineFirstUserDataRepository
import com.onepercentbetter.core.data.repository.RecentSearchRepository
import com.onepercentbetter.core.data.repository.SearchContentsRepository
import com.onepercentbetter.core.data.repository.TopicsRepository
import com.onepercentbetter.core.data.repository.UserDataRepository
import com.onepercentbetter.core.data.util.ConnectivityManagerNetworkMonitor
import com.onepercentbetter.core.data.util.NetworkMonitor
import com.onepercentbetter.core.data.util.TimeZoneBroadcastMonitor
import com.onepercentbetter.core.data.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsTopicRepository(
        topicsRepository: OfflineFirstTopicsRepository,
    ): TopicsRepository

    @Binds
    internal abstract fun bindsNewsResourceRepository(
        newsRepository: OfflineFirstNewsRepository,
    ): NewsRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsRecentSearchRepository(
        recentSearchRepository: DefaultRecentSearchRepository,
    ): RecentSearchRepository

    @Binds
    internal abstract fun bindsSearchContentsRepository(
        searchContentsRepository: DefaultSearchContentsRepository,
    ): SearchContentsRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
}
