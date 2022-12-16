package isel.leic.tds.checkers.storage

import isel.leic.tds.checkers.createPersonalizedBoard
import isel.leic.tds.checkers.model.board.*
import isel.leic.tds.checkers.model.moves.move.Player
import isel.leic.tds.checkers.plays
import isel.leic.tds.checkers.storage.BoardSerializer.parse
import isel.leic.tds.checkers.storage.BoardSerializer.write
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame

class TestBoardSerializer {
    @Test fun `Get an equivalent BoardRun from serialize and deserialize`() {
        val board = initialBoard().plays(
            "3c 4d",
            "6d 5c"
        )
        val actual = parse(write(board))
        assertEquals(board.moves, actual.moves)
        assertNotSame(board, actual)
        assertIs<BoardRun>(actual)
    }
    @Test fun `Get an equivalent BoardWin from serialize and deserialize`() {
        var board: Board = createPersonalizedBoard(
            set_turn = Player.w, "7a w", "7e B", "6b b"
        )
        board = board.plays("7a 5c", "7e 4b")
        val actual = parse(write(board))
        assertEquals(board.moves, actual.moves)
        assertNotSame(board, actual)
        assertIs<BoardWin>(actual)
    }
    @Test fun `Get an equivalent BoardDraw from serialize and deserialize`() {
        var board: Board = createPersonalizedBoard(
            set_turn = Player.w, "7a W", "6f B"
        )
        // Move each King to a square and comeback (Idle plays)
        board = board.plays(
            "7a 1g",
            "6f 8h",
            "1g 7a",
            "8h 6f",
            "7a 1g",
            "6f 8h",
            "1g 7a",
            "8h 6f",
            "7a 1g",
            "6f 8h",
            "1g 7a",
            "8h 6f",
            "7a 1g",
            "6f 8h",
            "1g 7a",
            "8h 6f",
            "7a 1g",
            "6f 8h",
            "1g 7a",
            "8h 6f",
        )
        val actual = parse(write(board))
        assertEquals(board.moves, actual.moves)
        assertNotSame(board, actual)
        assertIs<BoardDraw>(actual)
    }
}
