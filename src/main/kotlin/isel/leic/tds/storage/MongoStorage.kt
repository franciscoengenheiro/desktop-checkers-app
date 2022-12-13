package isel.leic.tds.storage

import org.litote.kmongo.coroutine.CoroutineDatabase

/**
 * Implements MongoDb asynchronous storage interaction.
 * @param [T] Object type to serialize to string format.
 */
class MongoStorage<T>(
    collectionName: String,
    db: CoroutineDatabase,
    private val serializer: Serializer<T, String>
): Storage<String,T> {
    /**
     * Groups data to a specific id, like a regular Document, for the MongoDb
     */
    private class Doc(val _id: String, val data: String) // _id is a mandatory PK to access MongoDb
    private val collection = db.getCollection<Doc>(collectionName)
    /**
     * Creates a document with the specified [id] in the MongoDb.
     * @param id Document unique identifier.
     * @param value The data to store.
     * @throws [IllegalArgumentException] If the document already exists.
     */
    override suspend fun create(id: String, value: T) {
        // Validate that id is unique and there is no other document with that path.
        require(read(id) == null) { "There is already a document with given id $id" }
         // 1. Convert resulting Object in String
         // 2. Save former String into a Document in MongoDB
        val objStr = serializer.write(value) // Entity -> String <=> value -> String
        collection.insertOne(Doc(id, objStr))
    }
    /**
     * Retrieves data from a specified document in the MongoDb.
     * @param id Document unique identifier.
     * @return The first item retrieved or null.
     */
    override suspend fun read(id: String): T? {
        val doc = collection.findOneById(id) ?: return null
        // 1. read the String content of a document
        // 2. String -> Entity
        val objStr = doc.data
        return serializer.parse(objStr)
    }
    /**
     * Updates data to a specified document in the MongoDb.
     * @param id Document unique identifier.
     * @param value Data to store.
     * @throws [IllegalArgumentException] If the document couldn't be found.
     */
    override suspend fun update(id: String, value: T) {
        require(read(id) != null) { "There is no document with given id $id" }
        val objStr = serializer.write(value) // Entity -> String <=> obj -> String
        collection.replaceOneById(id, Doc(id, objStr))
    }
    /**
     * Deletes data from specified document in the MongoDb.
     * @param id Document unique identifier.
     * @throws [IllegalArgumentException] If the document couldn't be found.
     */
    override suspend fun delete(id: String) {
        require(read(id) != null) { "There is no document with given id $id" }
        collection.deleteOneById(id)
    }
}