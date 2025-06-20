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

package de.cyb3rko.backpack.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import de.cyb3rko.backpack.R
import de.cyb3rko.backpack.interfaces.BackpackMainView
import de.cyb3rko.backpack.modals.AboutDialog
import de.cyb3rko.backpack.modals.LinksDialog
import de.cyb3rko.backpack.utils.Vibration

/**
 * The base fragment of Backpack apps' home fragment with predefined functionality:
 *
 * - menu options
 * - myContext variable
 * - vibrator object
 */
open class BackpackMainFragment : Fragment() {
    private lateinit var fragmentInterface: BackpackMainView
    protected lateinit var myContext: Context
    protected val vibrator by lazy { Vibration.getVibrator(myContext) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myContext = requireContext()
        addMenuProvider()
        return null
    }

    private fun addMenuProvider() {
        (requireActivity() as MenuHost).addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_home, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_links -> {
                            LinksDialog.show(myContext)
                            true
                        }
                        R.id.action_settings -> {
                            startActivity(fragmentInterface.getSettingsIntent())
                            true
                        }
                        R.id.action_about -> {
                            showAboutDialog()
                            true
                        }
                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    protected fun bindInterface(fragmentInterface: BackpackMainView) {
        this.fragmentInterface = fragmentInterface
    }

    private fun showAboutDialog() {
        AboutDialog.show(
            myContext,
            fragmentInterface.getBuildInfo(),
            fragmentInterface.getIconCredits()
        )
    }
}
