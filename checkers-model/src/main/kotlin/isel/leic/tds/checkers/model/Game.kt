package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.storage.Storage

/**
 * Represents a game instance.
 * @param id Game unique identifier.
 * @param localPlayer Player assigned to this game.
 * @param board Current state of the board.
 */
data class Game(val id: String, val localPlayer: Player, val board: Board)

/**
 * Assigns a player to the white checkers if they are the first to enter the game,
 * or the black checkers if they are the second, and begins the game with a given [id].
 * If the storage didn't include a board or if more than one white checker has been changed
 * from its initial starting position in an existing game, a new game is constructed
 * from scratch.
 */
fun createGame(id: String, storage: Storage<String, Board>): Game {
    // Check if a board is in the specified storage
    val existingBoard = storage.read(id)
    // If the storage has a board:
    if (existingBoard != null) {
        // Check if the already created board has a maximum of one move done by the
        // first player to enter
        if (existingBoard is BoardRun && existingBoard.numberOfMoves in 0..1) {
            // If it does, the game will assign the second player
            return Game(id, Player.b, existingBoard)
        } else {
            // Delete the storage with the previous board
            storage.delete(id)
        }
    }
    // Create a new game
    return Game(id, Player.w, initialBoard()).also {
        // Also create a storage to store the new board
        storage.create(id, initialBoard())
    }
}

/**
 * Resumes a game identified by an [id] with a given player.
 * If the game does not exit throws [IllegalArgumentException].
 */
fun resumeGame(id: String, localPlayer: Player, storage: Storage<String, Board>): Game {
    // Check if a board is in the specified storage
    val existingBoard = storage.read(id)
    // If the storage has a previously created board:
    requireNotNull(existingBoard) { "Game with id $id does not exist"}
    // The game will resume with the specified player
    return Game(id, localPlayer, existingBoard)
}

/**
 * Ensures the correct requirements are in order before calling [Board.play].
 * @return A new board on the respective storage and a copy of the game with the
 * updated board.
 */
fun Game.play(fromSqr: Square, toSqr: Square, storage: Storage<String, Board>): Game {
    check(board is BoardRun) { "Game is over" }
    check(localPlayer == board.turn) { "Not your turn" }
    // Create a new board
    val newBoard = board.play(fromSqr, toSqr)
    // Update the board in the storage
    storage.update(this.id, newBoard)
    // Replace the old board with the new board
    return copy(board = newBoard)
}