package com.cyb3rko.backpackdemo

import android.os.Bundle
import com.cyb3rko.backpack.interfaces.BackpackSettings
import com.cyb3rko.backpack.activities.BackpackSettingsActivity
import com.cyb3rko.backpack.fragments.BackpackSettingsFragment

class SettingsActivity : BackpackSettingsActivity(), BackpackSettings {
    override fun onCreate(savedInstanceState: Bundle?) {
        bindInterface(this)
        super.onCreate(savedInstanceState)
    }

    override fun getPreferences(): Int {
        return R.xml.preferences
    }

    override fun getSettingsFragment(): BackpackSettingsFragment {
        return SettingsFragment()
    }
}
