package isel.leic.tds.storage

import org.javaync.io.readText
import org.javaync.io.writeText
import kotlin.io.path.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists

/**
 * Implements file storage asynchronous interaction
 * @param [K] Folder where the target file is or will be.
 * @param [T] Object type to serialize to string format.
 */
class FileStorage<K, T>(
    private val folder: String,
    private val serializer: Serializer<T, String>
): Storage<K, T> {
    private fun path(id: K) = "$folder/$id.txt"
    /**
     * Creates a new file with the name of the id with suffix.txt
     * if it does not exist.
     * @param id File unique identifier.
     * @param value data to store.
     * @throws [IllegalArgumentException] If there is already a file with that name.
     */
    override suspend fun create(id: K, value: T) {
        val file = Path(path(id))
        require(!file.exists()) { "There is already a file with that id $id" }
        val stream = serializer.write(value)
        file.writeText(stream)
    }
    /**
     * Retrieves data from a file.
     * @param id File unique identifier.
     * @return The data retrieved from the specified file or null if the file
     * does not exist.
     */
    override suspend fun read(id: K): T? {
        val file = Path(path(id))
        if (!file.exists()) return null
        val stream = file.readText()
        return serializer.parse(stream)
    }
    /**
     * Updates data to a specified file.
     * @param id File unique identifier.
     * @param value Data to store.
     * @throws [IllegalArgumentException] If the file does not exist.
     */
    override suspend fun update(id: K, value: T) {
        val file = Path(path(id))
        require(file.exists()) { "There is no file with that id $id" }
        val stream = serializer.write(value)
        file.writeText(stream)
    }
    /**
     * Deletes data to from specified file.
     * @param id File unique identifier.
     * @throws [IllegalArgumentException] If the file does not exist.
     */
    override suspend fun delete(id: K) {
        val file = Path(path(id))
        require(file.exists()) { "There is no file with that id $id" }
        file.deleteExisting()
    }
}