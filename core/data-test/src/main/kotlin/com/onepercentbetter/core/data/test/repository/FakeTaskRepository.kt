

package com.onepercentbetter.core.data.test.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.data.model.asExternalModel
import com.onepercentbetter.core.data.repository.task.NewsResourceQuery
import com.onepercentbetter.core.data.repository.task.TaskRepository
import com.onepercentbetter.core.network.Dispatcher
import com.onepercentbetter.core.network.OPBDispatchers.IO
import com.onepercentbetter.core.network.demo.DemoOPBNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Fake implementation of the [TaskRepository] that retrieves the news resources from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeTaskRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: DemoOPBNetworkDataSource,
) : TaskRepository {

    override fun getNewsResources(
        query: NewsResourceQuery,
    ): Flow<List<NewsResource>> =
        flow {
            val newsResources = datasource.getTasksForDate()
            val topics = datasource.getCategories()

            emit(
                newsResources
                    .filter { networkNewsResource ->
                        // Filter out any news resources which don't match the current query.
                        // If no query parameters (filterTopicIds or filterNewsIds) are specified
                        // then the news resource is returned.
                        listOfNotNull(
                            true,
                            query.filterNewsIds?.contains(networkNewsResource.id),
                            query.filterTopicIds?.let { filterTopicIds ->
                                networkNewsResource.topics.intersect(filterTopicIds).isNotEmpty()
                            },
                        )
                            .all(true::equals)
                    }
                    .map { it.asExternalModel(topics) },
            )
        }.flowOn(ioDispatcher)

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
