package isel.leic.tds.checkers.model
import kotlin.test.*

class TestBoard {
    // Function to retrieve the amount of pieces a player can have
    // at the start of the game given the board dimension (BOARD_DIM)
    private fun getPlayerPieces(dim: Int): Int = dim/2 * (dim/2 - 1)
    private fun validateSqr(s: String): Square {
        require (BOARD_DIM < 10) { "Board dim is greater or equal to 10" }
        // Last validation is required in order to ensure given string has length 2.
        // For a BOARD_DIM = 10, one move could be "10e" which will be bigger than
        // the expected length
        require(s.length == 2) { "Invalid string format" }
        val sqr = s.toSquareOrNull()
        requireNotNull(sqr) { "Square $sqr does not exist on the board" }
        return sqr
    }
    // This makes several plays but assumes their all valid plays
    private fun Board.plays(vararg s: String): Board {
        var b = this
        s.forEach {
            require(it.length == 7) { "Invalid string format" }
            val list = it.split(" ")
            require(list.size == 3) { "Incorrect expected arguments" }
            val toSqr = validateSqr(list[0])
            val fromSqr = validateSqr(list[1])
            val player = Player.valueOf(list[2])
            b = b.play(toSqr, fromSqr, player)
        }
        return b
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
        // Assert if the initial moves weren't made to the middle rows
        // and if it were made to a black square and to its respective rows
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
        sut = sut.play(validateSqr("3c"), validateSqr("4d"), Player.w)
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
            sut = sut.plays("3e 5c b")
        }
        assertFailsWith<IllegalArgumentException>("Position 2d is occupied") {
            sut = sut.plays("3e 2d w")
        }
        assertFailsWith<IllegalArgumentException>("square 6f doesn't have your checker") {
            sut = sut.plays("6f 4f w")
        }
        // Play a valid move
        sut = sut.plays("3e 4f w")
        // Player b turn:
        assertFailsWith<IllegalArgumentException>( "Invalid move") {
            sut = sut.plays("6f 4d b")
        }
        assertFailsWith<IllegalArgumentException>("Position 7e is occupied") {
            sut = sut.plays("6f 7e b")
        }
        assertFailsWith<IllegalArgumentException>("square 3e doesn't have your checker") {
            sut = sut.plays("3e 4f b")
        }
        // Play a valid move
        sut = sut.plays("6f 5e b")
    }
    @Test fun `Make a capture`() {
        var sut: Board = initialBoard()
        // Valid moves
        sut = sut.plays(
            "3c 4d w",
            "6d 5c b",
            "4d 5e w"
        )
        assertFailsWith<IllegalArgumentException>("There is a mandatory capture in 6f") {
            sut = sut.plays("6f 5g b")
        }
        assertNotNull(sut.moves[validateSqr("5e")])
        // Valid move
        sut = sut.plays("6f 4d b")
        assertNull(sut.moves[validateSqr("5e")])
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 1, sut.moves.size)
        val whiteCheckers = sut.moves.values.filter { it.player == Player.w }
        assertEquals(getPlayerPieces(BOARD_DIM) - 1, whiteCheckers.size)
        val blackCheckers = sut.moves.values.filter { it.player == Player.b }
        assertEquals(getPlayerPieces(BOARD_DIM), blackCheckers.size)
    }
    @Test fun `More than one capture is avalaible at one time`() {
        var sut: Board = initialBoard()
        sut = sut.plays(
            "3e 4f w",
            "6d 5c b",
            "4f 5g w"
        )
        val boardbeforeTheCapture = sut
        // Two captures are avalaible
        sut = sut.plays("6f 4h b")
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 1, sut.moves.size)
        sut = boardbeforeTheCapture.plays("6h 4f b")
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 1, sut.moves.size)
    }
    @Test fun `Make a black King`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = initialBoard()
        sut = sut.plays(
            "3e 4f w",
            "6d 5c b",
            "4f 5g w",
            "6f 4h b", // Capture(5g)
            "3g 4f w",
            "7e 6d b",
            "2f 3e w",
            "4h 3g b",
            "1e 2f w",
            "3g 1e b"  // Capture(2f), also a black piece was made King
        )
        assertTrue(sut.moves["1e".toSquareOrNull()] is King)
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 2, sut.moves.size)
    }
    @Test fun `Make a white King`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = initialBoard()
        sut = sut.plays(
            "3g 4h w",
            "6h 5g b",
            "2f 3g w",
            "5g 4f b",
            "3e 5g w", // Capture(4f)
            "6f 5e b",
            "5g 6h w",
            "7g 6f b",
            "3a 4b w",
            "8f 7g b",
            "6h 8f w", // Capture(7g), also a white piece was made King
        )
        assertTrue(sut.moves["8f".toSquareOrNull()] is King)
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 2, sut.moves.size)
    }
    @Test fun `Move a King backwards without capture`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = initialBoard()
        sut = sut.plays(
            "3g 4h w",
            "6h 5g b",
            "2f 3g w",
            "5g 4f b",
            "3e 5g w", // Capture(4f)
            "6f 5e b",
            "5g 6h w",
            "7g 6f b",
            "3a 4b w",
            "8f 7g b",
            "6h 8f w", // Capture(7g), also a white piece was made King
            "6b 5a b",
        )
        assertNull(sut[validateSqr("7g")])
        sut = sut.plays("8f 7g w") // Move king backwards
        assertNotNull(sut[validateSqr("7g")])
        assertTrue(sut[validateSqr("7g")] is King)
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - 2, sut.moves.size)
    }
    @Test fun `Win the game by capturing all opponent's checkers`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = initialBoard()
        sut = sut.plays(
            "3g 4h w",
            "6h 5g b",
            "2f 3g w",
            "5g 4f b",
            "3e 5g w", // Capture(4f)
            "6f 5e b",
            "5g 6h w",
            "7g 6f b",
            "3a 4b w",
            "8f 7g b",
            "6h 8f w", // Capture(7g), also a white piece was made King
            "6b 5c b",
            "4b 5a w",
            "6f 5g b",
            "4h 6f w", // Capture(5g)
            "6f 4d w", // Capture(5e)
            "4d 6b w", // Capture(5c)
            "7a 5c b", // Capture(6b)
            "3g 4h w",
            "6d 5e b",
            "8f 6d w", // Capture(7e) using King
            "6d 4f w", // Capture(5e) using King
            "5c 4d b",
            "3c 5e w", // Capture(4d)
            "8d 7e b",
            "2h 3g w",
            "8b 7a b",
            "5e 6d w",
            "7c 5e b", // Capture(6d)
            "4f 6d w", // Capture(5e)
            "6d 8f w", // Capture(7e)
            "7a 6b b",
            "5a 7c w", // Capture(6b)
            "8h 7g b",
            "8f 6h w", // Capture(7g)
        )
        assertTrue(sut is BoardWin)
        val numberOfCaptures = 14
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - numberOfCaptures, sut.moves.size)
        assertEquals(Player.w, sut.winner)
    }
    @Test fun `Win the game by blocking opponent's remaining checkers`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        var sut: Board = initialBoard()
        sut = sut.plays(
            "3g 4h w",
            "6h 5g b",
            "2f 3g w",
            "5g 4f b",
            "3e 5g w", // Capture(4f)
            "6f 5e b",
            "5g 6h w",
            "7g 6f b",
            "3a 4b w",
            "8f 7g b",
            "6h 8f w", // Capture(7g), also a white piece was made King
            "6b 5c b",
            "4b 5a w",
            "6f 5g b",
            "4h 6f w", // Capture(5g)
            "6f 4d w", // Capture(5e)
            "4d 6b w", // Capture(5c)
            "7a 5c b", // Capture(6b)
            "3g 4h w",
            "6d 5e b",
            "8f 6d w", // Capture(7e) using King
            "6d 4f w", // Capture(5e) using King
            "5c 4d b",
            "3c 5e w", // Capture(4d)
            "8d 7e b",
            "2h 3g w",
            "8b 7a b",
            "5e 6d w",
            "7e 5c b", // Capture(6d)
            "4h 5g w",
            "5c 4b b",
            "5a 3c w", // Capture(4b)
            "7a 6b b",
            "5g 6h w",
            "6b 5c b",
            "4f 5g w",
            "7c 6d b",
            "5g 6f w",
            "5c 4b b",
            "3c 5a w", // Capture(4b)
            "6d 5c b",
            "6h 7g w",
            "5c 4b b")
        sut = sut.plays("5a 3c w") // Capture(4b)
        assertTrue(sut is BoardWin)
        val numberOfCaptures = 13
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - numberOfCaptures, sut.moves.size)
        // Game was won but the player controlling the white checkes, but a black checker
        // still remain on the board
        assertNotNull(sut[validateSqr("8h")])
        assertEquals(Player.w, sut.winner)
    }
    @Test fun `Draw a game`() {
        require( BOARD_DIM == 8) { "Board dim should be 8 for this test" }
        require( MAX_MOVES_WITHOUT_CAPTURE == 10 )
            { "Max moves without capture should be 10 for this test" }
        var sut: Board = initialBoard()
        sut = sut.plays(
            "3g 4h w",
            "6h 5g b",
            "2f 3g w",
            "5g 4f b",
            "3e 5g w", // Capture(4f)
            "6f 5e b",
            "5g 6h w",
            "7g 6f b",
            "3a 4b w",
            "8f 7g b",
            "6h 8f w", // Capture(7g), also a white piece was made King
            "6b 5c b",
            "4b 5a w",
            "6f 5g b",
            "4h 6f w", // Capture(5g)
            "6f 4d w", // Capture(5e)
            "4d 6b w", // Capture(5c)
            "7a 5c b", // Capture(6b)
            "3g 4h w",
            "6d 5e b",
            "8f 6d w", // Capture(7e) using King
            "6d 4f w", // Capture(5e) using King
            "5c 4d b",
            "3c 5e w", // Capture(4d)
            "8d 7e b",
            "2h 3g w",
            "8b 7a b",
            "5e 6d w",
            "7e 5c b", // Capture(6d)
            "4h 5g w",
            "5c 4b b",
            "5a 3c w", // Capture(4b)
            "7a 6b b",
            "5g 6h w",
            "6b 5a b",
            "3c 4b w",
            "5a 3c b", // Capture(4b)
            "2b 4d w", // Capture(3c)
            "7c 6b b",
            "6h 7g w",
            "8h 6f b", // Capture(7g)
            "4d 5e w",
            "6f 4d b", // Capture(5e)
            "1a 2b w",
            "6b 5a b",
            "4f 5g w",
            "5a 4b b",
            "3g 4h w",
            "4b 3a b",
            "2b 3c w",
            "4d 2b b", // Capture(3c)
            "1e 2f w",
            "2b 1a b", // A black piece was made King
            "5g 6f w",
            "1a 2b b",
            "6f 5g w",
            "2b 1a b",
            "5g 6f w",
            "1a 2b b",
            "6f 5g w",
            "2b 1a b",
        )
        assertTrue(sut is BoardDraw)
        val numberOfCaptures = 16
        assertEquals(getPlayerPieces(BOARD_DIM)*2 - numberOfCaptures, sut.moves.size)
    }
}
