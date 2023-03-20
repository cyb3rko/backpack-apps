package com.cyb3rko.backpackdemo

import android.os.Bundle
import androidx.navigation.findNavController
import com.cyb3rko.backpack.activities.BackpackMainActivity
import com.cyb3rko.backpack.interfaces.BackpackMain
import com.cyb3rko.backpackdemo.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : BackpackMainActivity(), BackpackMain {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).asContentView()
        findNavController(R.id.nav_host_fragment_content_main).apply()
        bindInterface(this)
    }

    override fun getToolbar(): MaterialToolbar {
        return binding.toolbar
    }

    override fun getVersionName(): String {
        return "1.0.0"
    }
}
