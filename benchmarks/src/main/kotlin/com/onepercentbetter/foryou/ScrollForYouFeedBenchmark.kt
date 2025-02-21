

package com.onepercentbetter.foryou

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ScrollForYouFeedBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollFeedCompilationNone() = scrollFeed(CompilationMode.None())

    @Test
    fun scrollFeedCompilationBaselineProfile() = scrollFeed(CompilationMode.Partial())

    @Test
    fun scrollFeedCompilationFull() = scrollFeed(CompilationMode.Full())

    private fun scrollFeed(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = StartupMode.WARM,
        setupBlock = {
            // Start the app
            pressHome()
            startActivityAndAllowNotifications()
        },
    ) {
        forYouWaitForContent()
        forYouSelectTopics()
        forYouScrollFeedDownUp()
    }
}
