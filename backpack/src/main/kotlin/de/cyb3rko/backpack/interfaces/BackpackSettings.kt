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

package de.cyb3rko.backpack.interfaces

import androidx.annotation.XmlRes
import de.cyb3rko.backpack.fragments.BackpackSettingsFragment

/**
 * Interface for providing settings to the Backpack parent settings activity
 */
interface BackpackSettings {
    @XmlRes
    fun getPreferences(): Int

    fun getSettingsFragment(): BackpackSettingsFragment
}
