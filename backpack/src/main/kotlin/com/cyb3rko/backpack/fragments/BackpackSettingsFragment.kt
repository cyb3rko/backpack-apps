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

package com.cyb3rko.backpack.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.cyb3rko.backpack.R
import com.cyb3rko.backpack.interfaces.BackpackSettingsView
import com.cyb3rko.backpack.modals.ListPreferenceDialog
import com.cyb3rko.backpack.utils.BiometricAuthentication
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * The base fragment of Backpack apps' settings fragment with predefined functionality:
 *
 * - initialize preferences view
 * - initialize `Adaptive Colors` preference
 * - provide app restart dialog
 */
open class BackpackSettingsFragment : PreferenceFragmentCompat() {
    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var myContext: Context
    private lateinit var settingsInterface: BackpackSettingsView

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        myContext = requireContext()
        setPreferencesFromResource(settingsInterface.getPreferences(), rootKey)

        val keyAppLock = getString(R.string.preference_key_app_lock)
        val appLockPreference = findPreference<SwitchPreferenceCompat>(keyAppLock)
        if (appLockPreference != null) {
            var logMessage = "App lock preference found and initialized: "
            if (BiometricAuthentication(myContext).canAuthenticate()) {
                logMessage += "Enabled"
                appLockPreference.isEnabled = true
            } else {
                logMessage += "Disabled"
                appLockPreference.setSummary(R.string.preference_item_material_you_note)
                return
            }
            Log.i("Backpack", logMessage)
        } else {
            Log.e("Backpack", "App lock preference not found.")
        }

        val keyAdaptiveColors = getString(R.string.preference_key_adaptive_colors)
        val adaptiveColorsPreference = findPreference<SwitchPreferenceCompat>(keyAdaptiveColors)
        if (adaptiveColorsPreference == null) {
            Log.e("Backpack", "Adaptive Colors preference not found.")
            return
        }
        findPreference<SwitchPreferenceCompat>(keyAdaptiveColors)!!.let {
            var logMessage = "Adaptive Colors preference found and initialized: "
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                logMessage += "Enabled"
                it.isEnabled = true
            } else {
                logMessage += "Disabled"
                it.setSummary(R.string.preference_item_material_you_note)
                return
            }
            it.onPreferenceChangeListener = OnPreferenceChangeListener { _, _ ->
                showRestartDialog()
                true
            }
            Log.i("Backpack", logMessage)
        }
    }

    protected fun bindInterface(settingsInterface: BackpackSettingsView) {
        this.settingsInterface = settingsInterface
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun showRestartDialog() {
        MaterialAlertDialogBuilder(myContext)
            .setTitle(getString(R.string.dialog_restart_title))
            .setMessage(getString(R.string.dialog_restart_message))
            .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                val packageManager = requireActivity().packageManager
                val packageName = requireActivity().packageName
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                val componentName = intent!!.component
                val mainIntent = Intent.makeRestartActivityTask(componentName)
                startActivity(mainIntent)
                Runtime.getRuntime().exit(0)
            }
            .setNegativeButton(getString(R.string.dialog_restart_button2), null)
            .show()
    }

    protected fun SwitchPreferenceCompat.bindRestartDialog() {
        this.onPreferenceChangeListener = OnPreferenceChangeListener { _, _ ->
            showRestartDialog()
            true
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference is ListPreference) {
            showListPreferenceDialog(preference)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    private fun showListPreferenceDialog(preference: ListPreference) {
        ListPreferenceDialog().apply {
            arguments = bundleOf("key" to preference.key)
            @Suppress("DEPRECATION")
            setTargetFragment(this@BackpackSettingsFragment, 0)
            show(
                (myContext as FragmentActivity).supportFragmentManager,
                "androidx.preference.PreferenceFragment.DIALOG"
            )
        }
    }
}
