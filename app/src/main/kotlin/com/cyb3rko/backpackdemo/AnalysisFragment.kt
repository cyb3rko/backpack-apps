package com.cyb3rko.backpackdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyb3rko.backpack.data.Serializable
import com.cyb3rko.backpack.fragments.BackpackAnalysisFragment
import com.cyb3rko.backpack.interfaces.BackpackAnalysis

internal class AnalysisFragment : BackpackAnalysisFragment(), BackpackAnalysis {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindInterface(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun useRandom() = true

    override fun getDemoData(): Serializable {
        return DummyData()
    }
}
