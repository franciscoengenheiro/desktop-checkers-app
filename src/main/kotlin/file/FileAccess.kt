package file

/**
 * Contract that defines synchronous access to a text file.
 * @param T The type of the data to store to or read from a file.
 */
interface FileAccess<String, T> {
    fun read(fileName: String): Array<T>
    fun write(fileName: String, data: Array<T>)
}