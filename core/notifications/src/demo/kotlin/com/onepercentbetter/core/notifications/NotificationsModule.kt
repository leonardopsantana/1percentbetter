

package com.onepercentbetter.core.notifications

import com.onepercentbetter.core.notifications.NoOpNotifier
import com.onepercentbetter.core.notifications.Notifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationsModule {
    @Binds
    abstract fun bindNotifier(
        notifier: NoOpNotifier,
    ): Notifier
}
