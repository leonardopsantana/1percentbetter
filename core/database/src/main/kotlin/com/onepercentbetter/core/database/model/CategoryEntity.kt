package com.onepercentbetter.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.onepercentbetter.core.model.data.CategoryModel

/**
 * Defines a topic a user may follow.
 * It has a many to many relationship with [TaskEntity]
 */
@Entity(
    tableName = "categories",
)
data class CategoryEntity(
    @PrimaryKey
    val categoryId: String,
    val title: String,
    val image: String,
)

fun CategoryEntity.asModel() = CategoryModel(
    id = categoryId,
    title = title,
    image = image,
)
