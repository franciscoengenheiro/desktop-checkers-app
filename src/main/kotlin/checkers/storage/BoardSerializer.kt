package checkers.storage

import checkers.model.board.Board
import checkers.model.board.BoardDraw
import checkers.model.board.BoardRun
import checkers.model.board.BoardWin
import checkers.model.moves.Move
import checkers.model.moves.move.King
import checkers.model.moves.move.Piece
import checkers.model.moves.move.Player
import checkers.model.moves.move.toSquareOrNull
import storage.Serializer
import storage.Storage

// Constants
private const val sep = " "

/**
 * Represents a storage used to store [Board] types as data, using [String]
 * as the identifier.
 */
typealias BoardStorage = Storage<String, Board>

/**
 * Converts a [Board] instance to [String] and vice-versa.
 */
object BoardSerializer: Serializer<Board, String> {
    private const val noTurn = ""
    override fun write(obj: Board): String {
        // ::class represents an instance of KClass the obj belongs to
        val boardType = obj::class.simpleName
        val turn = when(obj) {
            is BoardDraw -> noTurn
            is BoardRun -> obj.turn
            is BoardWin -> obj.winner
        }
        val numberOfMoves = if (obj is BoardRun) obj.numberOfMoves else 0
        val mvsWithoutCapture = if (obj is BoardRun) obj.mvsWithoutCapture else 0
        val prevCaptureSqr = if (obj is BoardRun) obj.prevCaptureSqr else null
        val nl = System.lineSeparator()
        return boardType + nl + turn + nl + numberOfMoves + nl + mvsWithoutCapture + nl +
                prevCaptureSqr + nl +
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
        val turn = if (words[1] == noTurn) null else Player.valueOf(words[1])
        val numberOfMoves = words[2].toInt()
        val mvsWithoutCapture = words[3].toInt()
        val prevCaptureSqr = words[4].toSquareOrNull()
        // Drop and filter the received stream
        val wordsFiltered = words.drop(5).filter { it.isNotEmpty() }
        // Reconstruct board moves
        val moves = wordsFiltered.associate { it.reconstructMove() }
        // Assert the next player turn
        return when (boardType) {
            BoardRun::class.simpleName -> {
                requireNotNull(turn) { "There can't be a BoardRun without a valid turn" }
                BoardRun(moves, numberOfMoves, mvsWithoutCapture, prevCaptureSqr, turn)
            }
            BoardWin::class.simpleName -> {
                requireNotNull(turn) { "There can't be a BoardWin without a winner" }
                BoardWin(moves, turn)
            }
            BoardDraw::class.simpleName -> BoardDraw(moves)
            else -> error("The board type: $boardType does not exist")
        }
    }
}

/**
 * Given a string resulting from a serialized object, reconstruct a [Move].
 * @throws [IllegalArgumentException] If the expected format: <Square> <Checker>
 * <Player> is incorrect, if the retrieved square does not exist on the board
 * or even the checker type retrieved does not exist.
 */
private fun String.reconstructMove(): Move {
    val words = this.split(sep)
    require(words.size == 3) { "Each line must have exactly 3 words in a string with" +
            " the following format: <Square> <Checker> <Player>" }
    val retrievedSquare = (words[0]).toSquareOrNull()
    requireNotNull(retrievedSquare) { "Retrieved square doesn't exist on the board" }
    val retrievedPlayer = Player.valueOf(words[2])
    val checkerType = when(words[1]) {
        Piece::class.simpleName -> Piece(retrievedPlayer)
        King::class.simpleName -> King(retrievedPlayer)
        else -> throw IllegalArgumentException("The checker type: ${words[1]} does not exist")
    }
    // Construct the move read from the file
    return Move(retrievedSquare, checkerType)
}