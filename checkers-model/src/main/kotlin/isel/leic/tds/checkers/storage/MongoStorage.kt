package isel.leic.tds.checkers.storage

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.*

/**
 * Implements MongoDb storage interaction.
 * @param [T] Object type to serialize to string format.
 */
class MongoStorage<T>(
    collectionName: String,
    db: MongoDatabase,
    private val serializer: Serializer<T, String>
): Storage<String,T> {
    /**
     * Groups data to a specific id, like a regular Document, for the MongoDb
     */
    private class Doc(val _id: String, val data: String) // _id is a mandatory PK to access MongoDb
    private val collection: MongoCollection<Doc>
        // ::class.java is used to assert the type of this object in the
        // Driver Mongo JVM
        = db.getCollection(collectionName, Doc::class.java)
    /**
     * Creates a document with the specified [id] in the MongoDb or throws
     * [IllegalArgumentException] if the document already exists.
     * @param id Document unique identifier.
     * @param value The data to store.
     */
    override fun create(id: String, value: T) {
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
    override fun read(id: String): T? {
        val doc = collection.findOneById(id) ?: return null
        // 1. read the String content of a document
        // 2. String -> Entity
        val objStr = doc.data
        return serializer.parse(objStr)
    }
    /**
     * Updates data to a specified document in the MongoDb or throws [IllegalArgumentException]
     * if the document couldn't be found.
     * @param id Document unique identifier.
     * @param value Data to store.
     */
    override fun update(id: String, value: T) {
        require(read(id) != null) { "There is no document with given id $id" }
        val objStr = serializer.write(value) // Entity -> String <=> obj -> String
        collection.replaceOneById(id, Doc(id, objStr))
    }
    /**
     * Deletes data from specified document in the MongoDb or throws [IllegalArgumentException]
     * if the document couldn't be found.
     * @param id Document unique identifier.
     */
    override fun delete(id: String) {
        require(read(id) != null) { "There is no document with given id $id" }
        collection.deleteOneById(id)
    }
}