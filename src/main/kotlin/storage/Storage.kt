package storage

/**
 * Contract that has all the CRUD basic operations for data storage asynchronous access.
 * @param K The type of the unique identifier (key).
 * @param T The type of the data to store.
 */
interface Storage<K, T> {
    suspend fun create(id: K, value: T)
    suspend fun read(id: K): T?
    suspend fun update(id: K, value: T)
    suspend fun delete(id: K)
}