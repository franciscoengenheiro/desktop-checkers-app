package composables.radiobuttons

/**
 * Contract that defines a set of radio options.
 * @param T Type of the entity.
 */
interface RadioOptions<T> {
    val list: List<String>
        get() = set()
    fun set(): List<String>
    fun get(selectedOption: String): T
}