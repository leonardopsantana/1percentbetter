

package com.onepercentbetter.core.data.model

import com.onepercentbetter.core.database.model.TopicEntity
import com.onepercentbetter.core.network.model.NetworkTopic

fun NetworkTopic.asEntity() = TopicEntity(
    id = id,
    name = name,
    shortDescription = shortDescription,
    longDescription = longDescription,
    url = url,
    imageUrl = imageUrl,
)
