

package com.onepercentbetter.core.data.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.data.changeListSync
import com.onepercentbetter.core.data.model.asEntity
import com.onepercentbetter.core.database.dao.TopicDao
import com.onepercentbetter.core.database.model.TopicEntity
import com.onepercentbetter.core.database.model.asExternalModel
import com.onepercentbetter.core.datastore.ChangeListVersions
import com.onepercentbetter.core.model.data.Topic
import com.onepercentbetter.core.network.OPBNetworkDataSource
import com.onepercentbetter.core.network.model.NetworkTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [TopicsRepository].
 * Reads are exclusively from local storage to support offline access.
 */
internal class OfflineFirstTopicsRepository @Inject constructor(
    private val topicDao: TopicDao,
    private val network: OPBNetworkDataSource,
) : TopicsRepository {

    override fun getTopics(): Flow<List<Topic>> =
        topicDao.getTopicEntities()
            .map { it.map(TopicEntity::asExternalModel) }

    override fun getTopic(id: String): Flow<Topic> =
        topicDao.getTopicEntity(id).map { it.asExternalModel() }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.changeListSync(
            versionReader = ChangeListVersions::topicVersion,
            changeListFetcher = { currentVersion ->
                network.getTopicChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion ->
                copy(topicVersion = latestVersion)
            },
            modelDeleter = topicDao::deleteTopics,
            modelUpdater = { changedIds ->
                val networkTopics = network.getTopics(ids = changedIds)
                topicDao.upsertTopics(
                    entities = networkTopics.map(NetworkTopic::asEntity),
                )
            },
        )
}
