package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.storage.BoardStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
 * @param id Game unique identifier.
 * @param storage Storage where the game data will be.
 * @param scope A Coroutine Scope context.
 */
suspend fun createGame(
    id: String,
    storage: BoardStorage,
    scope: CoroutineScope
): Game {
    // Check if a board is in the specified storage
    val existingBoard = scope.async {
        storage.read(id)
    }
    // Await for the value of the retrieved board, without blocking the current thread
    val board = existingBoard.await()
    // Create a job to represent the coroutine action: delete the game from storage
    var deleted: Job? = null
    // If the storage has a board:
    if (board != null) {
        // Check if the already created board has a maximum of one move done
        // by the first player to enter
        if (board is BoardRun && board.numberOfMoves in 0..1) {
            // If it does, the game will assign the second player
            return Game(id, Player.b, board)
        } else {
            // Save in this job the state of this coroutine action
            deleted = scope.launch {
                // Delete the previous board from the storage
                storage.delete(id)
            }
        }
    }
    // Create a new game
    return Game(id, Player.w, initialBoard()).also {
        // Wait for the job (Delete a game from storage), mentioned above, to be
        // completed if it exists, since before creating a new game,the other game,
        // with the same id, must be deleted first
        deleted?.join()
        scope.launch {
            // Create a storage to store the new board
            storage.create(id, initialBoard())
        }
    }
}

/**
 * Resumes a game identified by an [id] with a given [localPlayer].
 * @param id Game unique identifier.
 * @param localPlayer PLayer to join the game.
 * @param storage Storage where the game data is.
 * @param scope A Coroutine Scope context.
 * @throws [IllegalArgumentException] If the game does not exist.
 */
suspend fun resumeGame(
    id: String,
    localPlayer: Player,
    storage: BoardStorage,
    scope: CoroutineScope
): Game {
    val existingBoard = scope.async {
        // Check if a board is in the specified storage
        storage.read(id)
    }
    // Await for the value of the retrieved board, without blocking the current thread
    val newBoard = existingBoard.await()
    requireNotNull(newBoard) { "Game with id $id does not exist" }
    // If the storage has a previously created board, the game will resume with
    // the specified player
    return Game(id, localPlayer, newBoard)
}

/**
 * Ensures the correct requirements are in order before calling [Board.play].
 * @param fromSqr Square to move a checker from.
 * @param toSqr Square to move a checker to.
 * @param storage Storage where the game data is.
 * @param scope A Coroutine Scope context.
 * @return A new board on the respective storage and a copy of the game with the
 * updated board.
 * @throws [IllegalStateException] If the board is not of [BoardRun] type or
 * if the current board turn does not belong to the player trying to make a move.
 */
fun Game.play(
    fromSqr: Square,
    toSqr: Square,
    storage: BoardStorage,
    scope: CoroutineScope
): Game {
    check(board is BoardRun) { "Game is over" }
    check(localPlayer == board.turn) { "Not your turn" }
    // Create a new board with the recent play
    val newBoard = board.play(fromSqr, toSqr)
    // Launch a coroutine, to update the game asynchronously, since there's no need
    // to wait for it to finish to resume the game
    scope.launch {
        println("updating game")
        // Update the board in the storage
        storage.update(this@play.id, newBoard)
    }
    // Replace the old board with the new board
    return copy(board = newBoard)
}