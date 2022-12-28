package checkers.storage

import checkers.model.board.Board
import checkers.model.board.Dimension
import encryption.decrypt
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
    val connectionString = decrypt(
        cipherText = TextFile.read("EncryptedConnString").first(),
        key = "3edd5c8a-85ec-11"
    )
    fun createClient(): MongoStorage<Board> {
        // Creates a client asynchronously
        val client = KMongo.createClient(connectionString).coroutine
        return MongoStorage(collection, client.getDatabase(database), BoardSerializer)
    }
}