package file

import kotlin.io.path.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestTextFile {
    private val folder = "out"
    private val testFileName = "testFile"
    private val path = "$folder/$testFileName.txt"
    private val dataSize = 10
    private val data = Array(dataSize) { idx -> "line: $idx" }
    @Test fun `read and write from a text file`() {
        TextFile.write(path, data)
        val actual = TextFile.read(path)
        assertTrue { actual.contentEquals(data) }
    }
    @AfterTest fun `delete created file`() {
        val file = Path(path)
        if (file.exists()) file.deleteExisting()
    }
}

