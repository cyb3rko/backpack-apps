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

package de.cyb3rko.backpack.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object Preferences {
    const val KEY_APP_LOCK = "app_lock"
    const val KEY_MATERIAL_YOU = "adaptive_colors"

    private lateinit var spf: SharedPreferences

    fun initialize(context: Context) {
        spf = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getBoolean(key: String, defaultValue: Boolean) = spf.getBoolean(key, defaultValue)

    fun getString(key: String, defaultValue: String) = spf.getString(key, defaultValue)!!

    fun setBoolean(key: String, value: Boolean) = spf.edit().putBoolean(key, value).apply()

    fun setString(key: String, value: String) = spf.edit().putString(key, value).apply()
}
