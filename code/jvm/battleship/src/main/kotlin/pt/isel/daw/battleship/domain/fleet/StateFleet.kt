package pt.isel.daw.battleship.domain.fleet

data class StateFleet(
    val numberShipsSunk: Int,
    val numberShipsHit: Int,
    val shipsTypeSunk: List<ShipType>
)
