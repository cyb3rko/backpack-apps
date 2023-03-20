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

package com.cyb3rko.backpack.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.cyb3rko.backpack.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Time helper functions

fun now() = System.currentTimeMillis()

// Logging helper functions

fun Any?.printToLog() = Log.d("Backpack", this.toString())

// View extension functions

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

// Context extension functions

fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.storeToClipboard(label: String, text: String) {
    val clip = ClipData.newPlainText(label, text)
    (this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(clip)
}

fun Context.openUrl(url: String, label: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        this.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        this.storeToClipboard(label, url)
        this.showToast(getString(R.string.toast_url_failed), Toast.LENGTH_LONG)
    }
}

internal fun Context.showDialog(
    title: String,
    message: CharSequence,
    icon: Int?,
    buttonClick: () -> Unit = {},
    actionMessage: String = "",
    cancelable: Boolean = true
) {
    val builder = MaterialAlertDialogBuilder(
        this,
        com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
    )
        .setTitle(title)
        .setMessage(message)
        .setCancelable(cancelable)

    if (icon != null) {
        builder.setIcon(ResourcesCompat.getDrawable(resources, icon, theme))
    }

    if (actionMessage.isNotBlank()) {
        builder.setPositiveButton(actionMessage) { _, _ ->
            buttonClick()
        }
    }
    builder.show()
}

// Fragment extension functions

fun Fragment.openUrl(url: String, label: String) {
    this.requireContext().openUrl(url, label)
}

// ByteArray extension functions

fun ByteArray.nthLast(n: Int) = this[this.size - n]

fun ByteArray.lastN(n: Int) = this.copyOfRange(this.size - n, this.size)

fun ByteArray.withoutLast() = this.copyOfRange(0, this.size - 1)

fun ByteArray.withoutLastN(n: Int) = this.copyOfRange(0, this.size - n)
