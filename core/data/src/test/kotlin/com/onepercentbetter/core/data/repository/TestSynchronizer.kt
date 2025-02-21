

package com.onepercentbetter.core.data.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.datastore.ChangeListVersions
import com.onepercentbetter.core.datastore.OPBPreferencesDataSource

/**
 * Test synchronizer that delegates to [OPBPreferencesDataSource]
 */
class TestSynchronizer(
    private val opbPreferences: OPBPreferencesDataSource,
) : Synchronizer {
    override suspend fun getChangeListVersions(): ChangeListVersions =
        opbPreferences.getChangeListVersions()

    override suspend fun updateChangeListVersions(
        update: ChangeListVersions.() -> ChangeListVersions,
    ) = opbPreferences.updateChangeListVersion(update)
}
