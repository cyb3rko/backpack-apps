package com.cyb3rko.backpackdemo

import android.os.Bundle
import androidx.navigation.findNavController
import com.cyb3rko.backpack.activities.BackpackMainActivity
import com.cyb3rko.backpackdemo.databinding.ActivityMainBinding

class MainActivity : BackpackMainActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).asContentView()
        setSupportActionBar(binding.toolbar)
        findNavController(R.id.nav_host_fragment_content_main).applyToActionBar()
    }
}
