package checkers.model.moves.move

/**
 * Checker is a sealed class because this class can only two instances, which represent
 * the possible types of checkers in the game
 */
sealed class Checker(val player: Player)

/**
 * Represents a regular checker on the board belonging to a [Player].
 **/
class Piece (player: Player): Checker(player) {
    override fun toString() = player.name
    // A Piece is equal to another checker if it has the same type and player assigned to it
    override fun equals(other: Any?) = if (other !is Piece) false
        else this.player === other.player
    override fun hashCode() = player.hashCode() * Piece::class.hashCode()
}

/**
 * Represents a regular checker upgraded to King, which occurs when
 * it reaches the opponent's first row. This checker belongs to a [Player].
 **/
class King(player: Player): Checker(player) {
    override fun toString() = player.name.uppercase()
    // A King is equal to another checker if it has the same type and player assigned to it
    override fun equals(other: Any?) = if (other !is King) false
        else this.player === other.player
    override fun hashCode() = player.hashCode() * King::class.hashCode()
}