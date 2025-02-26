

package com.onepercentbetter.core.data.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.data.model.asEntity
import com.onepercentbetter.core.data.testdoubles.CollectionType
import com.onepercentbetter.core.data.testdoubles.TestOPBNetworkDataSource
import com.onepercentbetter.core.data.testdoubles.TestTopicDao
import com.onepercentbetter.core.database.dao.TopicDao
import com.onepercentbetter.core.database.model.TopicEntity
import com.onepercentbetter.core.database.model.asExternalModel
import com.onepercentbetter.core.datastore.OPBPreferencesDataSource
import com.onepercentbetter.core.datastore.test.InMemoryDataStore
import com.onepercentbetter.core.model.data.Topic
import com.onepercentbetter.core.network.model.NetworkTopic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class OfflineFirstTopicsRepositoryTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: OfflineFirstTopicsRepository

    private lateinit var topicDao: TopicDao

    private lateinit var network: TestOPBNetworkDataSource

    private lateinit var opbPreferences: OPBPreferencesDataSource

    private lateinit var synchronizer: Synchronizer

    @Before
    fun setup() {
        topicDao = TestTopicDao()
        network = TestOPBNetworkDataSource()
        opbPreferences = OPBPreferencesDataSource(InMemoryDataStore(_root_ide_package_.com.onepercentbetter.core.datastore.UserPreferences.getDefaultInstance()))
        synchronizer = TestSynchronizer(opbPreferences)

        subject = OfflineFirstTopicsRepository(
            topicDao = topicDao,
            network = network,
        )
    }

    @Test
    fun offlineFirstTopicsRepository_topics_stream_is_backed_by_topics_dao() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                topicDao.getTopicEntities()
                    .first()
                    .map(TopicEntity::asExternalModel),
                subject.getTopics()
                    .first(),
            )
        }

    @Test
    fun offlineFirstTopicsRepository_sync_pulls_from_network() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            val networkTopics = network.getTopics()
                .map(NetworkTopic::asEntity)

            val dbTopics = topicDao.getTopicEntities()
                .first()

            assertEquals(
                networkTopics.map(TopicEntity::id),
                dbTopics.map(TopicEntity::id),
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

            val networkTopics = network.getTopics()
                .map(NetworkTopic::asEntity)
                // Drop 10 to simulate the first 10 items being unchanged
                .drop(10)

            val dbTopics = topicDao.getTopicEntities()
                .first()

            assertEquals(
                networkTopics.map(TopicEntity::id),
                dbTopics.map(TopicEntity::id),
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
            val networkTopics = network.getTopics()
                .map(NetworkTopic::asEntity)
                .map(TopicEntity::asExternalModel)

            // Delete half of the items on the network
            val deletedItems = networkTopics
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

            val dbTopics = topicDao.getTopicEntities()
                .first()
                .map(TopicEntity::asExternalModel)

            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                networkTopics.map(Topic::id) - deletedItems,
                dbTopics.map(Topic::id),
            )

            // After sync version should be updated
            assertEquals(
                network.latestChangeListVersion(CollectionType.Topics),
                synchronizer.getChangeListVersions().topicVersion,
            )
        }
}
