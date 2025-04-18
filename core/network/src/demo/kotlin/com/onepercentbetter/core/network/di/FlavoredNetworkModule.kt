

package com.onepercentbetter.core.network.di

import com.onepercentbetter.core.network.OPBNetworkDataSource
import com.onepercentbetter.core.network.demo.DemoOPBNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredNetworkModule {

    @Binds
    fun binds(impl: DemoOPBNetworkDataSource): OPBNetworkDataSource
}
