package isel.leic.tds.checkers.storage

import java.io.File

/**
 * Implements file storage interaction.
 * @param [K] Folder where the target file is or will be.
 * @param [T] Object type to serialize to string format.
 */
class FileStorage<K, T>(val folder: String, val serializer: Serializer<T, String>): Storage<K, T> {
    private fun path(id: K) = "$folder/$id.txt"
    /**
     * Creates a new file with the name of the id with suffix.txt
     * if it does not exist or throws [IllegalArgumentException]
     * if there is already a file with that name.
     * @param id File unique identifier.
     * @param value data to store.
     */
    override fun create(id: K, value: T) {
        val file = File(path(id))
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
    override fun read(id: K): T? {
        val file = File(path(id))
        if (!file.exists()) return null
        val stream = file.readText()
        return serializer.parse(stream)
    }
    /**
     * Updates data to a specified file or throws [IllegalArgumentException]
     * if the file does not exist.
     * @param id File unique identifier.
     * @param value data to store.
     */
    override fun update(id: K, value: T) {
        val file = File(path(id))
        require(file.exists()) { "There is no file with that id $id" }
        val stream = serializer.write(value)
        file.writeText(stream)
    }
    /**
     * Deletes data to from specified file or throws [IllegalArgumentException]
     * if the file does not exist.
     * @param id File unique identifier.
     */
    override fun delete(id: K) {
        val file = File(path(id))
        require(file.exists()) { "There is no file with that id $id" }
        file.delete()
    }
}