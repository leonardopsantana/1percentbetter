

package com.onepercentbetter.core.datastore.test

import androidx.datastore.core.DataStore
import com.onepercentbetter.core.datastore.UserPreferences
import com.onepercentbetter.core.datastore.UserPreferencesSerializer
import com.onepercentbetter.core.datastore.di.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class],
)
internal object TestDataStoreModule {
    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        serializer: UserPreferencesSerializer,
    ): DataStore<_root_ide_package_.com.onepercentbetter.core.datastore.UserPreferences> = InMemoryDataStore(serializer.defaultValue)
}
