

package com.onepercentbetter.core.data.di

import com.onepercentbetter.core.data.repository.CompositeUserNewsResourceRepository
import com.onepercentbetter.core.data.repository.UserNewsResourceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UserNewsResourceRepositoryModule {
    @Binds
    fun bindsUserNewsResourceRepository(
        userDataRepository: CompositeUserNewsResourceRepository,
    ): UserNewsResourceRepository
}
