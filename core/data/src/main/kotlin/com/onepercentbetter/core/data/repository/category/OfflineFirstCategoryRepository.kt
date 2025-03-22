/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onepercentbetter.core.data.repository.category

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.database.dao.CategoryDao
import com.onepercentbetter.core.model.data.CategoryModel
import com.onepercentbetter.core.network.OPBNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [CategoryRepository].
 * Reads are exclusively from local storage to support offline access.
 */
internal class OfflineFirstCategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val network: OPBNetworkDataSource,
) : CategoryRepository {

    override fun getCategories(): Flow<List<CategoryModel>> {
        return flow { listOf<CategoryModel>() }
//        return categoryDao.getCategories().map {
//            it.asModel()
//        }.asFlow()
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        TODO("Not yet implemented")
    }
}
