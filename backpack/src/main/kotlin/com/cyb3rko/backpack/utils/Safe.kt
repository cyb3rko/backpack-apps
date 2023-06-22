/*
 * Copyright (c) 2023 Cyb3rKo
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

package com.cyb3rko.backpack.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object Safe {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    fun initialize(context: Context) {
        if (!this::sharedPreferences.isInitialized) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            editor = sharedPreferences.edit()
        }
    }

    fun getBoolean(label: String, default: Boolean) = sharedPreferences.getBoolean(label, default)

    fun getInt(label: String, default: Int) = sharedPreferences.getInt(label, default)

    fun getString(label: String, default: String) = sharedPreferences.getString(label, default)

    fun writeBoolean(label: String, value: Boolean) = editor.putBoolean(label, value).apply()

    fun writeInt(label: String, value: Int) = editor.putInt(label, value).apply()

    fun writeString(label: String, value: String) = editor.putString(label, value).apply()
}
