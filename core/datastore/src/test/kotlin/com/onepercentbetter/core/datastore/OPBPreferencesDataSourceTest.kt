

package com.onepercentbetter.core.datastore

import com.onepercentbetter.core.datastore.test.InMemoryDataStore
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before

class OPBPreferencesDataSourceTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: OPBPreferencesDataSource

    @Before
    fun setup() {
        subject = OPBPreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
    }
}
