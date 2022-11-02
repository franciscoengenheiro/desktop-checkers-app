package isel.leic.tds.checkers.storage

/**
 * Contract that has all of the CRUD basic operations for data storage
 * @param K The type of the unique identifier (key)
 * @param T The type of the data to store
 */
interface Storage<K, T> {
    fun create(id: K, value: T)
    fun read(id: K): T?
    fun update(id: K, value: T)
    fun delete(id: K)
}