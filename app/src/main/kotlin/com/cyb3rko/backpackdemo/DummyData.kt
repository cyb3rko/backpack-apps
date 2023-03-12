package com.cyb3rko.backpackdemo

import com.cyb3rko.backpack.data.Serializable

internal class DummyData : Serializable() {
    @Suppress("UNUSED")
    val dummy = "Some data to be stored"

    companion object {
        private const val serialVersionUID = 4919742411796996764L
    }
}
