package com.cyb3rko.backpackdemo

import android.os.Bundle
import androidx.preference.SwitchPreferenceCompat
import com.cyb3rko.backpack.fragments.BackpackSettingsFragment
import com.cyb3rko.backpack.interfaces.BackpackSettingsView

internal class SettingsFragment: BackpackSettingsFragment(), BackpackSettingsView {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        bindInterface(this)
        super.onCreatePreferences(savedInstanceState, rootKey)

        findPreference<SwitchPreferenceCompat>(KEY_SETTING_EXAMPLE_2)?.bindRestartDialog()
    }

    override fun getPreferences(): Int {
        return R.xml.preferences
    }

    companion object {
        private const val KEY_SETTING_EXAMPLE_2 = "setting_example2"
    }
}
