/*
 * Copyright (c) 2023-2025 Cyb3rKo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cyb3rko.backpack

import android.app.Application
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors

/**
 * The extended Backpack Application with predefined functionality:
 *
 * - adapt theming to Material You setting on start
 *
 * Can directly be referenced in app's manifest.
 */
open class BackpackApplication : Application() {
    override fun onCreate() {
        val spf = PreferenceManager.getDefaultSharedPreferences(this)
        if (spf.getBoolean(getString(R.string.preference_key_adaptive_colors), false)) {
            DynamicColors.applyToActivitiesIfAvailable(this)
        }
        super.onCreate()
    }
}
