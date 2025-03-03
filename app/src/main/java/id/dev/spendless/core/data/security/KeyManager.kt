package id.dev.spendless.core.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object KeyManager {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    fun getOrCreateSecretKey(passphrase: ByteArray): SecretKey {
        val keyAlias = deriveKeyAlias(passphrase)
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
        val entry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry

        return entry?.secretKey ?: generateSecretKey(keyAlias)
    }

    private fun generateSecretKey(keyAlias: String): SecretKey {
        val keyGenerator =
            KeyGenerator.getInstance(ALGORITHM, ANDROID_KEYSTORE)

        val keyGenSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(BLOCK_MODE)
            .setRandomizedEncryptionRequired(true)
            .setEncryptionPaddings(PADDING)
            .setKeySize(256)
            .build()

        keyGenerator.init(keyGenSpec)

        return keyGenerator.generateKey()
    }

    private fun deriveKeyAlias(passphrase: ByteArray): String {
        val digest = MessageDigest.getInstance(KeyProperties.DIGEST_SHA256)
        val hash = digest.digest(passphrase)

        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }
}