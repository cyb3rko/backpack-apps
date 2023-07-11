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

package com.cyb3rko.backpack.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.cyb3rko.backpack.databinding.ActivitySettingsBinding
import com.cyb3rko.backpack.interfaces.BackpackSettings

/**
 * The base activity of Backpack apps' settings activity with predefined functionality:
 *
 * - initializing app bar
 * - set preferences' default values
 * - show Settings fragment
 */
open class BackpackSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsInterface: BackpackSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val containerId = binding.settingsContainer.id
        val fragment = settingsInterface.getSettingsFragment().apply {
            this.containerId = containerId
        }
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, fragment)
            .commit()

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        PreferenceManager.setDefaultValues(this, settingsInterface.getPreferences(), false)
    }

    protected fun bindInterface(settingsInterface: BackpackSettings) {
        this.settingsInterface = settingsInterface
    }
}
