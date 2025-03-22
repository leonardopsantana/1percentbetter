

package com.onepercentbetter.core.data.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.data.model.asEntity
import com.onepercentbetter.core.data.repository.category.OfflineFirstCategoryRepository
import com.onepercentbetter.core.data.testdoubles.CollectionType
import com.onepercentbetter.core.data.testdoubles.TestOPBNetworkDataSource
import com.onepercentbetter.core.data.testdoubles.TestCategoryDao
import com.onepercentbetter.core.database.dao.CategoryDao
import com.onepercentbetter.core.database.model.CategoryEntity
import com.onepercentbetter.core.database.model.asModel
import com.onepercentbetter.core.datastore.OPBPreferencesDataSource
import com.onepercentbetter.core.datastore.test.InMemoryDataStore
import com.onepercentbetter.core.network.model.CategoryResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class OfflineFirstCategoryRepositoryTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: OfflineFirstCategoryRepository

    private lateinit var categoryDao: CategoryDao

    private lateinit var network: TestOPBNetworkDataSource

    private lateinit var opbPreferences: OPBPreferencesDataSource

    private lateinit var synchronizer: Synchronizer

    @Before
    fun setup() {
        categoryDao = TestCategoryDao()
        network = TestOPBNetworkDataSource()
        opbPreferences = OPBPreferencesDataSource(InMemoryDataStore(_root_ide_package_.com.onepercentbetter.core.datastore.UserPreferences.getDefaultInstance()))
        synchronizer = TestSynchronizer(opbPreferences)

        subject = OfflineFirstCategoryRepository(
            categoryDao = categoryDao,
            network = network,
        )
    }

    @Test
    fun offlineFirstTopicsRepository_topics_stream_is_backed_by_topics_dao() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                categoryDao.getTopicEntities()
                    .first()
                    .map(CategoryEntity::asModel),
                subject.getCategories()
                    .first(),
            )
        }

    @Test
    fun offlineFirstTopicsRepository_sync_pulls_from_network() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            val routineTopicsResponse = network.getCategories()
                .map(CategoryResponse::asEntity)

            val dbTopics = categoryDao.getTopicEntities()
                .first()

            assertEquals(
                routineTopicsResponse.map(CategoryEntity::categoryId),
                dbTopics.map(CategoryEntity::categoryId),
            )

            // After sync version should be updated
            assertEquals(
                network.latestChangeListVersion(CollectionType.Topics),
                synchronizer.getChangeListVersions().topicVersion,
            )
        }

    @Test
    fun offlineFirstTopicsRepository_incremental_sync_pulls_from_network() =
        testScope.runTest {
            // Set topics version to 10
            synchronizer.updateChangeListVersions {
                copy(topicVersion = 10)
            }

            subject.syncWith(synchronizer)

            val routineTopicsResponse = network.getCategories()
                .map(CategoryResponse::asEntity)
                // Drop 10 to simulate the first 10 items being unchanged
                .drop(10)

            val dbTopics = categoryDao.getTopicEntities()
                .first()

            assertEquals(
                routineTopicsResponse.map(CategoryEntity::categoryId),
                dbTopics.map(CategoryEntity::categoryId),
            )

            // After sync version should be updated
            assertEquals(
                network.latestChangeListVersion(CollectionType.Topics),
                synchronizer.getChangeListVersions().topicVersion,
            )
        }

    @Test
    fun offlineFirstTopicsRepository_sync_deletes_items_marked_deleted_on_network() =
        testScope.runTest {
            val routineTopicsResponse = network.getCategories()
                .map(CategoryResponse::asEntity)
                .map(CategoryEntity::asModel)

            // Delete half of the items on the network
            val deletedItems = routineTopicsResponse
                .map(Topic::id)
                .partition { it.chars().sum() % 2 == 0 }
                .first
                .toSet()

            deletedItems.forEach {
                network.editCollection(
                    collectionType = CollectionType.Topics,
                    id = it,
                    isDelete = true,
                )
            }

            subject.syncWith(synchronizer)

            val dbTopics = categoryDao.getTopicEntities()
                .first()
                .map(CategoryEntity::asModel)

            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                routineTopicsResponse.map(Topic::id) - deletedItems,
                dbTopics.map(Topic::id),
            )

            // After sync version should be updated
            assertEquals(
                network.latestChangeListVersion(CollectionType.Topics),
                synchronizer.getChangeListVersions().topicVersion,
            )
        }
}
