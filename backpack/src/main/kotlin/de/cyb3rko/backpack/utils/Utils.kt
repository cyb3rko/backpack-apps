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

package de.cyb3rko.backpack.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.cyb3rko.backpack.R
import java.text.SimpleDateFormat
import java.util.Date

// Time helper functions

fun now(): Long = System.currentTimeMillis()

fun dateNow(): Date = Date(now())

@SuppressLint("SimpleDateFormat")
fun Date.toFormattedString(): String = SimpleDateFormat("yyyyMMdd-HHmmss").format(this)

// Logging helper functions

fun Any?.printToLog() {
    Log.d("Backpack", this.toString())
}

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
        builder.setIcon(ResourcesCompat.getDrawable(resources, icon, null))
    }

    if (actionMessage.isNotBlank()) {
        builder.setPositiveButton(actionMessage) { _, _ ->
            buttonClick()
        }
    }
    builder.show()
}

fun Context.showDialogView(
    title: String,
    view: View,
    icon: Int?,
    cancelable: Boolean = true
) {
    val builder = MaterialAlertDialogBuilder(
        this,
        com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
    )
        .setTitle(title)
        .setView(view)
        .setCancelable(cancelable)

    if (icon != null) {
        builder.setIcon(
            ResourcesCompat.getDrawable(resources, icon, theme)
        )
    }
    builder.show()
}

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable {
    return AppCompatResources.getDrawable(this, id)!!
}

// Fragment extension functions

fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.requireContext(), message, length).show()
}

fun Fragment.openUrl(url: String, label: String) {
    this.requireContext().openUrl(url, label)
}

fun Fragment.showDialog(
    title: String,
    message: CharSequence,
    icon: Int?,
    actionMessage: String = "",
    cancelable: Boolean = true,
    buttonClick: () -> Unit = {},
) {
    requireContext().showDialog(title, message, icon, buttonClick, actionMessage, cancelable)
}

// ByteArray extension functions

fun ByteArray.firstN(n: Int) = this.copyOfRange(0, n)

fun ByteArray.nthLast(n: Int) = this[this.size - n]

fun ByteArray.lastN(n: Int) = this.copyOfRange(this.size - n, this.size)

fun ByteArray.withoutLast() = this.copyOfRange(0, this.size - 1)

fun ByteArray.withoutLastN(n: Int) = this.copyOfRange(0, this.size - n)
