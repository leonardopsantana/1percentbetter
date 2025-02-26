

package com.onepercentbetter.core.data.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.data.changeListSync
import com.onepercentbetter.core.data.model.asEntity
import com.onepercentbetter.core.data.model.topicCrossReferences
import com.onepercentbetter.core.data.model.topicEntityShells
import com.onepercentbetter.core.database.dao.NewsResourceDao
import com.onepercentbetter.core.database.dao.TopicDao
import com.onepercentbetter.core.database.model.PopulatedNewsResource
import com.onepercentbetter.core.database.model.TopicEntity
import com.onepercentbetter.core.database.model.asExternalModel
import com.onepercentbetter.core.datastore.ChangeListVersions
import com.onepercentbetter.core.datastore.OPBPreferencesDataSource
import com.onepercentbetter.core.model.data.NewsResource
import com.onepercentbetter.core.network.OPBNetworkDataSource
import com.onepercentbetter.core.network.model.NetworkNewsResource
import com.onepercentbetter.core.notifications.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Heuristic value to optimize for serialization and deserialization cost on client and server
// for each news resource batch.
private const val SYNC_BATCH_SIZE = 40

/**
 * Disk storage backed implementation of the [NewsRepository].
 * Reads are exclusively from local storage to support offline access.
 */
internal class OfflineFirstNewsRepository @Inject constructor(
    private val OPBPreferencesDataSource: OPBPreferencesDataSource,
    private val newsResourceDao: NewsResourceDao,
    private val topicDao: TopicDao,
    private val network: OPBNetworkDataSource,
    private val notifier: Notifier,
) : NewsRepository {

    override fun getNewsResources(
        query: NewsResourceQuery,
    ): Flow<List<NewsResource>> = newsResourceDao.getNewsResources(
        useFilterTopicIds = query.filterTopicIds != null,
        filterTopicIds = query.filterTopicIds ?: emptySet(),
        useFilterNewsIds = query.filterNewsIds != null,
        filterNewsIds = query.filterNewsIds ?: emptySet(),
    )
        .map { it.map(PopulatedNewsResource::asExternalModel) }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        var isFirstSync = false
        return synchronizer.changeListSync(
            versionReader = ChangeListVersions::newsResourceVersion,
            changeListFetcher = { currentVersion ->
                isFirstSync = currentVersion <= 0
                network.getNewsResourceChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion ->
                copy(newsResourceVersion = latestVersion)
            },
            modelDeleter = newsResourceDao::deleteNewsResources,
            modelUpdater = { changedIds ->
                val userData = OPBPreferencesDataSource.userData.first()
                val followedTopicIds = userData.followedTopics

                if (isFirstSync) {
                    // When we first retrieve news, mark everything viewed, so that we aren't
                    // overwhelmed with all historical news.
                    OPBPreferencesDataSource.setNewsResourcesViewed(changedIds, true)
                }

                // Obtain the news resources which have changed from the network and upsert them locally
                changedIds.chunked(SYNC_BATCH_SIZE).forEach { chunkedIds ->
                    val networkNewsResources = network.getNewsResources(ids = chunkedIds)

                    // Order of invocation matters to satisfy id and foreign key constraints!

                    topicDao.insertOrIgnoreTopics(
                        topicEntities = networkNewsResources
                            .map(NetworkNewsResource::topicEntityShells)
                            .flatten()
                            .distinctBy(TopicEntity::id),
                    )
                    newsResourceDao.upsertNewsResources(
                        newsResourceEntities = networkNewsResources.map(
                            NetworkNewsResource::asEntity,
                        ),
                    )
                    newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
                        newsResourceTopicCrossReferences = networkNewsResources
                            .map(NetworkNewsResource::topicCrossReferences)
                            .distinct()
                            .flatten(),
                    )
                }
            },
        )
    }
}
