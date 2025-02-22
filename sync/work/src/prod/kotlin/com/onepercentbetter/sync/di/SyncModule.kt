

package com.onepercentbetter.sync.di

import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.onepercentbetter.core.data.util.SyncManager
import com.onepercentbetter.sync.status.FirebaseSyncSubscriber
import com.onepercentbetter.sync.status.SyncSubscriber
import com.onepercentbetter.sync.status.WorkManagerSyncManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    internal abstract fun bindsSyncStatusMonitor(
        syncStatusMonitor: WorkManagerSyncManager,
    ): SyncManager

    @Binds
    internal abstract fun bindsSyncSubscriber(
        syncSubscriber: FirebaseSyncSubscriber,
    ): SyncSubscriber

    companion object {
        @Provides
        @Singleton
        internal fun provideFirebaseMessaging(): FirebaseMessaging = Firebase.messaging
    }
}
