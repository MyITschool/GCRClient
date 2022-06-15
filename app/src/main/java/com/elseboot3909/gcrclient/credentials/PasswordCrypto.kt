package com.elseboot3909.gcrclient.credentials

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.elseboot3909.gcrclient.utils.Constants
import com.google.protobuf.ByteString
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object PasswordCrypto {

    private const val PROVIDER = "AndroidKeyStore"
    private const val ALIAS = "PasswordCrypto"

    private const val IV_LENGTH = 12

    private val charset = charset("UTF-8")

    private val cipherInstance = Cipher.getInstance("AES/GCM/NoPadding")

    private val keyGenerator = KeyGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_AES,
        PROVIDER
    )

    private val keyStore by lazy {
        KeyStore.getInstance(PROVIDER).apply {
            load(null)
        }
    }

    private fun getIV(): ByteArray {
        val iv = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(iv)
        return iv
    }

    private fun getSecretKey(): SecretKey? {
        if (keyStore.containsAlias(ALIAS)) {
            return (keyStore.getEntry(ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            return keyGenerator.apply {
                init(
                    KeyGenParameterSpec
                        .Builder(
                            ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                        )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(false)
                        .build()
                )
            }.generateKey()
        }
    }

    fun encryptString(values: String): ByteString? {
//        cipherInstance.init(
//            Cipher.ENCRYPT_MODE,
//            getSecretKey(),
//            GCMParameterSpec(128, cipherInstance.iv)
//        )
//        return ByteString.copyFrom(cipherInstance.doFinal(values.toByteArray(charset)))
        printUnsecuredMsg()
        return ByteString.copyFrom(values.toByteArray(charset))
    }

    fun decryptString(values: ByteString): String {
//        cipherInstance.init(
//            Cipher.DECRYPT_MODE,
//            getSecretKey(),
//            GCMParameterSpec(128, cipherInstance.iv)
//        )
//        return cipherInstance.doFinal(values.toByteArray()).toString(charset)
        printUnsecuredMsg()
        return values.toByteArray().toString(charset)
    }

    private fun printUnsecuredMsg() {
        Log.e(Constants.LOG_TAG, "NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE")
        Log.e(Constants.LOG_TAG, "NOTICE                             NOTICE")
        Log.e(Constants.LOG_TAG, "NOTICE   ENCRYPTION NOT IN USE!!   NOTICE")
        Log.e(Constants.LOG_TAG, "NOTICE                             NOTICE")
        Log.e(Constants.LOG_TAG, "NOTICE NOTICE NOTICE NOTICE NOTICE NOTICE")
    }

}