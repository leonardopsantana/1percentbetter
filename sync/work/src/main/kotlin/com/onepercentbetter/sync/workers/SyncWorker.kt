

package com.onepercentbetter.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.tracing.traceAsync
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.onepercentbetter.core.analytics.AnalyticsHelper
import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.data.repository.category.CategoryRepository
import com.onepercentbetter.core.data.repository.task.TaskRepository
import com.onepercentbetter.core.datastore.OPBPreferencesDataSource
import com.onepercentbetter.core.network.Dispatcher
import com.onepercentbetter.core.network.OPBDispatchers.IO
import com.onepercentbetter.sync.initializers.SyncConstraints
import com.onepercentbetter.sync.initializers.syncForegroundInfo
import com.onepercentbetter.sync.status.SyncSubscriber
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val opbPreferences: OPBPreferencesDataSource,
    private val topicRepository: CategoryRepository,
    private val taskRepository: TaskRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
    private val syncSubscriber: SyncSubscriber,
) : CoroutineWorker(appContext, workerParams), Synchronizer {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        traceAsync("Sync", 0) {
            analyticsHelper.logSyncStarted()

            syncSubscriber.subscribe()

            // First sync the repositories in parallel
            val syncedSuccessfully = awaitAll(
                async { topicRepository.sync() },
                async { taskRepository.sync() },
            ).all { it }

            analyticsHelper.logSyncFinished(syncedSuccessfully)

            if (syncedSuccessfully) {
//                searchContentsRepository.populateFtsData()
                Result.success()
            } else {
                Result.retry()
            }
        }
    }

//    override suspend fun getChangeListVersions(): ChangeListVersions =
//        opbPreferences.getChangeListVersions()
//
//    override suspend fun updateChangeListVersions(
//        update: ChangeListVersions.() -> ChangeListVersions,
//    ) = opbPreferences.updateChangeListVersion(update)

    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}
