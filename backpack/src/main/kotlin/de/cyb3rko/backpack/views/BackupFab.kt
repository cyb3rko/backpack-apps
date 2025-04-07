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

package de.cyb3rko.backpack.views

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import de.cyb3rko.backpack.R
import de.cyb3rko.backpack.databinding.BackupFabBinding
import de.cyb3rko.backpack.utils.hide
import de.cyb3rko.backpack.utils.show

/**
 * The FloatingActionButton for importing and exporting of Backpack apps' content.
 *
 * Positioned in the bottom left corner and expands by onClick.
 *
 * Includes transformation animations.
 */
class BackupFab(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private var binding: BackupFabBinding

    private var isFabOpen = false
    private var onOpen: () -> Unit = {}
    private var onClose: () -> Unit = {}
    private var onExport: () -> Unit = {}
    private var onImport: () -> Unit = {}

    init {
        inflate(context, R.layout.backup_fab, this)
        binding = BackupFabBinding.bind(rootView).apply {
            topFab.setOnClickListener {
                if (!isFabOpen) showFabMenu() else closeFabMenu()
            }
            fabBgLayout.setOnClickListener {
                closeFabMenu()
            }
            fabMenu1.setOnClickListener {
                closeFabMenu()
                onExport()
            }
            fabMenu2.setOnClickListener {
                closeFabMenu()
                onImport()
            }
        }
    }

    fun setOnOpen(action: () -> Unit) {
        onOpen = action
    }

    fun setOnClose(action: () -> Unit) {
        onClose = action
    }

    fun setOnExport(action: () -> Unit) {
        onExport = action
    }

    fun setOnImport(action: () -> Unit) {
        onImport = action
    }

    private fun showFabMenu() {
        isFabOpen = true
        binding.topFab.animate().rotation(180f)
        binding.fabLayout1.show()
        binding.fabLayout2.show()
        binding.fabBgLayout.show()
        binding.fabLayout1.animate().translationY(-resources.getDimension(R.dimen.fab_menu1))
        binding.fabLayout2.animate().translationY(-resources.getDimension(R.dimen.fab_menu2))
        onOpen()
    }

    private fun closeFabMenu() {
        isFabOpen = false
        binding.topFab.animate().rotation(0f)
        binding.fabBgLayout.hide()
        binding.fabLayout1.animate().translationY(0f)
        binding.fabLayout2.animate().translationY(0f)
        binding.fabLayout2.animate().translationY(0f).setListener(
            object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    if (!isFabOpen) {
                        binding.fabLayout1.hide()
                        binding.fabLayout2.hide()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            }
        )
        onClose()
    }
}
