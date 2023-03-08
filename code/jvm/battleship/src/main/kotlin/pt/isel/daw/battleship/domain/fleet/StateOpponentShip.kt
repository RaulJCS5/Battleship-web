package pt.isel.daw.battleship.domain.fleet

data class StateOpponentShip(
    val shipsHit: MutableList<Position>,
    val shipsMiss: MutableList<Position>,
)