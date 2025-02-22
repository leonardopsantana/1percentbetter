

package com.onepercentbetter.sync.di

import com.onepercentbetter.core.data.util.SyncManager
import com.onepercentbetter.sync.status.StubSyncSubscriber
import com.onepercentbetter.sync.status.SyncSubscriber
import com.onepercentbetter.sync.status.WorkManagerSyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    internal abstract fun bindsSyncStatusMonitor(
        syncStatusMonitor: WorkManagerSyncManager,
    ): SyncManager

    @Binds
    internal abstract fun bindsSyncSubscriber(
        syncSubscriber: StubSyncSubscriber,
    ): SyncSubscriber
}
