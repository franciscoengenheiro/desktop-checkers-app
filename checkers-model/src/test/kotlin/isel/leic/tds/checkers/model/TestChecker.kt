package isel.leic.tds.checkers.model

import kotlin.test.*

class TestChecker {
    // Suts
    private val sutPiece = Piece(player = Player.w)
    private val sutKing = King(player = Player.b)
    // Checkers
    private val whitePiece = Piece(player = Player.w)
    private val blackPiece = Piece(player = Player.b)
    private val whiteKing = King(player = Player.w)
    private val blackKing = King(player = Player.b)
    @Test fun `Equals method`() {
        assertEquals(whitePiece, sutPiece)
        assertNotEquals(blackPiece, sutPiece)
        assertNotEquals(whiteKing, sutKing)
        assertEquals(blackKing, sutKing)
    }
    @Test fun `Hash method`() {
        assertEquals(whitePiece.hashCode(), sutPiece.hashCode())
        assertNotEquals(blackPiece.hashCode(), sutPiece.hashCode())
        assertNotEquals(whiteKing.hashCode(), sutKing.hashCode())
        assertEquals(blackKing.hashCode(), sutKing.hashCode())
    }
}