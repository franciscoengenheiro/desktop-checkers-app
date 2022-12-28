package file

import kotlin.test.Test
import kotlin.test.assertEquals

class TestTextFile {
    private val testFileName = "testFile"
    private val path =
        TextFile.createPathToFile("src", "main", "resources", "$testFileName.txt")
    private val dataSize = 10
    private val data = List(dataSize) { idx -> "line: $idx" }
    @Test fun `write to a text file`() {
        TextFile.write(testFileName, data)
    }
    @Test fun `read from a text file`() {
        TextFile.write(testFileName, data)
        val actual = TextFile.read(testFileName)
        assertEquals(data, actual)
    }
}