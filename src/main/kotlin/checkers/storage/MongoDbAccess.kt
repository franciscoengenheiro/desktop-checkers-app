package checkers.storage

import checkers.model.board.Board
import checkers.model.board.Dimension
import encryption.decrypt
import file.TextFile
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import storage.MongoStorage

/**
 * Creates a client asynchronously to access a Mongo database inside a collection.
 * The connection to the database is established by decrypting a connection
 * string stored in a resource file.
 */
object MongoDbAccess {
    private val collection = "$Dimension"
    const val database = "Checkers"
    val connectionString = decrypt(
        cipherText = TextFile.read("EncryptedConnString").first(),
        key = "3edd5c8a-85ec-11"
    )
    fun createClient(): MongoStorage<Board> {
        val client = KMongo.createClient(connectionString).coroutine
        return MongoStorage(collection, client.getDatabase(database), BoardSerializer)
    }
}