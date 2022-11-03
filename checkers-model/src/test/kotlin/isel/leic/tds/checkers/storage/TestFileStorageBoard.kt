package isel.leic.tds.checkers.storage

import kotlin.test.*
import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.ui.BoardSerializer.parse
import isel.leic.tds.checkers.ui.BoardSerializer.write
import isel.leic.tds.checkers.plays

class TestFileStorageBoard {
    @Test fun `Get an equivalent BoardRun from serialize and deserialize`() {
        val board = initialBoard().plays(
            "3c 4d w",
            "6d 5c b"
        )
        val actual = parse(write(board))
        assertEquals(board.moves, actual.moves)
        assertNotSame(board, actual)
        assertIs<BoardRun>(actual)
    }
    @Test fun `Get an equivalent BoardWin from serialize and deserialize`() {
        val board = initialBoard().plays(
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
        val actual = parse(write(board))
        assertEquals(board.moves, actual.moves)
        assertNotSame(board, actual)
        assertIs<BoardWin>(actual)
    }
    @Test fun `Get an equivalent BoardDraw from serialize and deserialize`() {
        val board = initialBoard().plays(
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
        val actual = parse(write(board))
        assertEquals(board.moves, actual.moves)
        assertNotSame(board, actual)
        assertIs<BoardDraw>(actual)
    }
}
