package composables.radiobuttons

interface RadioOptions<T> {
    val list: List<String>
        get() = set()
    fun set(): List<String>
    fun get(selectedOption: String): T
}