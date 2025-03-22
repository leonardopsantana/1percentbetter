

package com.onepercentbetter.core.network.demo

import JvmUnitTestDemoAssetManager
import com.onepercentbetter.core.network.model.TaskResponse
import com.onepercentbetter.core.network.model.CategoryResponse
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DemoOPBNetworkDataSourceTest {

    private lateinit var subject: DemoOPBNetworkDataSource

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        subject = DemoOPBNetworkDataSource(
            ioDispatcher = testDispatcher,
            networkJson = Json { ignoreUnknownKeys = true },
            assets = JvmUnitTestDemoAssetManager,
        )
    }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun testDeserializationOfTopics() = runTest(testDispatcher) {
        assertEquals(
            CategoryResponse(
                id = "1",
                name = "Headlines",
                shortDescription = "News you'll definitely be interested in",
                longDescription = "The latest events and announcements from the world of Android development.",
                url = "",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            ),
            subject.getCategories().first(),
        )
    }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun testDeserializationOfNewsResources() = runTest(testDispatcher) {
        assertEquals(
            TaskResponse(
                id = "125",
                title = "Android Basics with Compose",
                content = "We released the first two units of Android Basics with Compose, our first free course that teaches Android Development with Jetpack Compose to anyone; you do not need any prior programming experience other than basic computer literacy to get started. ",
                url = "https://android-developers.googleblog.com/2022/05/new-android-basics-with-compose-course.html",
                headerImageUrl = "https://developer.android.com/images/hero-assets/android-basics-compose.svg",
                publishDate = LocalDateTime(
                    year = 2022,
                    monthNumber = 5,
                    dayOfMonth = 4,
                    hour = 23,
                    minute = 0,
                    second = 0,
                    nanosecond = 0,
                ).toInstant(TimeZone.UTC),
                type = "Codelab",
                topics = listOf("2", "3", "10"),
            ),
            subject.getTasksForDate().find { it.id == "125" },
        )
    }
}
