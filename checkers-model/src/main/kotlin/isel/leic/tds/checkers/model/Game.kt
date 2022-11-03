package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.storage.Storage

data class Game(val id: String, val player: Player, val board: Board)

// TODO("if the first player starts the game and leaves, and start again and the other player also leaves, every time he returns will be player.w?")
fun createGame(id: String, storage: Storage<String, Board>): Game {
    // Check if a board is in the file
    val existingBoard = storage.read(id)
    // If tThe file has a board:
    if (existingBoard != null) {
        // Check if the already created board has at least one white checker moved:
        if (existingBoard.moves != initialBoard().moves) {
            // If it does, the game will resume
            return Game(id, Player.b, existingBoard)
        }
        // Delete the file with the previous game
        storage.delete(id)
    }
    // Create a new game
    return Game(id, Player.w, initialBoard()).also {
        // Create a new file with the new board
        storage.create(id, initialBoard())
    }
}

fun Game.play(fromSqr: Square, toSqr: Square, storage: Storage<String, Board>): Game {
    check(board is BoardRun) { "Game is over" }
    check(player == board.turn) { "Not your turn" }
    // Create a new board
    val newBoard = board.play(fromSqr, toSqr)
    // Update the board in the storage
    storage.update(this.id, newBoard)
    // Replace the old board with the new board
    return copy(board = newBoard)
}