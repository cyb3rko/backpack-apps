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
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import de.cyb3rko.backpack.R

/**
 * The helper class for providing an easy to use biometric prompt implementation including callbacks
 */
class BiometricAuthentication(private val context: Context) {
    private lateinit var onSucceeded: () -> Unit
    private lateinit var onFailed: (canAuthenticate: Boolean) -> Unit
    private val authenticators: Int by lazy {
        Authenticators.BIOMETRIC_WEAK or Authenticators.DEVICE_CREDENTIAL
    }
    private val getPromptInfo: BiometricPrompt.PromptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometry_lock_title))
            .setSubtitle(context.getString(R.string.biometry_lock_subtitle))
            .setAllowedAuthenticators(authenticators)
            .build()
    }

    fun onAuthenticationSucceeded(action: () -> Unit) {
        onSucceeded = action
    }

    fun onAuthenticationFailed(action: (canAuthenticate: Boolean) -> Unit) {
        onFailed = action
    }

    fun showPrompt() {
        BiometricPrompt(
            context as FragmentActivity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    Log.d(
                        "BackpackAuth",
                        "${decodeAuthenticationType(result)} Authentication succeeded"
                    )
                    onSucceeded()
                }

                override fun onAuthenticationError(errorCode: Int, errorString: CharSequence) {
                    Log.d("BackpackAuth", "Authentication failed: $errorString")
                    onFailed(canAuthenticate())
                }
            }
        ).apply {
            authenticate(getPromptInfo)
        }
    }

    private fun decodeAuthenticationType(result: BiometricPrompt.AuthenticationResult): String {
        return when (result.authenticationType) {
            BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC -> "Biometric"
            BiometricPrompt.AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL -> "Credential"
            else -> "Unknown"
        }
    }

    fun canAuthenticate(): Boolean {
        val manager = BiometricManager.from(context)
        val authPossibility = manager.canAuthenticate(authenticators)
        return authPossibility == BiometricManager.BIOMETRIC_SUCCESS
    }
}
