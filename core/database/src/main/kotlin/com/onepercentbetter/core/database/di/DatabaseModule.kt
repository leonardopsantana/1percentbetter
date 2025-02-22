

package com.onepercentbetter.core.database.di

import android.content.Context
import androidx.room.Room
import com.onepercentbetter.core.database.OpbDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesOPBDatabase(
        @ApplicationContext context: Context,
    ): OpbDatabase = Room.databaseBuilder(
        context,
        OpbDatabase::class.java,
        "opb-database",
    ).build()
}
