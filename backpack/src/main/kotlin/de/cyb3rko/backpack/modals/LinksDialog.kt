/*
 * Copyright (c) 2025 Cyb3rKo
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
import androidx.fragment.app.FragmentActivity
import de.cyb3rko.backpack.databinding.DialogLinksBinding
import de.cyb3rko.backpack.R
import de.cyb3rko.backpack.utils.openUrl
import de.cyb3rko.backpack.utils.showDialogView

object LinksDialog {
    fun show(context: Context) {
        val view = DialogLinksBinding.inflate((context as FragmentActivity).layoutInflater)
        view.githubButton.setOnClickListener {
            context.openUrl("https://github.com/cyb3rko/backpack-apps", "Backpack Apps GitHub repository")
        }
        view.matrixButton.setOnClickListener {
            context.openUrl("https://matrix.to/#/#cyb3rko-community:matrix.org", "Cyb3rKo Matrix room")
        }
        view.donateButton.setOnClickListener {
            context.openUrl("https://github.com/cyb3rko/backpack-apps#donate", "Backpack Apps donation links")
        }
        context.showDialogView(
            title = context.getString(R.string.dialog_links_title),
            view = view.root,
            icon = R.drawable.ic_link
        )
    }
}
