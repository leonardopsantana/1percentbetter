

package com.onepercentbetter.core.data.test.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.data.repository.TopicsRepository
import com.onepercentbetter.core.model.data.Topic
import com.onepercentbetter.core.network.Dispatcher
import com.onepercentbetter.core.network.OPBDispatchers.IO
import com.onepercentbetter.core.network.demo.DemoOPBNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Fake implementation of the [TopicsRepository] that retrieves the topics from a JSON String, and
 * uses a local DataStore instance to save and retrieve followed topic ids.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
internal class FakeTopicsRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: DemoOPBNetworkDataSource,
) : TopicsRepository {
    override fun getTopics(): Flow<List<Topic>> = flow {
        emit(
            datasource.getTopics().map {
                Topic(
                    id = it.id,
                    name = it.name,
                    shortDescription = it.shortDescription,
                    longDescription = it.longDescription,
                    url = it.url,
                    imageUrl = it.imageUrl,
                )
            },
        )
    }.flowOn(ioDispatcher)

    override fun getTopic(id: String): Flow<Topic> = getTopics()
        .map { it.first { topic -> topic.id == id } }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
