package id.dev.spendless.core.data.database

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File
import java.security.SecureRandom

class SpendLessDbPassphrase(
    private val context: Context
) {
    fun getPassphrase(): ByteArray {
        val file = File(context.filesDir, "spendless_passphrase.bin")
        val encryptedFile = EncryptedFile.Builder(
            file,
            context,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return if (file.exists()) {
            encryptedFile.openFileInput().use { it.readBytes() }
        } else {
            generatePassphrase().also { passPhrase ->
                encryptedFile.openFileOutput().use { it.write(passPhrase) }
            }
        }
    }

    private fun generatePassphrase(): ByteArray {
        val random = SecureRandom.getInstanceStrong()
        val result = ByteArray(32)

        random.nextBytes(result)
        while (result.contains(0)) {
            random.nextBytes(result)
        }

        return result
    }
}