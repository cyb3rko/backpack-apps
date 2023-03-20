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
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewbinding.ViewBinding
import com.cyb3rko.backpack.BuildConfig
import com.cyb3rko.backpack.interfaces.BackpackMain
import com.cyb3rko.backpack.utils.Preferences

/**
 * The base activity of Backpack apps' main activity with predefined functionality:
 *
 * - screenshot protection for non-debug builds
 * - helper function to show app version in toolbar subtitle
 */
open class BackpackMainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var activityInterface: BackpackMain

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!BuildConfig.DEBUG) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setSupportActionBar(activityInterface.getToolbar())
        setupActionBarWithNavController(navController)
        Preferences.initialize(this)
    }

    fun showSubtitle(show: Boolean = true) {
        activityInterface.getToolbar().subtitle = if (show) {
            activityInterface.getVersionName()
        } else {
            ""
        }
    }

    protected fun <T>ViewBinding.asContentView(): T {
        setContentView(this.root)
        @Suppress("UNCHECKED_CAST")
        return (this as T)
    }

    protected fun NavController.apply() {
        navController = this
    }

    protected fun bindInterface(activityInterface: BackpackMain) {
        this.activityInterface = activityInterface
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
