package storage

import checkers.model.board.initialBoard
import checkers.plays
import checkers.storage.BoardSerializer
import checkers.storage.MongoDbAccess.database
import checkers.storage.MongoDbAccess.envVariable
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MongoStorageTest {
    class Student(val _id: Any, val name: String)
    private val envConnection = System.getenv(envVariable)
    private val db = KMongo.createClient(envConnection).coroutine.getDatabase(database)
    private val collection = "test"
    private val gameId = "super"
    @BeforeTest fun setup() {
        runBlocking {
            db.getCollection<Student>(collection)
                .deleteOneById(gameId)
        }
    }
    @Test fun `Check Mongo Db Connection`() {
        runBlocking {
            db.getCollection<Student>(collection)
                .insertOne(Student(Random.nextInt(), "ISEL"))
        }
    }
    @Test fun `Save and load a complex entity Board - Moves - Move`() {
        val fs = MongoStorage(collection, db, BoardSerializer)
        runBlocking {
            val board = initialBoard().also { fs.create(gameId, it) }
                .plays("3e 4f", "6b 5a", "4f 5g")
            fs.update(gameId, board)
            val iter = fs.read(gameId)?.moves?.entries?.iterator()
            board.moves.forEach { (sqr, checker) ->
                val actual = iter?.next()
                assertEquals(sqr, actual?.key)
                assertEquals(checker, actual?.value)
            }
        }
    }
}