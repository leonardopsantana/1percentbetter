package com.onepercentbetter.core.network.retrofit

import androidx.tracing.trace
import com.onepercentbetter.core.network.OPBNetworkDataSource
import com.onepercentbetter.core.network.model.CategoryResponse
import com.onepercentbetter.core.network.model.TaskResponse
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for OPB Network API
 */
private interface RetrofitOpbNetworkApi {
    @GET(value = "categories")
    suspend fun getCategories(): NetworkResponse<List<CategoryResponse>>

    @GET(value = "task")
    suspend fun getTasksForDate(
        @Query("date") date: Instant,
    ): NetworkResponse<List<TaskResponse>>
}

private const val OPB_BASE_URL = _root_ide_package_.com.onepercentbetter.core.network.BuildConfig.BACKEND_URL

/**
 * Wrapper for data provided from the [OPB_BASE_URL]
 */
@Serializable
private data class NetworkResponse<T>(
    val data: T,
)

/**
 * [Retrofit] backed [OPBNetworkDataSource]
 */
@Singleton
internal class RetrofitOPBNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : OPBNetworkDataSource {

    private val networkApi = trace("RetrofitOPBNetwork") {
        Retrofit.Builder()
            .baseUrl(OPB_BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitOpbNetworkApi::class.java)
    }

    override suspend fun getCategories(): List<CategoryResponse> =
        networkApi.getCategories().data

    override suspend fun getTasksForDate(date: Instant): List<TaskResponse> =
        networkApi.getTasksForDate(date).data
}
