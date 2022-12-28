package checkers.storage

import checkers.model.board.Board
import checkers.model.board.Dimension
import checkers.ui.compose.dialogs.RULES_FILE_NAME
import checkers.ui.compose.dialogs.Rule
import file.TextFile
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import storage.MongoStorage
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Creates a client to access a Mongo database inside a collection
 * by using a connection string stored in an environmental variable within
 * the IDE.
 */
object MongoDbAccess {
    private val collection = "$Dimension"
    const val database = "Checkers"
    private fun decrypt(cipherText: String): String {
        val key = SecretKeySpec("3edd5c8a-85ec-11".toByteArray(), "AES")
        // Initialization vector
        val iv = IvParameterSpec(ByteArray(16))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
        return String(plainText)
    }
    private val encryptedConnectionString
        get() = TextFile.read("EncryptedConnString.txt").first()
    fun createClient(): MongoStorage<Board> {
        // Decrypts connection string
        val connectionString = decrypt(encryptedConnectionString)
        // Creates a client asynchronously
        val client = KMongo.createClient(connectionString).coroutine
        return MongoStorage(collection, client.getDatabase(database), BoardSerializer)
    }
}