package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.getPlayerPieces
import isel.leic.tds.checkers.validateSqr
import isel.leic.tds.checkers.plays
import kotlin.test.*

class TestBoard {
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
    @Test fun `More than one capture is avalaible at one time`() {
        var sut: Board = initialBoard()
        sut = sut.plays("3e 4f", "6d 5c", "4f 5g")
        val boardbeforeACapture = sut
        // Two captures are avalaible
        sut = sut.plays("6f 4h")
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 1, sut.moves.size)
        sut = boardbeforeACapture.plays("6h 4f")
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
            "5e 4d",
            "3c 5e", // Capture(4d)
            "5e 7g", // Capture(6f)
            "8h 6f", // Capture(7g)
            "4b 5a",
            "6f 5e",
            "4h 5g",
            "6d 5c",
            "8f 6d", // Capture(7e), with white King
            "6d 3a", // Capture(5c), with white King while landing further
        )
    }
    @Test fun `Win the game by capturing all opponent's checkers`() {
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
            "6b 5c",
            "4b 5a",
            "6f 5g",
            "4h 6f", // Capture(5g)
            "6f 4d", // Capture(5e)
            "4d 6b", // Capture(5c)
            "7a 5c", // Capture(6b)
            "3g 4h",
            "6d 5e",
            "8f 6d", // Capture(7e) using King
            "6d 4f", // Capture(5e) using King
            "5c 4d",
            "3c 5e", // Capture(4d)
            "8d 7e",
            "2h 3g",
            "8b 7a",
            "5e 6d",
            "7c 5e", // Capture(6d)
            "4f 6d", // Capture(5e)
            "6d 8f", // Capture(7e)
            "7a 6b",
            "5a 7c", // Capture(6b)
            "8h 7g",
            "8f 6h", // Capture(7g)
        )
        assertIs<BoardWin>(sut)
        val numberOfCaptures = 14
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - numberOfCaptures, sut.moves.size)
        assertEquals(Player.w, sut.winner)
    }
    @Test fun `Win the game by blocking opponent's remaining checkers`() {
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
            "6b 5c",
            "4b 5a",
            "6f 5g",
            "4h 6f", // Capture(5g)
            "6f 4d", // Capture(5e)
            "4d 6b", // Capture(5c)
            "7a 5c", // Capture(6b)
            "3g 4h",
            "6d 5e",
            "8f 6d", // Capture(7e) using King
            "6d 4f", // Capture(5e) using King
            "5c 4d",
            "3c 5e", // Capture(4d)
            "7c 6b",
            "5a 7c", // Capture(6b)
            "8d 6b", // Capture(7c)
            "5e 6f",
            "8b 7c",
            "4f 8b", // Capture(7c) using King
            "6b 5a",
            "4h 5g",
            "5a 4b",
            "5g 6h",
            "4b 3a",
            "6h 7g",
        )
        assertIs<BoardWin>(sut)
        val numberOfCaptures = 12
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - numberOfCaptures, sut.moves.size)
        // Game was won by the player with the white checkers, but a black checkers
        // still remain on the board
        assertEquals(Player.w, sut.winner)
    }
    @Test fun `Draw a game`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        require( MAX_MOVES_WITHOUT_CAPTURE == 10 )
            { "Max moves without capture should be 10 for this test" }
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
            "6b 5c",
            "4b 5a",
            "6f 5g",
            "4h 6f", // Capture(5g)
            "6f 4d", // Capture(5e)
            "4d 6b", // Capture(5c)
            "7a 5c", // Capture(6b)
            "3g 4h",
            "6d 5e",
            "8f 6d", // Capture(7e) using King
            "6d 3g", // Capture(5e) using King
            "5c 4b",
            "5a 6b",
            "7c 5a", // Capture(6b)
            "3c 4d",
            "4b 3a",
            "2b 3c",
            "5a 4b",
            "3c 5a", // Capture(4b)
            "8h 7g",
            "1c 2b",
            "3a 1c", // Capture(2b)
            "2d 3c",
            "1c 3a",
            "3g 5e",
            "3a 1c",
            "5e 8h", // Capture(7g)
            "1c 3a",
            "8h 5e",
            "3a 1c",
            "5e 8h",
            "1c 3a",
            "8h 5e",
            "3a 1c",
            "5e 8h",
            "1c 3a",
            "8h 5e",
        )
        assertIs<BoardDraw>(sut)
        val numberOfCaptures = 12
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - numberOfCaptures, sut.moves.size)
    }
}
