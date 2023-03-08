package pt.isel.daw.battleship.domain.fleet

data class Ship(
    val shipType: ShipType,
    val layout: ShipLayout,
    val referencePoint: Position
)

enum class ShipType(val size: Int) {
    CARRIER(5),
    BATTLESHIP(4),
    SUBMARINE(3),
    CRUISER(3),
    DESTROYER(2)
}