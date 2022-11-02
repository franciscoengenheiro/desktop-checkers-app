package isel.leic.tds.checkers.ui

import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.storage.Serializer

//TODO("change this object to account for moveswithoutcapture)
object BoardSerializer: Serializer<Board, String> {
    override fun write(obj: Board): String {
        // ::class represents an instance of KClass the obj belongs to
        val boardType = obj::class.simpleName
        return boardType + System.lineSeparator() + obj
            .moves
            // <SquareRow><SquareColumn><Checker><Player>
            // Example: Pair<Square(2,a), Piece(Player.w) will be: 2apw
            // Example: Pair<Square(5,e), King(Player.b) will be: 5ekb
            .map {
                it.key.toString() + it.value::class.simpleName + it.value.player.name
            }
            .joinToString(System.lineSeparator())
    }
    override fun parse(stream: String): Board {
        val words = stream.split(System.lineSeparator())
        val boardType = words[0]
        val wordsFiltered = words
            .drop(1) // drop the first element (boardType)
            .filter { it.isNotEmpty() }
        // Last move is always at the end of the file
        val lastMove = wordsFiltered.last().reconstructMove() // Example 34pw
        // Re-construct board pieces
        val moves = wordsFiltered
            .associate { it.reconstructMove() }
        // Assert the next player to play
        val player = if(moves.isEmpty()) Player.w else lastMove.second.player
        return when(boardType) {
            // BoardRun::class.simpleName -> BoardRun(moves, player)
            BoardWin::class.simpleName -> BoardWin(moves, player)
            BoardDraw::class.simpleName -> BoardDraw(moves)
            else -> error("The board type: $boardType does not exist")
        }
    }
}

private fun String.reconstructMove(): Move {
    require(this.length == 4) { "Each line must have exactly 4 chars with " +
            "<SquareRow><SquareColumn><Checker><Player>" }
    val retrievedSquare = (this[0].toString() + this[1].toString()).toSquareOrNull()
    requireNotNull(retrievedSquare) { "The first two characters do not represent a " +
            "valid square" }
    val retrievedPlayer = Player.valueOf(this[4].toString())
    val checkerType = when(this[3].toString()) {
        Piece::class.simpleName -> Piece(retrievedPlayer)
        King::class.simpleName -> King(retrievedPlayer)
        else -> error("The checker type: ${this[3]} does not exist")
    }
    // Construct the move read from the file
    return Move(retrievedSquare, checkerType)
}
