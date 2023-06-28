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

package com.cyb3rko.backpack.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.cyb3rko.backpack.BuildConfig
import com.cyb3rko.backpack.crypto.xxhash3.XXH3_128
import com.cyb3rko.backpack.utils.ObjectSerializer
import com.cyb3rko.backpack.utils.firstN
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import com.lambdapioneer.argon2kt.Argon2Version
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.Provider
import java.security.SecureRandom
import java.security.Security
import java.util.LinkedList
import java.util.Queue
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.random.Random

/**
 * The manager object for cryptographic functions like hashing, encryption and (secure) randoms.
 */
object CryptoManager {
    private object AesGcmSpecs {
        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        const val AAD_LENGTH = 16
        const val TAG_LENGTH = 16

        override fun toString(): String {
            return "$ALGORITHM/$BLOCK_MODE/$PADDING"
        }
    }
    private object Argon2Specs {
        val MODE = Argon2Mode.ARGON2_ID
        val VERSION = Argon2Version.V13
        const val ITERATIONS = 10
        const val KIBI_BYTE = 65_536
        const val HASH_LENGTH = 32
    }
    private object Sha512Specs {
        const val ALGORITHM = "SHA-512"
        const val STRETCH_ITERATIONS = 250_000
    }

    private val KEYSTORE_ALIAS = if (!BuildConfig.DEBUG) "iamsecure" else "iamsecuredebug"

