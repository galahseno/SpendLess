package id.dev.spendless.core.data.pref.serializer

import androidx.datastore.core.Serializer
import id.dev.spendless.core.domain.EncryptionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

class DataStoreSerializer<T>(
    private val encryptionService: EncryptionService,
    private val serializer: KSerializer<T>,
    private val defaultValueProvider: () -> T
) : Serializer<T> {

    override val defaultValue: T
        get() = defaultValueProvider()

    override suspend fun readFrom(input: InputStream): T {
        val encryptedBytes = withContext(Dispatchers.IO) {
            input.use { it.readBytes() }
        }

        val encryptedBytesDecoded = Base64.getDecoder().decode(encryptedBytes)
        val decryptedBytes = encryptionService.decryptDataStore(encryptedBytesDecoded)
        val decodedJsonString = decryptedBytes.decodeToString()

        return Json.decodeFromString(serializer, decodedJsonString)
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        val json = Json.encodeToString(serializer, t)
        val bytes = json.toByteArray()

        val encryptedBytes = encryptionService.encryptDataStore(bytes)
        val encryptedBytesBase64 = Base64.getEncoder().encode(encryptedBytes)

        withContext(Dispatchers.IO) {
            output.use {
                it.write(encryptedBytesBase64)
            }
        }
    }
}