

package com.onepercentbetter.core.network.demo

import JvmUnitTestDemoAssetManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import com.onepercentbetter.core.network.Dispatcher
import com.onepercentbetter.core.network.OPBDispatchers.IO
import com.onepercentbetter.core.network.OPBNetworkDataSource
import com.onepercentbetter.core.network.model.NetworkChangeList
import com.onepercentbetter.core.network.model.NetworkNewsResource
import com.onepercentbetter.core.network.model.NetworkTopic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.BufferedReader
import javax.inject.Inject

/**
 * [OPBNetworkDataSource] implementation that provides static news resources to aid development
 */
class DemoOPBNetworkDataSource @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkJson: Json,
    private val assets: DemoAssetManager = JvmUnitTestDemoAssetManager,
) : OPBNetworkDataSource {

    override suspend fun getTopics(ids: List<String>?): List<NetworkTopic> =
        getDataFromJsonFile(TOPICS_ASSET)

    override suspend fun getNewsResources(ids: List<String>?): List<NetworkNewsResource> =
        getDataFromJsonFile(NEWS_ASSET)

    override suspend fun getTopicChangeList(after: Int?): List<NetworkChangeList> =
        getTopics().mapToChangeList(NetworkTopic::id)

    override suspend fun getNewsResourceChangeList(after: Int?): List<NetworkChangeList> =
        getNewsResources().mapToChangeList(NetworkNewsResource::id)

    /**
     * Get data from the given JSON [fileName].
     */
    @OptIn(ExperimentalSerializationApi::class)
    private suspend inline fun <reified T> getDataFromJsonFile(fileName: String): List<T> =
        withContext(ioDispatcher) {
            assets.open(fileName).use { inputStream ->
                if (SDK_INT <= M) {
                    /**
                     * On API 23 (M) and below we must use a workaround to avoid an exception being
                     * thrown during deserialization. See:
                     * https://github.com/Kotlin/kotlinx.serialization/issues/2457#issuecomment-1786923342
                     */
                    inputStream.bufferedReader().use(BufferedReader::readText)
                        .let(networkJson::decodeFromString)
                } else {
                    networkJson.decodeFromStream(inputStream)
                }
            }
        }

    companion object {
        private const val NEWS_ASSET = "news.json"
        private const val TOPICS_ASSET = "topics.json"
    }
}

/**
 * Converts a list of [T] to change list of all the items in it where [idGetter] defines the
 * [NetworkChangeList.id]
 */
private fun <T> List<T>.mapToChangeList(
    idGetter: (T) -> String,
) = mapIndexed { index, item ->
    NetworkChangeList(
        id = idGetter(item),
        changeListVersion = index,
        isDelete = false,
    )
}
