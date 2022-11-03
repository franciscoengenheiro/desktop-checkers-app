package isel.leic.tds.checkers.storage

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.*

class Doc(val _id: String, val data: String)

class MongoStorage<T>(
    collectionName: String,
    db: MongoDatabase,
    private val serializer: Serializer<T, String>
): Storage<String,T> {
    private val collection: MongoCollection<Doc>
        // ::class.java is used to assert the type of this object in the
        // Driver Mongo JVM
        = db.getCollection(collectionName, Doc::class.java)
    /**
     * Requires a unique id.
     */
    override fun create(id: String, value: T) {
        /**
         * Validate that id is unique and there is no other file with that path.
         */
        require(read(id) == null) { "There is already a document with given id $id" }
        /**
         * 1. Convert resulting Object in String
         * 2. Save former String into a Document in MongoDB
         */
        val objStr = serializer.write(value) // Entity -> String <=> value -> String
        collection.insertOne(Doc(id, objStr))
    }

    override fun read(id: String): T? {
        val doc = collection.findOneById(id) ?: return null
        /**
         * 1. read the String content of a document
         * 2. String -> Entity
         */
        val objStr = doc.data
        return serializer.parse(objStr)
    }

    override fun update(id: String, value: T) {
        require(read(id) != null) { "There is no document with given id $id" }
        val objStr = serializer.write(value) // Entity -> String <=> obj -> String
        collection.replaceOneById(id, Doc(id, objStr))
    }

    override fun delete(id: String) {
        require(read(id) != null) { "There is no document with given id $id" }
        collection.deleteOneById(id)
    }
}