package isel.leic.tds.checkers.ui

// class to distinguish each avalaible command
data class CommandLine(val name: String, val params:List<String>)

fun readCommand(): CommandLine {
    while (true) {
        print("> ")
        // Read user input and split it for every space character
        val words = readln().split(" ").filter { it.isNotEmpty() }
        if (words.isNotEmpty())
            return CommandLine(words.first().uppercase(), words.drop(1))
    }
}