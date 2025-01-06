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

package com.cyb3rko.backpack.modals

import android.content.Context
import androidx.annotation.StringRes
import com.cyb3rko.backpack.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object ErrorDialog {
    fun show(
        context: Context,
        error: Exception,
        titleRes: Int = R.string.dialog_error_title
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(titleRes))
            .setMessage(error.message)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(context.getString(R.string.dialog_error_button2)) { _, _ ->
                throw error
            }
            .create()
            .show()
    }

    fun showCustom(
        context: Context,
        @StringRes titleRes: Int,
        @StringRes messageRes: Int
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(titleRes))
            .setMessage(context.getString(messageRes))
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()
    }
}
