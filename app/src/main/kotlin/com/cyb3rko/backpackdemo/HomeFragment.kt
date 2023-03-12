package com.cyb3rko.backpackdemo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavDirections
import com.cyb3rko.backpack.data.BuildInfo
import com.cyb3rko.backpack.interfaces.BackpackMain
import com.cyb3rko.backpack.fragments.BackpackMainFragment
import com.cyb3rko.backpackdemo.databinding.FragmentHomeBinding

internal class HomeFragment : BackpackMainFragment(), BackpackMain {
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindInterface(this)
        binding.backupFab.apply {
            setOnOpen { showToast("Backup menu opened") }
            setOnClose { showToast("Backup menu closed") }
            setOnImport { showToast("Import triggered") }
            setOnExport { showToast("Export triggered") }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(myContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun getSettingsIntent(): Intent {
        return Intent(myContext, SettingsActivity::class.java)
    }

    override fun getAnalysisNavigation(): NavDirections {
        return HomeFragmentDirections.homeToAnalysis()
    }

    override fun getGithubLink(): Int {
        return R.string.github_link
    }

    override fun getBuildInfo(): BuildInfo {
        return BuildInfo(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.BUILD_TYPE)
    }

    override fun getIconCredits(): Int {
        return R.string.icon_credits
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
