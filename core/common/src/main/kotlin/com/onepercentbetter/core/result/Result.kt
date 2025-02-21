

package com.onepercentbetter.core.result

import com.onepercentbetter.core.result.Result.Error
import com.onepercentbetter.core.result.Result.Loading
import com.onepercentbetter.core.result.Result.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
    data object Loading : Result<Nothing>
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> = map<T, Result<T>> { Success(it) }
    .onStart { emit(Loading) }
    .catch { emit(Error(it)) }
