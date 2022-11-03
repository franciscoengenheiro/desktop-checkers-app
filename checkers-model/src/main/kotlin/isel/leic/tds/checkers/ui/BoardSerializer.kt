package isel.leic.tds.checkers.ui

import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.storage.Serializer

// Contants
private const val sep = " "

object BoardSerializer: Serializer<Board, String> {
    override fun write(obj: Board): String {
        // ::class represents an instance of KClass the obj belongs to
        val boardType = obj::class.simpleName
        val mvsWithoutCapt = if (obj is BoardRun) obj.movesWithoutCapture else 0
        return boardType + System.lineSeparator() + mvsWithoutCapt.toString() + System.lineSeparator() +
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
        val boardType = words[0]
        val movesWithoutCapture = words[1].toInt()
        val wordsFiltered = words
            .drop(2) // drop the first element (boardType) and the amount of valid moves without capture
            .filter { it.isNotEmpty() }
        // Last move is always at the end of the file
        val lastMove = wordsFiltered.last().reconstructMove()
        // Re-construct board pieces
        val moves = wordsFiltered
            .associate { it.reconstructMove() }
        // Assert the next player to play
        // TODO("only relying on the last move to assert the player turn is not an option")
        // TODO("the last player could've more captures to do, how to do this?")
        val player = if(moves == initialBoard().moves) Player.w else lastMove.second.player
        return when (boardType) {
            BoardRun::class.simpleName -> BoardRun(moves, movesWithoutCapture, player)
            BoardWin::class.simpleName -> BoardWin(moves, player)
            BoardDraw::class.simpleName -> BoardDraw(moves)
            else -> error("The board type: $boardType does not exist")
        }
    }
}

private fun String.reconstructMove(): Move {
    val words = this.split(sep)
    require(words.size == 3) { "Each line must have exaclty 3 words in string with" +
            "the following format: <Square> <Checker> <Player>" }
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
