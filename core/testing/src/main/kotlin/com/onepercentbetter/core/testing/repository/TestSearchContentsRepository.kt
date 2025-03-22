

package com.onepercentbetter.core.testing.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestSearchContentsRepository : SearchContentsRepository {

    private val cachedTopics = MutableStateFlow(emptyList<Topic>())
    private val cachedNewsResources = MutableStateFlow(emptyList<NewsResource>())

    override suspend fun populateFtsData() = Unit

    override fun searchContents(searchQuery: String): Flow<SearchResult> =
        combine(cachedTopics, cachedNewsResources) { topics, news ->
            SearchResult(
                topics = topics.filter {
                    searchQuery in it.name || searchQuery in it.shortDescription || searchQuery in it.longDescription
                },
                newsResources = news.filter {
                    searchQuery in it.content || searchQuery in it.title
                },
            )
        }

    override fun getSearchContentsCount(): Flow<Int> = combine(cachedTopics, cachedNewsResources) { topics, news -> topics.size + news.size }

    @TestOnly
    fun addTopics(topics: List<Topic>) = cachedTopics.update { it + topics }

    @TestOnly
    fun addNewsResources(newsResources: List<NewsResource>) =
        cachedNewsResources.update { it + newsResources }
}
