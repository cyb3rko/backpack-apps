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

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.cyb3rko.backpack.R
import com.cyb3rko.backpack.data.AppIconLabel
import com.cyb3rko.backpack.databinding.FragmentAppIconBinding
import com.cyb3rko.backpack.utils.showDialog
import com.cyb3rko.backpack.utils.showToast
import com.google.android.material.imageview.ShapeableImageView

/**
 * The final fragment of Backpack apps' app icon configuration fragment with full functionality
 * out of the box.
 */
open class AppIconFragment(
    private val packageMainActivity: String,
    private val appName: String,
    private val appIcon: Drawable
) : Fragment() {
    private var _binding: FragmentAppIconBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var myContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppIconBinding.inflate(inflater, container, false)
        myContext = requireContext()

        val currentConfig = readCurrentAppIconLabel()
        Log.i("Backpack", "Found current icon $currentConfig")

        var appIconLabel: AppIconLabel
        var appIconId: Int?
        var imageView: ShapeableImageView
        var textView: TextView
        lateinit var selectedImageView: ShapeableImageView
        (binding.root[0] as ViewGroup).children.forEachIndexed { index, view ->
            // Skip TextView
            if (index == 0) return@forEachIndexed

            view as LinearLayout
            appIconLabel = AppIconLabel.valueOf(index - 1)
            appIconId = appIconLabel.appIcon
            imageView = view[0] as ShapeableImageView
            textView = view[1] as TextView

            if (appIconId != null) {
                imageView.setImageDrawable(appIconId!!)
            } else {
                imageView.setImageDrawable(appIcon)
            }
            imageView.tag = index - 1

            // Highlight current configuration
            if (currentConfig == appIconLabel) {
                imageView.highlight()
                selectedImageView = imageView
            }

            imageView.setOnClickListener {
                appIconLabel = AppIconLabel.valueOf((view[0] as ShapeableImageView).tag as Int)
                if (currentConfig == appIconLabel) return@setOnClickListener

                showRestartDialog {
                    selectedImageView.unhighlight()
                    selectedImageView = (view[0] as ShapeableImageView)
                    selectedImageView.highlight()
                    activateActivityAlias(appIconLabel)
                    showToast(getString(R.string.preference_app_icon_toast))
                }
            }
            textView.text = if (appIconLabel.text != "appName") appIconLabel.text else appName
        }

        return binding.root
    }

    private fun readCurrentAppIconLabel(): AppIconLabel {
        val pm = myContext.packageManager
        val activeIcon = enumValues<AppIconLabel>().first {
            val componentName = ComponentName(
                myContext,
                packageMainActivity + it.alias
            )
            val componentEnabledSetting = pm.getComponentEnabledSetting(componentName)
            if (it == AppIconLabel.DEFAULT
                && componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
            ) {
                return it
            }

            componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }

        return activeIcon
    }

    private fun activateActivityAlias(newAppIconLabel: AppIconLabel) {
        Log.i("Backpack", "Changing app label to ${newAppIconLabel.name}")
        enumValues<AppIconLabel>().forEach { item ->
            if (item != newAppIconLabel) {
                activateActivityAlias(item.alias, PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
            } else {
                activateActivityAlias(item.alias, PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            }
        }
    }

    private fun activateActivityAlias(alias: String, enabledFlag: Int) {
        myContext.packageManager.setComponentEnabledSetting(
            ComponentName(myContext, packageMainActivity + alias),
            enabledFlag,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun showRestartDialog(onConfirm: () -> Unit) {
        showDialog(
            title = getString(R.string.preference_app_icon_dialog_title),
            message = getString(R.string.preference_app_icon_dialog_message),
            icon = null,
            actionMessage = getString(R.string.preference_app_icon_dialog_button)
        ) {
            onConfirm()
        }
    }

    private fun ShapeableImageView.setImageDrawable(@DrawableRes drawableId: Int) {
        this.setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, null))
    }

    private fun ShapeableImageView.highlight() {
        this.strokeWidth = 14f
    }

    private fun ShapeableImageView.unhighlight() {
        this.strokeWidth = 0f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
