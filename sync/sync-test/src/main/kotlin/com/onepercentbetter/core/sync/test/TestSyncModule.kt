

package com.onepercentbetter.core.sync.test

import com.onepercentbetter.core.data.util.SyncManager
import com.onepercentbetter.sync.di.SyncModule
import com.onepercentbetter.sync.status.StubSyncSubscriber
import com.onepercentbetter.sync.status.SyncSubscriber
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SyncModule::class],
)
internal interface TestSyncModule {
    @Binds
    fun bindsSyncStatusMonitor(
        syncStatusMonitor: NeverSyncingSyncManager,
    ): SyncManager

    @Binds
    fun bindsSyncSubscriber(
        syncSubscriber: StubSyncSubscriber,
    ): SyncSubscriber
}
