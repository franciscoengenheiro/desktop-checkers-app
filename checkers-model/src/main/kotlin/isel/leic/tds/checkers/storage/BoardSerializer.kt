package isel.leic.tds.checkers.storage

import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.storage.Serializer

// Contants
private const val sep = " "

/**
 * Converts a [Board] instance to [String] and vice-versa.
 */
object BoardSerializer: Serializer<Board, String> {
    override fun write(obj: Board): String {
        // ::class represents an instance of KClass the obj belongs to
        val boardType = obj::class.simpleName
        val turn = when(obj) {
            is BoardDraw -> ""
            is BoardRun -> obj.turn
            is BoardWin -> obj.winner
        }
        val mvsWithoutCapture = if (obj is BoardRun) obj.mvsWithoutCapture else 0
        val numberOfMoves = if (obj is BoardRun) obj.numberOfMoves else 0
        val nl = System.lineSeparator()
        return boardType + nl + turn + nl + numberOfMoves + nl + mvsWithoutCapture + nl +
            obj.moves
            // <Square> <Checker> <Player>
            // Example: Pair<Square(2,a), Piece(Player.w) will be: 2a Piece w
            // Example: Pair<Square(5,e), King(Player.b) will be: 5e King b
            .map {
                it.key.row.number.toString() +
                    it.key.column.toString() + sep +
                        it.value::class.simpleName + sep +
                            it.value.player.name
            }
            .joinToString(System.lineSeparator())
    }
    override fun parse(stream: String): Board {
        val words = stream.split(System.lineSeparator())
        // Retrieve single types
        val boardType = words[0]
        val turn = Player.valueOf(words[1])
        val numberOfMoves = words[2].toInt()
        val mvsWithoutCapture = words[3].toInt()
        // Drop and filter the received stream
        val wordsFiltered = words.drop(4).filter { it.isNotEmpty() }
        // Re-construct board moves
        val moves = wordsFiltered.associate { it.reconstructMove() }
        // Assert the next player turn
        return when (boardType) {
            BoardRun::class.simpleName -> BoardRun(moves, numberOfMoves, mvsWithoutCapture, turn)
            BoardWin::class.simpleName -> BoardWin(moves, turn)
            BoardDraw::class.simpleName -> BoardDraw(moves)
            else -> error("The board type: $boardType does not exist")
        }
    }
}

/**
 * Given a string resulting from a serialized object, reconstruct a [Move].
 */
private fun String.reconstructMove(): Move {
    val words = this.split(sep)
    require(words.size == 3) { "Each line must have exaclty 3 words in string with" +
            " the following format: <Square> <Checker> <Player>" }
    val retrievedSquare = (words[0]).toSquareOrNull()
    requireNotNull(retrievedSquare) { "Retrieved square doens't exist on the board" }
    val retrievedPlayer = Player.valueOf(words[2])
    val checkerType = when(words[1]) {
        Piece::class.simpleName -> Piece(retrievedPlayer)
        King::class.simpleName -> King(retrievedPlayer)
        else -> error("The checker type: ${words[1]} does not exist")
    }
    // Construct the move read from the file
    return Move(retrievedSquare, checkerType)
}
