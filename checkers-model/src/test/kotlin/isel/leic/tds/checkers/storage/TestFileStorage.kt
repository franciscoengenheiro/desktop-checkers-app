package isel.leic.tds.checkers.storage

import kotlin.test.*
import java.io.File

data class Student(val nr: Int, val name: String)

// Object example
object StudentSerializer: Serializer<Student, String> {
    override fun write(obj: Student) = "${obj.nr} ${obj.name}"
    override fun parse(stream: String): Student {
        val words = stream.split(" ")
        require(words.size == 2) { "Source stream in illegal format, it should contain <nr> <name>"}
        val nr = words.first().toIntOrNull()
        requireNotNull(nr) { "Number received is not a valid number" }
        return Student(nr, words[1])
    }
}

class TestFileStorage {
    // Constants
    private val studentNr = 934973
    private val folder = "out"
    // Setup function to run always before every test and to delete created files
    // from previous tests
    @BeforeTest fun setup() {
        val f = File("$folder/$studentNr.txt")
        if (f.exists()) f.delete()
    }
    @Test fun `Check if we cannot create the same file twice`() {
        val fs = FileStorage<Int, Student>(folder, StudentSerializer)
        fs.create(studentNr, Student(studentNr, "ISEL"))
        assertFailsWith<IllegalArgumentException> {
            fs.create(studentNr, Student(studentNr, "TDS"))
        }
    }
    @Test fun `Serialize add deserialize `() {
        val fs = FileStorage<Int, Student>(folder, StudentSerializer)
        val student = Student(studentNr, "ISEL")
        fs.create(studentNr, student)
        val actual = fs.read(studentNr)
        assertEquals(student, actual)
        assertNotSame(student, actual)
    }
    // TODO(complete delete and update tests")
}