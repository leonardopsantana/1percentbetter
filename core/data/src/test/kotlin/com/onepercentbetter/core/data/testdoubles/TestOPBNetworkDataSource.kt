

package com.onepercentbetter.core.data.testdoubles

import com.onepercentbetter.core.data.testdoubles.CollectionType.NewsResources
import com.onepercentbetter.core.data.testdoubles.CollectionType.Topics
import com.onepercentbetter.core.network.OPBNetworkDataSource
import com.onepercentbetter.core.network.demo.DemoOPBNetworkDataSource
import com.onepercentbetter.core.network.model.TaskResponse
import com.onepercentbetter.core.network.model.CategoryResponse
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.serialization.json.Json

enum class CollectionType {
    Topics,
    NewsResources,
}

/**
 * Test double for [OPBNetworkDataSource]
 */
class TestOPBNetworkDataSource : OPBNetworkDataSource {

    private val source = DemoOPBNetworkDataSource(
        UnconfinedTestDispatcher(),
        Json { ignoreUnknownKeys = true },
    )

    private val allTopics = runBlocking { source.getCategories() }

    private val allNewsResources = runBlocking { source.getTasksForDate() }

    private val changeLists: MutableMap<CollectionType, List<NetworkChangeList>> = mutableMapOf(
        Topics to allTopics
            .mapToChangeList(idGetter = CategoryResponse::id),
        NewsResources to allNewsResources
            .mapToChangeList(idGetter = TaskResponse::id),
    )

    override suspend fun getCategories(ids: List<String>?): List<CategoryResponse> =
        allTopics.matchIds(
            ids = ids,
            idGetter = CategoryResponse::id,
        )

    override suspend fun getTasksForDate(ids: List<String>?): List<TaskResponse> =
        allNewsResources.matchIds(
            ids = ids,
            idGetter = TaskResponse::id,
        )

    override suspend fun getTopicChangeList(after: Int?): List<NetworkChangeList> =
        changeLists.getValue(Topics).after(after)

    override suspend fun getNewsResourceChangeList(after: Int?): List<NetworkChangeList> =
        changeLists.getValue(NewsResources).after(after)

    fun latestChangeListVersion(collectionType: CollectionType) =
        changeLists.getValue(collectionType).last().changeListVersion

    fun changeListsAfter(collectionType: CollectionType, version: Int) =
        changeLists.getValue(collectionType).after(version)

    /**
     * Edits the change list for the backing [collectionType] for the given [id] mimicking
     * the server's change list registry
     */
    fun editCollection(collectionType: CollectionType, id: String, isDelete: Boolean) {
        val changeList = changeLists.getValue(collectionType)
        val latestVersion = changeList.lastOrNull()?.changeListVersion ?: 0
        val change = NetworkChangeList(
            id = id,
            isDelete = isDelete,
            changeListVersion = latestVersion + 1,
        )
        changeLists[collectionType] = changeList.filterNot { it.id == id } + change
    }
}

fun List<NetworkChangeList>.after(version: Int?): List<NetworkChangeList> = when (version) {
    null -> this
    else -> filter { it.changeListVersion > version }
}

/**
 * Return items from [this] whose id defined by [idGetter] is in [ids] if [ids] is not null
 */
private fun <T> List<T>.matchIds(
    ids: List<String>?,
    idGetter: (T) -> String,
) = when (ids) {
    null -> this
    else -> ids.toSet().let { idSet -> filter { idGetter(it) in idSet } }
}

/**
 * Maps items to a change list where the change list version is denoted by the index of each item.
 * [after] simulates which models have changed by excluding items before it
 */
private fun <T> List<T>.mapToChangeList(
    idGetter: (T) -> String,
) = mapIndexed { index, item ->
    NetworkChangeList(
        id = idGetter(item),
        changeListVersion = index + 1,
        isDelete = false,
    )
}
