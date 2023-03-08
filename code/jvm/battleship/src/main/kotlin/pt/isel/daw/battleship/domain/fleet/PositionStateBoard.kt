package pt.isel.daw.battleship.domain.fleet

data class PositionStateBoard(
    val boardPosition:Position,
    var wasShoot:Boolean,
    var wasShip: Boolean?,
    var shipType: ShipType?,
    var shipLayout: String?
)