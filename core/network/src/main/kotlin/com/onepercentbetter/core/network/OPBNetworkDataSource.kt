

package com.onepercentbetter.core.network

import com.onepercentbetter.core.network.model.NetworkChangeList
import com.onepercentbetter.core.network.model.NetworkNewsResource
import com.onepercentbetter.core.network.model.NetworkTopic

/**
 * Interface representing network calls to the OPB backend
 */
interface OPBNetworkDataSource {
    suspend fun getTopics(ids: List<String>? = null): List<NetworkTopic>

    suspend fun getNewsResources(ids: List<String>? = null): List<NetworkNewsResource>

    suspend fun getTopicChangeList(after: Int? = null): List<NetworkChangeList>

    suspend fun getNewsResourceChangeList(after: Int? = null): List<NetworkChangeList>
}
