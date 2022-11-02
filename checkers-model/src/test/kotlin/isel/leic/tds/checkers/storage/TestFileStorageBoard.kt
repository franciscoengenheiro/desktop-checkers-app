/*
package isel.leic.tds.checkers.storage

import kotlin.test.*
import java.io.File
import isel.leic.tds.checkers.model.*
import java.lang.System.lineSeparator

class TestFileStorageBoard {
    object BoardSerializer: Serializer<Board, String> {
        override fun write(obj: Board): String {
            val boardType = obj::class.simpleName
            return obj.moves.map { it.key.toString() + it.value } // <index><Player>
                .joinToString(lineSeparator())
        }
        override fun parse(stream: String): Board {
            val words = stream.split(lineSeparator())
            val boardType = words.first()
            // Associate -> assumes key and value
            val moves = words.associate {
                    require(it.length == 2)
                        { "Each line must have exaclty 2 chars with <?><?>" }
                    Move(it.first().toSquareOrNull(), Player.valueOf(it[1].toString()))
                }
                // TODO("continue")
        }
    }

    @Test fun `Check if we get an equivalent Board from serealize and deserialize`() {
        val board = initialBoard()
        val actual = parse(write(board))
        assertEquals(board.moves, actual)
    }

    // TODO ("make tests for every Board instance")
    @Test fun `Check if we get an equivalent BoardWin?  from serealize and deserialize`() {
        val board = initialBoard()
        val actual = parse(write(board))
        assertEquals(board.moves, actual)
    }
}*/
