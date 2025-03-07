package id.dev.spendless.core.domain

interface EncryptionService {
    fun encrypt(data: String): Pair<String, String>
    fun decrypt(encryptedData: String, iv: String): String
    fun encryptDataStore(bytes: ByteArray): ByteArray
    fun decryptDataStore(bytes: ByteArray): ByteArray
}