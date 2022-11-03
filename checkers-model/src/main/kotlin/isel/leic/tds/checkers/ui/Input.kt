package isel.leic.tds.checkers.ui

/**
 * Representes a command retrieved from the stdin which is identified by
 * a [name] and variable number of [params] which can be non-existent.
 */
data class CommandLine(val name: String, val params: List<String>)

/**
 * Reads user input commands in stdin and constructs a [CommandLine]
 * instance if the received input is not empty
 */
fun readCommand(): CommandLine {
    while (true) {
        print("> ")
        // Read user input and split it for every space character
        val words = readln().split(" ").filter { it.isNotEmpty() }
        if (words.isNotEmpty())
            return CommandLine(words.first().uppercase(), words.drop(1))
    }
}