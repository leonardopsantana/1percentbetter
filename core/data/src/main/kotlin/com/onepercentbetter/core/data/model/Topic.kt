package com.onepercentbetter.core.data.model

import com.onepercentbetter.core.database.model.CategoryEntity
import com.onepercentbetter.core.network.model.CategoryResponse

fun CategoryResponse.asEntity() = CategoryEntity(
    categoryId = id,
    title = title,
    image = image,
)
