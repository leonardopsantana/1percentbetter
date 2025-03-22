

package com.onepercentbetter.core.data.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.data.model.asEntity
import com.onepercentbetter.core.data.model.topicCrossReferences
import com.onepercentbetter.core.data.model.topicEntityShells
import com.onepercentbetter.core.data.repository.task.NewsResourceQuery
import com.onepercentbetter.core.data.repository.task.OfflineFirstTaskRepository
import com.onepercentbetter.core.data.testdoubles.CollectionType
import com.onepercentbetter.core.data.testdoubles.TestCategoryDao
import com.onepercentbetter.core.data.testdoubles.TestNewsResourceDao
import com.onepercentbetter.core.data.testdoubles.TestOPBNetworkDataSource
import com.onepercentbetter.core.data.testdoubles.filteredInterestsIds
import com.onepercentbetter.core.data.testdoubles.nonPresentInterestsIds
import com.onepercentbetter.core.database.model.CategoryEntity
import com.onepercentbetter.core.database.model.PopulatedNewsResource
import com.onepercentbetter.core.database.model.TaskEntity
import com.onepercentbetter.core.database.model.asModel
import com.onepercentbetter.core.datastore.OPBPreferencesDataSource
import com.onepercentbetter.core.datastore.test.InMemoryDataStore
import com.onepercentbetter.core.network.model.TaskResponse
import com.onepercentbetter.core.testing.notifications.TestNotifier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OfflineFirstTaskRepositoryTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: OfflineFirstTaskRepository

    private lateinit var opbPreferencesDataSource: OPBPreferencesDataSource

    private lateinit var newsResourceDao: TestNewsResourceDao

    private lateinit var topicDao: TestCategoryDao

    private lateinit var network: TestOPBNetworkDataSource

    private lateinit var notifier: TestNotifier

    private lateinit var synchronizer: Synchronizer

    @Before
    fun setup() {
        opbPreferencesDataSource = OPBPreferencesDataSource(InMemoryDataStore(_root_ide_package_.com.onepercentbetter.core.datastore.UserPreferences.getDefaultInstance()))
        newsResourceDao = TestNewsResourceDao()
        topicDao = TestCategoryDao()
        network = TestOPBNetworkDataSource()
        notifier = TestNotifier()
        synchronizer = TestSynchronizer(
            opbPreferencesDataSource,
        )

        subject = OfflineFirstTaskRepository(
            OPBPreferencesDataSource = opbPreferencesDataSource,
            taskDao = newsResourceDao,
            categoryDao = topicDao,
            network = network,
            notifier = notifier,
        )
    }

    @Test
    fun offlineFirstNewsRepository_news_resources_stream_is_backed_by_news_resource_dao() =
        testScope.runTest {
            subject.syncWith(synchronizer)
            assertEquals(
                newsResourceDao.getRoutines()
                    .first()
                    .map(PopulatedNewsResource::asModel),
                subject.getTasksForDate()
                    .first(),
            )
        }

    @Test
    fun offlineFirstNewsRepository_news_resources_for_topic_is_backed_by_news_resource_dao() =
        testScope.runTest {
            assertEquals(
                expected = newsResourceDao.getRoutines(
                    filterTopicIds = filteredInterestsIds,
                    useFilterTopicIds = true,
                )
                    .first()
                    .map(PopulatedNewsResource::asModel),
                actual = subject.getTasksForDate(
                    query = NewsResourceQuery(
                        filterTopicIds = filteredInterestsIds,
                    ),
                )
                    .first(),
            )

            assertEquals(
                expected = emptyList(),
                actual = subject.getTasksForDate(
                    query = NewsResourceQuery(
                        filterTopicIds = nonPresentInterestsIds,
                    ),
                )
                    .first(),
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_pulls_from_network() =
        testScope.runTest {
            // User has not onboarded
            subject.syncWith(synchronizer)

            val newsResourcesFromNetwork = network.getTasksForDate()
                .map(TaskResponse::asEntity)
                .map(TaskEntity::asModel)

            val newsResourcesFromDb = newsResourceDao.getRoutines()
                .first()
                .map(PopulatedNewsResource::asModel)

            assertEquals(
                newsResourcesFromNetwork.map(NewsResource::id).sorted(),
                newsResourcesFromDb.map(NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(CollectionType.NewsResources),
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedNewsResources.isEmpty())
        }

    @Test
    fun offlineFirstNewsRepository_sync_deletes_items_marked_deleted_on_network() =
        testScope.runTest {
            // User has not onboarded

            val newsResourcesFromNetwork = network.getTasksForDate()
                .map(TaskResponse::asEntity)
                .map(TaskEntity::asModel)

            // Delete half of the items on the network
            val deletedItems = newsResourcesFromNetwork
                .map(NewsResource::id)
                .partition { it.chars().sum() % 2 == 0 }
                .first
                .toSet()

            deletedItems.forEach {
                network.editCollection(
                    collectionType = CollectionType.NewsResources,
                    id = it,
                    isDelete = true,
                )
            }

            subject.syncWith(synchronizer)

            val newsResourcesFromDb = newsResourceDao.getRoutines()
                .first()
                .map(PopulatedNewsResource::asModel)

            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                expected = (newsResourcesFromNetwork.map(NewsResource::id) - deletedItems).sorted(),
                actual = newsResourcesFromDb.map(NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(CollectionType.NewsResources),
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedNewsResources.isEmpty())
        }

    @Test
    fun offlineFirstNewsRepository_incremental_sync_pulls_from_network() =
        testScope.runTest {
            // Set news version to 7
            synchronizer.updateChangeListVersions {
                copy(newsResourceVersion = 7)
            }

            subject.syncWith(synchronizer)

            val changeList = network.changeListsAfter(
                CollectionType.NewsResources,
                version = 7,
            )
            val changeListIds = changeList
                .map(NetworkChangeList::id)
                .toSet()

            val newsResourcesFromNetwork = network.getTasksForDate()
                .map(TaskResponse::asEntity)
                .map(TaskEntity::asModel)
                .filter { it.id in changeListIds }

            val newsResourcesFromDb = newsResourceDao.getRoutines()
                .first()
                .map(PopulatedNewsResource::asModel)

            assertEquals(
                expected = newsResourcesFromNetwork.map(NewsResource::id).sorted(),
                actual = newsResourcesFromDb.map(NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = changeList.last().changeListVersion,
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedNewsResources.isEmpty())
        }

    @Test
    fun offlineFirstNewsRepository_sync_saves_shell_topic_entities() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                expected = network.getTasksForDate()
                    .map(TaskResponse::topicEntityShells)
                    .flatten()
                    .distinctBy(CategoryEntity::categoryId)
                    .sortedBy(CategoryEntity::toString),
                actual = topicDao.getTopicEntities()
                    .first()
                    .sortedBy(CategoryEntity::toString),
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_saves_topic_cross_references() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                expected = network.getTasksForDate()
                    .map(TaskResponse::topicCrossReferences)
                    .flatten()
                    .distinct()
                    .sortedBy(NewsResourceTopicCrossRef::toString),
                actual = newsResourceDao.topicCrossReferences
                    .sortedBy(NewsResourceTopicCrossRef::toString),
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_marks_as_read_on_first_run() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                network.getTasksForDate().map { it.id }.toSet(),
                opbPreferencesDataSource.userData.first().viewedNewsResources,
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_does_not_mark_as_read_on_subsequent_run() =
        testScope.runTest {
            // Pretend that we already have up to change list 7
            synchronizer.updateChangeListVersions {
                copy(newsResourceVersion = 7)
            }

            subject.syncWith(synchronizer)

            assertEquals(
                emptySet(),
                opbPreferencesDataSource.userData.first().viewedNewsResources,
            )
        }

    @Test
    fun offlineFirstNewsRepository_sends_notifications_for_newly_synced_news_that_is_followed() =
        testScope.runTest {
            val networkNewsResources = network.getTasksForDate()

            // Follow roughly half the topics
            val followedTopicIds = networkNewsResources
                .flatMap(TaskResponse::topicEntityShells)
                .mapNotNull { topic ->
                    when (topic.categoryId.chars().sum() % 2) {
                        0 -> topic.categoryId
                        else -> null
                    }
                }
                .toSet()

            // Set followed topics
            opbPreferencesDataSource.setFollowedTopicIds(followedTopicIds)

            subject.syncWith(synchronizer)

            val followedNewsResourceIdsFromNetwork = networkNewsResources
                .filter { (it.topics intersect followedTopicIds).isNotEmpty() }
                .map(TaskResponse::id)
                .sorted()

            // Notifier should have been called with only news resources that have topics
            // that the user follows
            assertEquals(
                expected = followedNewsResourceIdsFromNetwork,
                actual = notifier.addedNewsResources.first().map(NewsResource::id).sorted(),
            )
        }

    @Test
    fun offlineFirstNewsRepository_does_not_send_notifications_for_existing_news_resources() =
        testScope.runTest {
            val networkNewsResources = network.getTasksForDate()
                .map(TaskResponse::asEntity)

            val newsResources = networkNewsResources
                .map(TaskEntity::asModel)

            // Prepopulate dao with news resources
            newsResourceDao.upsertNewsResources(networkNewsResources)

            val followedTopicIds = newsResources
                .flatMap(NewsResource::topics)
                .map(Topic::id)
                .toSet()

            // Follow all topics
            opbPreferencesDataSource.setFollowedTopicIds(followedTopicIds)

            subject.syncWith(synchronizer)

            // Notifier should not have been called bc all news resources existed previously
            assertTrue(notifier.addedNewsResources.isEmpty())
        }
}
