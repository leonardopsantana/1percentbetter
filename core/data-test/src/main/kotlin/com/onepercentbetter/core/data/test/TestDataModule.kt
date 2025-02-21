

package com.onepercentbetter.core.data.test

import com.onepercentbetter.core.data.di.DataModule
import com.onepercentbetter.core.data.repository.NewsRepository
import com.onepercentbetter.core.data.repository.RecentSearchRepository
import com.onepercentbetter.core.data.repository.SearchContentsRepository
import com.onepercentbetter.core.data.repository.TopicsRepository
import com.onepercentbetter.core.data.repository.UserDataRepository
import com.onepercentbetter.core.data.test.repository.FakeNewsRepository
import com.onepercentbetter.core.data.test.repository.FakeRecentSearchRepository
import com.onepercentbetter.core.data.test.repository.FakeSearchContentsRepository
import com.onepercentbetter.core.data.test.repository.FakeTopicsRepository
import com.onepercentbetter.core.data.test.repository.FakeUserDataRepository
import com.onepercentbetter.core.data.util.NetworkMonitor
import com.onepercentbetter.core.data.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class],
)
internal interface TestDataModule {
    @Binds
    fun bindsTopicRepository(
        fakeTopicsRepository: FakeTopicsRepository,
    ): TopicsRepository

    @Binds
    fun bindsNewsResourceRepository(
        fakeNewsRepository: FakeNewsRepository,
    ): NewsRepository

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: FakeUserDataRepository,
    ): UserDataRepository

    @Binds
    fun bindsRecentSearchRepository(
        recentSearchRepository: FakeRecentSearchRepository,
    ): RecentSearchRepository

    @Binds
    fun bindsSearchContentsRepository(
        searchContentsRepository: FakeSearchContentsRepository,
    ): SearchContentsRepository

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: AlwaysOnlineNetworkMonitor,
    ): NetworkMonitor

    @Binds
    fun binds(impl: DefaultZoneIdTimeZoneMonitor): TimeZoneMonitor
}
