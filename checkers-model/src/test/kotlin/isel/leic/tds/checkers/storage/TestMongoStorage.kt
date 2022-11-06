package isel.leic.tds.checkers.storage

import com.mongodb.ConnectionString
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import isel.leic.tds.checkers.model.initialBoard
import isel.leic.tds.checkers.plays
import isel.leic.tds.checkers.ui.BoardSerializer
import org.litote.kmongo.*
import kotlin.random.Random
import kotlin.test.*

class MongoStorageTest {
    class Student(val _id: Any, val name: String)
    private val connStr = "mongodb+srv://FranciscoLEICTDS32D:agwEI2MWzbYXnCJE@cluster0.b4vcgdl.mongodb.net/?retryWrites=true&w=majority"
    private val db: MongoDatabase = KMongo
        .createClient(ConnectionString(connStr))
        .getDatabase("Checkers")
    private val collection = "test"
    private val gameId = "super"
    @BeforeTest fun setup() {
        db.getCollection(collection).deleteOneById(gameId)
    }
    @Test fun `Check Mongo Db Connection`() {
        val collStudents: MongoCollection<Student>
            = db.getCollection(collection, Student::class.java)
        collStudents.insertOne(Student(Random.nextInt(), "ISEL"))
    }
    @Test fun `Save and load a complex entity Board - Moves - Move`() {
        val fs = MongoStorage(collection, db, BoardSerializer)
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