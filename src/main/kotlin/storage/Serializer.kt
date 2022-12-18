package storage

/**
 * Contract that has functions to convert data structures or object states into stream
 * format for storage purposes which can be reconstructed later.
 * Whoever implements this functions has to make sure the reconstruction is identical to
 * the one previously converted.
 * @param T Type of the entity.
 * @param S Type of the resulting Stream, e.g. ByteArray, String, etc.
 */
interface Serializer<T, S> {
    fun write(obj: T): S
    fun parse(stream: S): T
}