    private val argon2Kt by lazy { Argon2Kt() }
    private val sha512Digest by lazy { MessageDigest.getInstance(Sha512Specs.ALGORITHM) }
    private val secureRandom by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SecureRandom.getInstanceStrong()
        } else {
            SecureRandom()
        }
    }

    // Hashing

    fun xxHash(plaintext: String): String {
        val ciphertextBytes = XXH3_128().digest(plaintext.toByteArray())
        val sb = StringBuilder()
        for (i in ciphertextBytes.indices) {
            sb.append(((ciphertextBytes[i] and 0xff.toByte()) + 0x100).toString(16).substring(1))
        }
        val hash = sb.toString()
        if (BuildConfig.DEBUG) {
            Log.d("CryptoManager", "Hash: $hash")
        }
        return hash
    }

    fun argon2Hash(plaintext: String): Hash {
        val salt = getSalt()
        val hash = argon2Kt.hash(
            mode = Argon2Specs.MODE,
            password = plaintext.toByteArray(),
            salt = salt,
            tCostInIterations = Argon2Specs.ITERATIONS,
            mCostInKibibyte = Argon2Specs.KIBI_BYTE,
            hashLengthInBytes = Argon2Specs.HASH_LENGTH, // Byte -> 256 Bit
            version = Argon2Specs.VERSION
        )
        return Hash(hash.rawHashAsByteArray(), salt)
    }

    private fun argon2HashWithKey(ciphertext: String, salt: ByteArray): ByteArray {
        return argon2Kt.hash(
            mode = Argon2Specs.MODE,
            password = ciphertext.toByteArray(),
            salt = salt,
            tCostInIterations = Argon2Specs.ITERATIONS,
            mCostInKibibyte = Argon2Specs.KIBI_BYTE,
            hashLengthInBytes = Argon2Specs.HASH_LENGTH, // Byte -> 256 Bit
            version = Argon2Specs.VERSION
        ).rawHashAsByteArray()
    }

    fun shaHash(
        plaintext: String,
        rounds: Int = Sha512Specs.STRETCH_ITERATIONS
    ): Hash {
        var data = plaintext.toByteArray()
        val salt = getSalt()
        sha512Digest.update(salt)
        repeat(rounds) {
            sha512Digest.update(data)
            data = sha512Digest.digest()
        }
        return Hash(data, salt)
    }

    private fun shaHashWithKey(
        plaintext: String,
        salt: ByteArray,
        rounds: Int = Sha512Specs.STRETCH_ITERATIONS
    ): ByteArray {
        var data = plaintext.toByteArray()
        sha512Digest.update(salt)
        repeat(rounds) {
            sha512Digest.update(data)
            data = sha512Digest.digest()
        }
        return data
    }

    private fun ByteArray.toHex(): String {
        return joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
    }

    // Random

    fun getRandom(from: Int, until: Int) = Random.nextInt(from, until)

    fun getSecureRandom(until: Int, offset: Int = 0) = secureRandom.nextInt(until) + offset

    private fun getSalt() = getSecureBytes(32)

    private fun getSecureBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        secureRandom.nextBytes(bytes)
        return bytes
    }

    // Encryption / Decryption

    private val keyStore = getKeyStoreInstance(Security.getProviders().reversed(), 0)

    private fun getKeyStoreInstance(providers: List<Provider>, index: Int): KeyStore {
        val providerName = providers[index].name
        return try {
            Log.i("CryptoManager", "Getting provider $providerName")
            KeyStore.getInstance(providerName).apply {
                load(null)
            }
        } catch (e: Exception) {
            Log.w("CryptoManager", "Provider $providerName not found")
            getKeyStoreInstance(providers, index + 1)
        }
    }

    @Throws(
        KeyStoreException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class
    )
    private fun getEncryptCipher(key: ByteArray?): Pair<Cipher, ByteArray> {
        val secretKey = if (key != null) {
            SecretKeySpec(key, AesGcmSpecs.ALGORITHM)
        } else {
            null
        }
        val aad = SecureRandom().generateSeed(AesGcmSpecs.AAD_LENGTH)
        return Cipher.getInstance(AesGcmSpecs.toString()).apply {
            init(Cipher.ENCRYPT_MODE, secretKey ?: getKey())
            updateAAD(aad)
        } to aad
    }

    @Throws(
        KeyStoreException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class
    )
    private fun getDecryptCipherForIv(
        iv: ByteArray,
        key: ByteArray?,
        aad: ByteArray
    ): Cipher {
        val secretKey = if (key != null) {
            SecretKeySpec(key, AesGcmSpecs.ALGORITHM)
        } else {
            null
        }
        return Cipher.getInstance(AesGcmSpecs.toString()).apply {
            init(
                Cipher.DECRYPT_MODE,
                secretKey ?: getKey(),
                GCMParameterSpec(AesGcmSpecs.TAG_LENGTH * 8, iv)
            )
            updateAAD(aad)
        }
    }

    @Throws(
        KeyStoreException::class,
        NoSuchAlgorithmException::class
    )
    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(AesGcmSpecs.ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(AesGcmSpecs.BLOCK_MODE)
                    .setEncryptionPaddings(AesGcmSpecs.PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    @Throws(EnDecryptionException::class)
    fun encrypt(data: ByteArray, file: File): ByteArray {
        return doEncrypt(data, FileOutputStream(file), null)
    }

    @Throws(EnDecryptionException::class)
    fun encrypt(
        data: ByteArray,
        outputStream: OutputStream?,
        hash: Hash
    ): ByteArray {
        return doEncrypt(data, outputStream as FileOutputStream, hash)
    }

    @Throws(EnDecryptionException::class)
    private fun doEncrypt(
        data: ByteArray,
        outputStream: FileOutputStream,
        hash: Hash? = null
    ): ByteArray {
        val encryptCipher: Cipher
        val aad: ByteArray
        try {
            val key = hash?.value?.firstN(32)
            getEncryptCipher(key).apply {
                encryptCipher = first
                aad = second
            }
        } catch (e: KeyStoreException) {
            throw EnDecryptionException(
                "The KeyStore access failed.",
                e.stackTraceToString()
            )
        } catch (e: NoSuchAlgorithmException) {
            throw EnDecryptionException(
                "The requested algorithm $AesGcmSpecs is not supported.",
                e.stackTraceToString()
            )
        } catch (e: NoSuchPaddingException) {
            throw EnDecryptionException(
                "The requested padding ${AesGcmSpecs.PADDING} is not supported.",
                e.stackTraceToString()
            )
        }

        val encryptedBytes = encryptCipher.doFinal(data)
        outputStream.use {
            it.write(encryptCipher.iv)
            it.write(aad)
            if (hash != null) {
                it.write(hash.salt)
            }
            it.write(encryptedBytes)
        }
        return encryptedBytes
    }

    @Throws(EnDecryptionException::class)
    fun decrypt(file: File): ByteArray {
        return doDecrypt(FileInputStream(file), null)
    }

    @Throws(EnDecryptionException::class)
    fun decrypt(inputStream: InputStream?, input: String): ByteArray {
        return doDecrypt(inputStream as FileInputStream, input)
    }

    @Throws(EnDecryptionException::class)
    private fun doDecrypt(inputStream: FileInputStream, input: String? = null): ByteArray {
        return inputStream.use { stream ->
            val iv = ByteArray(12)
            val aad = ByteArray(16)
            val salt = ByteArray(32)

            stream.read(iv)
            stream.read(aad)

            val keys: Queue<ByteArray>?
            if (input != null) {
                keys = LinkedList()
                stream.read(salt)
                keys.add(argon2HashWithKey(input, salt).firstN(32))
                keys.add(shaHashWithKey(input, salt).firstN(32))
            } else {
                keys = null
            }
            val encryptedBytes = stream.readBytes()
            var key: ByteArray? = null
            repeat(keys?.size ?: 1) {
                keys?.let { keys ->
                    Log.d(
                        "CryptoManager",
                        when (it) {
                            0 -> "Decrypting with Argon2"
                            1 -> "Decrypting with SHA"
                            else -> "Available algorithms exhausted"
                        }
                    )
                    key = keys.remove()
                }

                val decryptCipher: Cipher
                try {
                    decryptCipher = getDecryptCipherForIv(iv, key, aad)
                } catch (e: KeyStoreException) {
                    throw EnDecryptionException(
                        "The KeyStore access failed.",
                        e.stackTraceToString()
                    )
                } catch (e: NoSuchAlgorithmException) {
                    throw EnDecryptionException(
                        "The requested algorithm $AesGcmSpecs is not supported.",
                        e.stackTraceToString()
                    )
                } catch (e: NoSuchPaddingException) {
                    throw EnDecryptionException(
                        "The requested padding ${AesGcmSpecs.PADDING} is not supported.",
                        e.stackTraceToString()
                    )
                }
                try {
                    return@use decryptCipher.doFinal(encryptedBytes)
                } catch (e: BadPaddingException) {
                    if (e.stackTraceToString().contains("BAD_DECRYPT")) {
                        e.printStackTrace()
                    } else {
                        throw e
                    }
                }
            }
            return ByteArray(0)
        }
    }

    @Throws(EnDecryptionException::class)
    fun appendStrings(file: File, vararg newStrings: String) {
        @Suppress("UNCHECKED_CAST")
        var data = ObjectSerializer.deserialize(decrypt(file)) as Set<String>
        newStrings.forEach {
            if (!data.contains(it)) {
                data = data.plus(it)
            }
        }
        encrypt(ObjectSerializer.serialize(data), file)
    }

    @Throws(EnDecryptionException::class)
    fun removeString(file: File, string: String) {
        @Suppress("UNCHECKED_CAST")
        var data = ObjectSerializer.deserialize(decrypt(file)) as Set<String>
        data = data.minus(string)
        encrypt(ObjectSerializer.serialize(data), file)
    }

    @Suppress("ArrayInDataClass")
    data class Hash(
        val value: ByteArray,
        val salt: ByteArray
    )

    class EnDecryptionException(
        message: String,
        val customStacktrace: String
    ) : Exception(message)
}
