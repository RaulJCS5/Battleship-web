package pt.isel.daw.battleship.domain.fleet

// *Fleet Type*
// Carrier -> Size: 5
// Battleship -> Size: 4
// Submarine -> Size: 3
// Cruiser -> Size: 3
// Destroyer -> Size: 2
data class Fleet(
    val carrier: Ship,
    val battleShip: Ship,
    val submarine: Ship,
    val cruiser: Ship,
    val destroyer: Ship
) {

    private fun validateInputFleet(): Boolean {
        return (carrier.shipType == ShipType.CARRIER &&
                battleShip.shipType == ShipType.BATTLESHIP &&
                submarine.shipType == ShipType.SUBMARINE &&
                cruiser.shipType == ShipType.CRUISER &&
                destroyer.shipType == ShipType.DESTROYER)
    }

    fun getFeetShips(): List<Ship>? {
        val ships: MutableList<Ship> = ArrayList()

        if (validateInputFleet()) {
            ships.add(carrier)
            ships.add(battleShip)
            ships.add(submarine)
            ships.add(cruiser)
            ships.add(destroyer)
            return ships
        }
        return null
    }
}