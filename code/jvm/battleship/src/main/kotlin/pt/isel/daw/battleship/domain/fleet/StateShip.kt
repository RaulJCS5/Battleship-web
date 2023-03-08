package pt.isel.daw.battleship.domain.fleet

data class StateShip(
    val shipHits: Int,
    val positionsHit: List<Position>,
    val sunk: Boolean
)