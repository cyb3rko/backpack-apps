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

package de.cyb3rko.backpack.modals

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.cyb3rko.backpack.databinding.DialogProgressBinding

class ProgressDialog(private val indeterminate: Boolean) {
    private lateinit var dialogReference: AlertDialog
    private lateinit var binding: DialogProgressBinding

    fun show(
        context: Context,
        @StringRes titleRes: Int,
        initialNote: String
    ) {
        binding = DialogProgressBinding.inflate((context as FragmentActivity).layoutInflater)
        binding.progressNote.text = initialNote
        if (indeterminate) binding.progressBar.isIndeterminate = true

        dialogReference = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setTitle(titleRes)
            .setView(binding.root)
            .create().apply {
                show()
            }
    }

    fun getProgress() = binding.progressBar.progress

    fun updateRelative(additionalProgress: Int) {
        binding.progressBar.progress = binding.progressBar.progress + additionalProgress
    }

    fun updateAbsolute(progress: Int) {
        binding.progressBar.progress = progress
    }

    fun updateText(message: String) {
        binding.progressNote.text = message
    }

    fun complete(message: String) {
        binding.progressBar.isIndeterminate = false
        binding.progressBar.progress = 100
        binding.progressNote.text = message
        dialogReference.setCancelable(true)
    }

    fun dismiss() {
        dialogReference.dismiss()
    }

    fun cancel() {
        dialogReference.cancel()
    }
}
