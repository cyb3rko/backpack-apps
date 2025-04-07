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

package de.cyb3rko.backpack.activities

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import de.cyb3rko.backpack.R
import de.cyb3rko.backpack.databinding.ActivityAuthenticationBinding
import de.cyb3rko.backpack.utils.BiometricAuthentication
import de.cyb3rko.backpack.utils.Preferences
import de.cyb3rko.backpack.utils.show

/**
 * The activity for Backpack apps' authentication screen. To include in apps only add the activity
 * declaration to the app's manifest.
 */
class BackpackAuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    private val authentication by lazy {
        BiometricAuthentication(this).apply {
            onAuthenticationSucceeded {
                setResult(RESULT_OK)
                finish()
            }
            onAuthenticationFailed { canAuthenticate ->
                if (canAuthenticate) {
                    showErrorNote()
                } else {
                    Preferences.initialize(this@BackpackAuthenticationActivity)
                    Preferences.setBoolean(Preferences.KEY_APP_LOCK, false)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showRequirementNote()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.authenticateButton.setOnClickListener {
            authentication.showPrompt()
        }
        authentication.showPrompt()
    }

    private fun showErrorNote() {
        binding.errorView.text = getString(R.string.auth_failed)
        binding.errorView.show()
    }

    private fun showRequirementNote() {
        binding.errorView.text = getString(R.string.auth_required)
        binding.errorView.show()
    }
}
