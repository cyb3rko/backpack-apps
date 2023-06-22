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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewbinding.ViewBinding
import com.cyb3rko.backpack.BuildConfig
import com.cyb3rko.backpack.interfaces.BackpackMain
import com.cyb3rko.backpack.utils.Preferences
import com.cyb3rko.backpack.utils.Safe
import com.cyb3rko.backpack.utils.now

/**
 * The base activity of Backpack apps' main activity with predefined functionality:
 *
 * - automatic user authentication (timeout: 20 seconds)
 * - screenshot protection for non-debug builds
 * - helper function to show app version in toolbar subtitle
 */
open class BackpackMainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var activityInterface: BackpackMain
    private var latestAuthentication: Long = -1

    private val authenticationResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            Log.d("BackpackAuth", "Caught Auth Activity result")
            if (result.resultCode == Activity.RESULT_OK) {
                latestAuthentication = now()
                setContentView(activityInterface.getBinding().root)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!BuildConfig.DEBUG) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        super.onCreate(savedInstanceState)
        Safe.initialize(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setSupportActionBar(activityInterface.getToolbar())
        setupActionBarWithNavController(navController)
        Preferences.initialize(this)
    }

    override fun onPause() {
        if (Preferences.getBoolean(Preferences.KEY_APP_LOCK, false)) {
            setContentView(View(this))
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (Preferences.getBoolean(Preferences.KEY_APP_LOCK, false)) {
            if (latestAuthentication == -1L || now() - latestAuthentication > 10000) {
                authenticationResultLauncher.launch(
                    Intent(applicationContext, BackpackAuthenticationActivity::class.java)
                )
            } else {
                setContentView(activityInterface.getBinding().root)
            }
        } else {
            setContentView(activityInterface.getBinding().root)
        }
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
