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
    private val cipher = Cipher.getInstance(transformation)
    private val tLen = 128

    override fun encrypt(data: String): Pair<String, String> {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv

        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return Pair(encryptedBytes.toBase64(), iv.toBase64())
    }

    override fun decrypt(encryptedData: String, iv: String): String {
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(tLen, iv.fromBase64()))
        val decryptedBytes = cipher.doFinal(encryptedData.fromBase64())

        return String(decryptedBytes)
    }

    override fun encryptDataStore(bytes: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv

        val encrypted = cipher.doFinal(bytes)
        return iv + encrypted
    }

    /**
    * blockSize - 4 cause no padding in the cipher for GCM block mode
    **/
    override fun decryptDataStore(bytes: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(transformation)
        val iv = bytes.copyOfRange(0, (cipher.blockSize - 4))
        val data = bytes.copyOfRange((cipher.blockSize - 4), bytes.size)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(tLen, iv))
        return cipher.doFinal(data)
    }

    private fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.DEFAULT)
    private fun String.fromBase64(): ByteArray = Base64.decode(this, Base64.DEFAULT)
}