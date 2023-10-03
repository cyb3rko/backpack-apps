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

package com.cyb3rko.backpack.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cyb3rko.backpack.R
import com.cyb3rko.backpack.crypto.CryptoManager
import com.cyb3rko.backpack.crypto.CryptoManager.EnDecryptionException
import com.cyb3rko.backpack.databinding.FragmentAnalysisBinding
import com.cyb3rko.backpack.interfaces.BackpackAnalysis
import com.cyb3rko.backpack.modals.ErrorDialog
import com.cyb3rko.backpack.utils.show
import com.cyb3rko.backpack.utils.ObjectSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * The base fragment of Backpack apps' analysis fragment with full functionality without
 * adding functionality when extending
 */
open class BackpackAnalysisFragment : Fragment() {
    private var _binding: FragmentAnalysisBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var myContext: Context
    private lateinit var fragmentInterface: BackpackAnalysis
    private var argonRunning = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        myContext = requireContext()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (fragmentInterface.useRandom()) {
            binding.randomSourceCard.show()
        }
        binding.argonHashingCard.setOnClickListener {
            if (argonRunning) return@setOnClickListener
            argonRunning = true
            binding.argonHashingProgress.show()
            lifecycleScope.launch(Dispatchers.IO) {
                val hashingTime = runArgonHash()
                argonRunning = false
                withContext(Dispatchers.Main) {
                    binding.argonHashingProgress.hide()
                    binding.argonHashingSpeed.text = getString(
                        R.string.analysis_argon_hashing_result,
                        hashingTime.toString()
                    )
                }
            }
        }
        CryptoManager.xxHash("") // initialize hash mechanism
        binding.xxhashingCard.setOnClickListener {
            val hashingTime = runxxHash()
            binding.xxhashingSpeed.text = getString(
                R.string.analysis_xxhashing_result,
                hashingTime.toString()
            )
        }

        binding.encryptionCard.setOnClickListener { runEncryption() }
        binding.decryptionCard.setOnClickListener { runDecryption() }

        val kP = getKeyStoreProvider()
        binding.keystoreProvider.text = getString(
            R.string.analysis_provider_info,
            kP.name,
            kP.version.toString(),
            kP.info
        )

        if (fragmentInterface.useRandom()) {
            val sRP = getSecureRandomProvider()
            binding.randomSource.text = getString(
                R.string.analysis_provider_info,
                sRP.name,
                sRP.version.toString(),
                sRP.info
            )
            binding.randomSourceCard.show()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun runEncryption() {
        try {
            val tempFile = File(myContext.filesDir, "enc-test")
            tempFile.createNewFile()
            val time = measureTime {
                CryptoManager.encrypt(
                    ObjectSerializer.serialize(fragmentInterface.getDemoData()),
                    tempFile
                )
            }
            tempFile.delete()

            binding.encryptionSpeed.text = getString(
                R.string.analysis_encryption_result,
                time.toString()
            )
        } catch (e: EnDecryptionException) {
            Log.d("CryptoManager", e.customStacktrace)
            ErrorDialog.show(myContext, e)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun runDecryption() {
        try {
            val tempFile = File(myContext.filesDir, "dec-test")
            tempFile.createNewFile()
            CryptoManager.encrypt(
                ObjectSerializer.serialize(fragmentInterface.useRandom()),
                tempFile
            )
            val time = measureTime { CryptoManager.decrypt(tempFile) }
            tempFile.delete()

            binding.decryptionSpeed.text = getString(
                R.string.analysis_decryption_result,
                time.toString()
            )
        } catch (e: EnDecryptionException) {
            Log.d("CryptoManager", e.customStacktrace)
            ErrorDialog.show(myContext, e)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun runArgonHash() = measureTime { CryptoManager.argon2Hash("This is a test") }

    @OptIn(ExperimentalTime::class)
    private fun runxxHash() = measureTime { CryptoManager.xxHash("This is a test") }

    private fun getKeyStoreProvider() = KeyStore.getInstance("AndroidKeyStore").provider

    private fun getSecureRandomProvider() = SecureRandom().provider

    protected fun bindInterface(fragmentInterface: BackpackAnalysis) {
        this.fragmentInterface = fragmentInterface
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
