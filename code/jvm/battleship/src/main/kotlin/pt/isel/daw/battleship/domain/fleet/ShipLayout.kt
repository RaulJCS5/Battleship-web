package pt.isel.daw.battleship.domain.fleet

class ShipLayout(
    val value: String
) {
    companion object {
        const val UP = "UP"
        const val DOWN = "DOWN"
        const val LEFT = "LEFT"
        const val RIGHT = "RIGHT"
    }
}