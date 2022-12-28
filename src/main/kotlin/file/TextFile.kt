package file

import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter

object TextFile: FileAccess<String, String> {
    // Function to read from a specified file and return data as an Array of Strings
    // Returns the array pointer
    override fun read(fileName: String): Array<String> {
        // Creates a reader
        val reader = BufferedReader(FileReader(fileName))
        // Creates an array to store all data lines
        var dataList: Array<String> = emptyArray()
        // Creates a mutable variable to store the current line that bufferedReader is reading
        var currentLine = reader.readLine()
        while (currentLine != null) {
            // Adds data line to the array
            dataList += currentLine
            // Moves to next data line
            currentLine = reader.readLine()
        }
        // Closes reader
        reader.close()
        return dataList
    }

    /**
     * Writes given data as a List of Strings to the specified file
     * @fi
     */
    override fun write(fileName: String, data: Array<String>) {
        // Creates a writer
        val writer = PrintWriter(fileName)
        for (line in data) {
            // Writes the current data line
            writer.println(line)
        }
        // Closes writer
        writer.close()
    }
}