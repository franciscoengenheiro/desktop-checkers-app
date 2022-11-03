import com.mongodb.ConnectionString
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import isel.leic.tds.checkers.model.BoardRun
import org.litote.kmongo.KMongo
import org.litote.kmongo.deleteOneById
import isel.leic.tds.checkers.model.initialBoard
import isel.leic.tds.checkers.plays
import isel.leic.tds.checkers.storage.MongoStorage
import isel.leic.tds.checkers.ui.BoardSerializer
import kotlin.random.Random
import kotlin.test.*

class MongoStorageTest {
    class Student(val _id: Any, val name: String)
    private val connStr = "mongodb+srv://FranciscoLEICTDS32D:agwEI2MWzbYXnCJE@cluster0.b4vcgdl.mongodb.net/?retryWrites=true&w=majority"
    private val db: MongoDatabase = KMongo
        .createClient(ConnectionString(connStr))
        .getDatabase("Checkers")
    private val coll = "games"
    private val gameId = "super"
    @BeforeTest fun setup() {
        db.getCollection(coll).deleteOneById(gameId)
    }
    @Test fun `Check Mongo Db Connection`() {
        val collStudents: MongoCollection<Student>
            = db.getCollection("Test", Student::class.java)
        collStudents.insertOne(Student(Random.nextInt(), "ISEL"))
    }
    @Test fun `Save and load a complex entity Board - Moves - Move`() {
        val fs = MongoStorage(coll, db, BoardSerializer)
        val board = initialBoard().also { fs.create(gameId, it) }
            .plays("3e 4f w", "6b 5a b", "4f 5g w")
        fs.update(gameId, board)
        val iter = fs.read(gameId)?.moves?.entries?.iterator()
        board.moves.forEach { (sqr, checker) ->
            val actual = iter?.next()
            assertEquals(sqr, actual?.key)
            assertEquals(checker, actual?.value)
        }
    }
}