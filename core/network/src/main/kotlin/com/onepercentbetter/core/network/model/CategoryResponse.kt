package com.onepercentbetter.core.network.model

import com.onepercentbetter.core.model.data.CategoryModel
import kotlinx.serialization.Serializable

/**
 * Network representation of [CategoryResponse]
 */
@Serializable
data class CategoryResponse(
    val id: String,
    val title: String,
    val image: String,
)

fun CategoryResponse.asModel(): CategoryModel =
    CategoryModel(
        id = id,
        title = title,
        image = image,
    )
