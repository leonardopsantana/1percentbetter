

package com.onepercentbetter.core.data.di

import com.onepercentbetter.core.data.repository.category.CategoryRepository
import com.onepercentbetter.core.data.repository.category.OfflineFirstCategoryRepository
import com.onepercentbetter.core.data.repository.task.OfflineFirstTaskRepository
import com.onepercentbetter.core.data.repository.task.TaskRepository
import com.onepercentbetter.core.data.repository.user.OfflineFirstUserDataRepository
import com.onepercentbetter.core.data.repository.user.UserDataRepository
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
    internal abstract fun bindsCategoryRepository(
        topicsRepository: OfflineFirstCategoryRepository,
    ): CategoryRepository

    @Binds
    internal abstract fun bindsTaskRepository(
        newsRepository: OfflineFirstTaskRepository,
    ): TaskRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
}
