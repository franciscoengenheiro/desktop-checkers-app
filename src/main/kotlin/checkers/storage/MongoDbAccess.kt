package checkers.storage

import checkers.model.board.Board
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import storage.MongoStorage

/**
 * Creates a client to access a Mongo database inside a collection
 * by using a connection string stored in an environmental variable within
 * the IDE.
 */
object MongoDbAccess {
    private const val collection = "games"
    const val database = "Checkers"
    const val envVariable = "MONGO_CONNECTION"
    fun createClient(): MongoStorage<Board> {
        // Retrieves environmental variable content (connection string)
        val envConnection = System.getenv(envVariable)
        // Creates a client asynchronously
        val client = KMongo.createClient(envConnection).coroutine
        return MongoStorage(collection, client.getDatabase(database), BoardSerializer)
    }
}