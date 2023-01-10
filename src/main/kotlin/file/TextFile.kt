package file

import java.io.File
import java.io.PrintWriter

/**
 * Object that implements read and write operations to a text file stored in
 * the project resources.
 */
object TextFile: FileAccess<String, String> {
    /**
     * Function to read from a specified file and return data as a List of Strings,
     * each string is a line separated by the current system line separator.
     * @param fileName unique file name.
     * @throws IllegalArgumentException if the file doesn't exist or is not located
     * in the current project resources.
     */
    override fun read(fileName: String): List<String> {
        val url = this::class.java.getResource("/$fileName.txt")
        requireNotNull(url) { "Could not find resource file: $fileName.txt" }
        val ls = System.lineSeparator()
        return url.readText().split(ls)
    }
    /**
     * Specifies a path independent of the operating system.
     * @param pathSegments a variable amount of strings, each one representing a folder
     * and the last one the file, that make a path to a file.
     */
    fun createPathToFile(vararg pathSegments: String) =
        pathSegments.joinToString(separator = File.separator)
    /**
     * Writes given data as a List of Strings to the specified file.
     * The new file will be located in the current project resources. If the
     * file already exists it will be overriden.
     * @param fileName unique file name.
     * @param data data to store.
     */
    override fun write(fileName: String, data: List<String>) {
        // Creates a writer
        val writer = PrintWriter(
            createPathToFile("src", "main", "resources", "$fileName.txt"))
        for (line in data) {
            // Writes the current data line
            if (line == data.last()) writer.print(line)
            else writer.println(line)
        }
        // Closes writer
        writer.close()
    }
}