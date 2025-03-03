package id.dev.spendless.core.domain

interface EncryptionService {
    fun encrypt(data: String): Pair<String, String>
    fun decrypt(encryptedData: String, iv: String): String
}