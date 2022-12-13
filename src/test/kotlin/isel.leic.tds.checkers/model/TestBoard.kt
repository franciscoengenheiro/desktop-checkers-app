package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.createPersonalizedBoard
import isel.leic.tds.checkers.getPlayerPieces
import isel.leic.tds.checkers.plays
import isel.leic.tds.checkers.validateSqr
import kotlin.test.*

class TestBoard {
    @Test fun `Board equality`() {
        // Suts - startingBoard
        val startingBoard = initialBoard()
        val boardWin_SB = BoardWin(startingBoard.moves, Player.b)
        val boardDraw_SB = BoardDraw(startingBoard.moves)
        // Suts - afterSomePlaysBoard
        val afterSomePlaysBoard = initialBoard().plays(
            "3c 4d",
            "6d 5c"
        )
        val boardWin_ASPB = BoardWin(afterSomePlaysBoard.moves, Player.w)
        val boardDraw_ASPB = BoardDraw(afterSomePlaysBoard.moves)
        // Two boards are equal if they share the same checker's current position
        // Starting Board
        assertEquals<Board>(startingBoard, boardWin_SB)
        assertEquals(startingBoard.hashCode(), boardWin_SB.hashCode())
        assertEquals<Board>(startingBoard, boardDraw_SB)
        assertEquals(startingBoard.hashCode(), boardDraw_SB.hashCode())
        assertNotEquals(startingBoard, afterSomePlaysBoard)
        assertNotEquals(startingBoard.hashCode(), afterSomePlaysBoard.hashCode())
        // After Some Plays Board
        assertEquals(afterSomePlaysBoard, boardWin_ASPB)
        assertEquals(afterSomePlaysBoard.hashCode(), boardWin_ASPB.hashCode())
        assertEquals(afterSomePlaysBoard, boardDraw_ASPB)
        assertEquals(afterSomePlaysBoard.hashCode(), boardDraw_ASPB.hashCode())
    }
    @Test fun `Sample values for getPlayerPieces`() {
        assertTrue(BOARD_DIM >= 8)
        val dimList = (8..16).step(2).toList()
        assertEquals(listOf(12, 20, 30, 42, 56), dimList.map{ dim -> getPlayerPieces(dim) })
    }
    @Test fun `Initial Board with nxn Squares`() {
        val sut = initialBoard()
        assertEquals(getPlayerPieces(BOARD_DIM)*2, sut.moves.size)
        assertEquals(Player.w, sut.turn)
        // Get white checkers placed in the board
        val whiteCheckers = sut.moves.values.filter { it.player === Player.w }
        assertEquals(getPlayerPieces(BOARD_DIM), whiteCheckers.size)
        // Get black checkers placed in the board
        val blackCheckers = sut.moves.values.filter { it.player === Player.b }
        assertEquals(getPlayerPieces(BOARD_DIM), blackCheckers.size)
        // Assert if the starting checkers are in the right position
        Square.values.forEach{ sqr ->
            if (sqr.black && sqr.row.index !in BOARD_DIM/2 - 1.. BOARD_DIM/2)
                assertNotNull(sut.moves[sqr])
            else
                assertNull(sut.moves[sqr])
        }
        // Evaluate if the top of the board has black checkers and the bottom
        // has white checkers associated with the respective player
        sut.moves.forEach{ sqr ->
            if (sqr.key.row.index in 0 until BOARD_DIM/2)
                assertSame(sqr.value.player, Player.b)
            else
                assertSame(sqr.value.player, Player.w)
        }
    }
    @Test fun `One move only`() {
        var sut: Board = initialBoard()
        val movesBefore = sut.moves
        sut = sut.play(validateSqr("3c"), validateSqr("4d"))
        val movesAfter = sut.moves
        assertNotEquals(movesBefore, movesAfter)
        assertNull(sut[validateSqr("3c")])
        assertNotNull(sut[validateSqr("4d")])
        assertEquals(movesBefore.size, movesAfter.size)
    }
    @Test fun `Each player plays once with some illegal moves`() {
        var sut: Board = initialBoard()
        // Player w turn:
        assertFailsWith<IllegalArgumentException>( "Invalid move") {
            sut = sut.plays("3c 4c")
        }
        assertFailsWith<IllegalArgumentException>( "Square 4c doesn't have a checker") {
            sut = sut.plays("4c 5d")
        }
        assertFailsWith<IllegalArgumentException>("Square 2d is occupied") {
            sut = sut.plays("3e 2d")
        }
        assertFailsWith<IllegalArgumentException>("Square 6f doesn't have your checker") {
            sut = sut.plays("6f 4f")
        }
        // Play a valid move
        sut = sut.plays("3e 4f")

        // Player b turn:
        assertFailsWith<IllegalArgumentException>( "Invalid move") {
            sut = sut.plays("6f 4e")
        }
        assertFailsWith<IllegalArgumentException>( "Square 5e doesn't have a checker") {
            sut = sut.plays("5e 4f")
        }
        assertFailsWith<IllegalArgumentException>("Square 7e is occupied") {
            sut = sut.plays("6f 7e")
        }
        assertFailsWith<IllegalArgumentException>("Square 3e doesn't have your checker") {
            sut = sut.plays("3e 4f")
        }
        // Play a valid move
        sut = sut.plays("6f 5e")
    }
    @Test fun `Make a capture`() {
        var sut: Board = initialBoard()
        // Valid moves
        sut = sut.plays("3c 4d", "6d 5c", "4d 5e")
        assertFailsWith<IllegalArgumentException>("There is a mandatory capture in 6f") {
            sut = sut.plays("6f 5g")
        }
        assertNotNull(sut.moves[validateSqr("5e")])
        // Make a capture
        sut = sut.plays("6f 4d")
        assertNull(sut.moves[validateSqr("5e")])
        // Assert if a white checker was removed from the board:
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 1, sut.moves.size)
        val whiteCheckers = sut.moves.values.filter { it.player === Player.w }
        assertEquals(getPlayerPieces(BOARD_DIM) - 1, whiteCheckers.size)
        val blackCheckers = sut.moves.values.filter { it.player === Player.b }
        assertEquals(getPlayerPieces(BOARD_DIM), blackCheckers.size)
    }
    @Test fun `More than one capture is avalaible at once`() {
        var sut: Board = initialBoard()
        sut = sut.plays("3e 4f", "6d 5c", "4f 5g")
        val boardbeforeACapture = sut
        // Two captures are avalaible
        sut = sut.plays("6f 4h") // First capture
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 1, sut.moves.size)
        sut = boardbeforeACapture.plays("6h 4f") // Second capture
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 1, sut.moves.size)
    }
    @Test fun `Make a black King`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = initialBoard()
        sut = sut.plays(
            "3e 4f",
            "6d 5c",
            "4f 5g",
            "6f 4h", // Capture(5g)
            "3g 4f",
            "7e 6d",
            "2f 3e",
            "4h 3g",
            "1e 2f",
            "3g 1e"  // Capture(2f), also a black piece was made King
        )
        val blackKing = sut.moves[validateSqr("1e")]
        assertIs<King>(blackKing)
        assertTrue{blackKing.player === Player.b}
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 2, sut.moves.size)
        assertIs<BoardRun>(sut)
        assertEquals(sut.turn, Player.w) // Assert lost turn on a crowned King
    }
    @Test fun `Make a white King`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = initialBoard()
        sut = sut.plays(
            "3g 4h",
            "6h 5g",
            "2f 3g",
            "5g 4f",
            "3e 5g", // Capture(4f)
            "6f 5e",
            "5g 6h",
            "7g 6f",
            "3a 4b",
            "8f 7g",
            "6h 8f", // Capture(7g), also a white piece was made King
        )
        val whiteKing = sut.moves[validateSqr("8f")]
        assertIs<King>(whiteKing)
        assertTrue{whiteKing.player === Player.w}
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 2, sut.moves.size)
        assertIs<BoardRun>(sut)
        assertEquals(sut.turn, Player.b) // Assert lost turn on a crowned King
    }
    @Test fun `Move a King in a digonal more than one square at once`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = initialBoard()
        sut = sut.plays(
            "3g 4h",
            "6h 5g",
            "2f 3g",
            "5g 4f",
            "3e 5g", // Capture(4f)
            "6f 5e",
            "5g 6h",
            "7g 6f",
            "3a 4b",
            "8f 7g",
            "6h 8f", // Capture(7g), also a white piece was made King
            "6b 5a",
        )
        // Try to move a King in a diagonal with two contiguous opponent's checkers
        assertFailsWith<IllegalArgumentException> {
            sut = sut.plays("8f 5c")
        }
        assertNull(sut[validateSqr("6h")])
        sut = sut.plays("8f 6h") // Move king backwards in a diagonal
        assertNotNull(sut[validateSqr("6h")])
    }
    @Test fun `Make a capture with a King`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = createPersonalizedBoard(
            set_turn = Player.w, "5e b", "7g W", "1a w"
        )
        val beforeAnyCapture = sut
        // 3 Capture possibilites should be avalaible here:
        assertNull(beforeAnyCapture.moves[validateSqr("4d")])
        sut = beforeAnyCapture.plays("7g 4d") // First Capture
        assertNotNull(sut.moves[validateSqr("4d")])
        assertNull(beforeAnyCapture.moves[validateSqr("3c")])
        sut = beforeAnyCapture.plays("7g 3c") // Second Capture
        assertNotNull(sut.moves[validateSqr("3c")])
        assertNull(beforeAnyCapture.moves[validateSqr("2b")])
        sut = beforeAnyCapture.plays("7g 2b") // Third Capture
        assertNotNull(sut.moves[validateSqr("2b")])
        assertFailsWith<IllegalArgumentException> {
            sut = beforeAnyCapture.plays("7g 1a")
        }
    }
    @Test fun `After a single capture more captures can be made`() {
        var sut: Board = createPersonalizedBoard(
            set_turn = Player.w, "3g b", "2f w", "8f W", "5c b", "2b b"
        )
        sut = sut.plays("8f 3a") // First capture: Capture(5c)
        assertFailsWith<IllegalArgumentException> {
            sut = sut.plays("2f 4h") // Try a capture with another turn checker
        }
        sut = sut.plays("3a 1c") // Second Capture: Capture(2b)
    }
    @Test fun `Win the game by capturing all opponent's checkers`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = createPersonalizedBoard(
            set_turn = Player.w, "7a w", "7e B", "6b b"
        )
        sut = sut.plays("7a 5c", "7e 4b")
        assertIs<BoardWin>(sut)
        assertEquals(Player.b, sut.winner)
    }
    @Test fun `Win the game by blocking opponent's remaining checkers`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = createPersonalizedBoard(
            set_turn = Player.w, "7a b", "8h b", "6f W", "6h w", "6b w"
        )
        sut = sut.plays("6h 7g")
        assertIs<BoardWin>(sut)
        assertEquals(Player.w, sut.winner)
    }
    @Test fun `Draw a game`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = createPersonalizedBoard(
            set_turn = Player.w, "7a W", "6f B"
        )
        // Move each King to a square and comeback (Idle plays)
        sut = sut.plays(
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
        assertIs<BoardDraw>(sut)
    }
}
