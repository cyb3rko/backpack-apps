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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import de.cyb3rko.backpack.R
import de.cyb3rko.backpack.databinding.BottomSheetNoticeBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NoticeBottomSheet(
    private val title: String,
    private val notice: String,
    private val action: () -> Unit): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        @SuppressLint("InflateParams")
        val layout = inflater.inflate(
            R.layout.bottom_sheet_notice,
            null
        ) as LinearLayout
        val binding = BottomSheetNoticeBinding.bind(layout)
        binding.title.text = title
        binding.notice.text = notice
        binding.okayButton.setOnClickListener {
            action()
            dismiss()
        }

        return layout
    }

    companion object {
        const val TAG = "Notice Bottom Sheet"
    }
}
