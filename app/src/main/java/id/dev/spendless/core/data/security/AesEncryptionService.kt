package id.dev.spendless.core.data.security

import android.util.Base64
import id.dev.spendless.core.data.security.KeyManager.TRANSFORMATION
import id.dev.spendless.core.domain.EncryptionService
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AesEncryptionService(
    private val secretKey: SecretKey
) : EncryptionService {

    private val transformation = TRANSFORMATION

    override fun encrypt(data: String): Pair<String, String> {
        val cipher = Cipher.getInstance(transformation)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv

        val encryptedBytes = cipher.doFinal(data.toByteArray())

        return Pair(encryptedBytes.toBase64(), iv.toBase64())
    }

    override fun decrypt(encryptedData: String, iv: String): String {
        val cipher = Cipher.getInstance(transformation)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv.fromBase64()))
        val decryptedBytes = cipher.doFinal(encryptedData.fromBase64())

        return String(decryptedBytes)
    }

    private fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.DEFAULT)
    private fun String.fromBase64(): ByteArray = Base64.decode(this, Base64.DEFAULT)
}