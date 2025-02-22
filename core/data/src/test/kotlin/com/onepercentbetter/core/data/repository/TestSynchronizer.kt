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

package com.onepercentbetter.core.data.repository

import com.onepercentbetter.core.data.Synchronizer
import com.onepercentbetter.core.datastore.ChangeListVersions
import com.onepercentbetter.core.datastore.OPBPreferencesDataSource

/**
 * Test synchronizer that delegates to [OPBPreferencesDataSource]
 */
class TestSynchronizer(
    private val opbPreferences: OPBPreferencesDataSource,
) : Synchronizer {
    override suspend fun getChangeListVersions(): ChangeListVersions =
        opbPreferences.getChangeListVersions()

    override suspend fun updateChangeListVersions(
        update: ChangeListVersions.() -> ChangeListVersions,
    ) = opbPreferences.updateChangeListVersion(update)
}
