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

package com.cyb3rko.backpack.data

import androidx.annotation.DrawableRes
import com.cyb3rko.backpack.R

/**
 * The final data class for single app icon label combinations.
 * Configurable via the [AppIconFragment][com.cyb3rko.backpack.fragments.AppIconFragment]
 */
internal enum class AppIconLabel(
    val text: String,
    @DrawableRes val appIcon: Int?,
    val alias: String
) {
    DEFAULT("appName", null, "Default"),
    BACKPACK("appName", R.mipmap.ic_launcher_backpack, "Backpack"),
    NEWS("News", R.mipmap.ic_launcher_news, "News"),
    WEATHER("Weather", R.mipmap.ic_launcher_weather, "Weather"),
    NOTES("Notes", R.mipmap.ic_launcher_notes, "Notes");

    companion object {
        fun valueOf(index: Int)  = when (index) {
            0 -> DEFAULT
            1 -> BACKPACK
            2 -> NEWS
            3 -> WEATHER
            4 -> NOTES
            else -> DEFAULT
        }
    }
}
