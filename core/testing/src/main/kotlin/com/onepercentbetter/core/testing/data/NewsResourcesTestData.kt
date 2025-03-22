package com.onepercentbetter.core.testing.data

import com.onepercentbetter.core.model.data.CategoryModel
import com.onepercentbetter.core.model.data.TaskModel
import com.onepercentbetter.core.model.data.TaskWithCategoryModel
import kotlinx.datetime.Instant

val tasksTestData: List<TaskWithCategoryModel> = listOf(
    TaskWithCategoryModel(
        task = TaskModel(
            id = "1",
            title = "Android Basics with Compose",
            isDone = false,
            date = Instant.parse("2021-11-09T00:00:00.000Z"),
            categoryId = "1"
        ),
        category = CategoryModel(
            id = "1",
            title = "Android Basics with Compose",
            image = "https://developer.android.com/images/hero-assets/android-basics-compose.svg"
        )
    )
)
