package isel.leic.tds.checkers.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestChecker {
    @Test fun `Checker equality`() {
        // Suts
        val sutPiece = Piece(player = Player.w)
        val sutKing = King(player = Player.b)
        // Possible checkers
        val whitePiece = Piece(player = Player.w)
        val blackPiece = Piece(player = Player.b)
        val whiteKing = King(player = Player.w)
        val blackKing = King(player = Player.b)
        assertEquals(whitePiece, sutPiece)
        assertEquals(whitePiece.hashCode(), sutPiece.hashCode())
        assertNotEquals(blackPiece, sutPiece)
        assertNotEquals(blackPiece.hashCode(), sutPiece.hashCode())
        assertNotEquals(whiteKing, sutKing)
        assertNotEquals(whiteKing.hashCode(), sutKing.hashCode())
        assertEquals(blackKing, sutKing)
        assertEquals(blackKing.hashCode(), sutKing.hashCode())
    }
}