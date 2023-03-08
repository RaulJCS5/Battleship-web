package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.domain.fleet.*

data class Board(
    var cells: Array<Array<PositionStateBoard>>
) {
    init {
        require(cells.size == 10 && cells.all { it.size == 10 })
    }

    //check if ships are inside board
    //check if ships are not overlaid
    //if okay set ship layout
    //already checked position 0->9 in PositionModel
    //already checked fleet is complete in FleetModel
    fun checkAndSetFleet(fleet: Fleet): Boolean {
        var result = true
        //get ships of fleet
        val ships = fleet.getFeetShips()
        // check ship positions in board
        if (ships != null) {
            ships.forEach() {
                when (it.layout.value) {
                    ShipLayout.RIGHT -> {
                        if (it.referencePoint.col - it.shipType.size > cells.size) {
                            result = false
                        }
                        var count = 0
                        while (count < it.shipType.size) {
                            val x = it.referencePoint.row
                            val y = it.referencePoint.col - count
                            if (cells[x][y].wasShip == true) {
                                result = false;break
                            }
                            cells[x][y] = PositionStateBoard(
                                boardPosition = Position(x, y),
                                wasShoot = false, wasShip = true, shipType = it.shipType, shipLayout = ShipLayout.RIGHT
                            )
                            count++
                        }
                    }

                    ShipLayout.LEFT -> {
                        if (it.referencePoint.col + it.shipType.size > cells.size) {
                            result = false
                        }
                        var count = 0
                        while (count < it.shipType.size) {
                            val x = it.referencePoint.row
                            val y = it.referencePoint.col + count
                            if (cells[x][y].wasShip == true) {
                                result = false;break
                            }
                            cells[x][y] = PositionStateBoard(
                                boardPosition = Position(x, y),
                                wasShoot = false, wasShip = true, it.shipType, shipLayout = ShipLayout.LEFT
                            )
                            count++
                        }
                    }

                    ShipLayout.DOWN -> {
                        if (it.referencePoint.row - it.shipType.size > cells.size) {
                            result = false
                        }
                        var count = 0
                        while (count < it.shipType.size) {
                            val x = it.referencePoint.row - count
                            val y = it.referencePoint.col
                            if (cells[x][y].wasShip == true) {
                                result = false;break
                            }
                            cells[x][y] = PositionStateBoard(
                                boardPosition = Position(x, y),
                                wasShoot = false, wasShip = true, it.shipType, shipLayout = ShipLayout.DOWN
                            )
                            count++
                        }
                    }

                    ShipLayout.UP -> {
                        if (it.referencePoint.row + it.shipType.size > cells.size) {
                            result = false
                        }
                        var count = 0
                        while (count < it.shipType.size) {
                            val x = it.referencePoint.row + count
                            val y = it.referencePoint.col
                            if (cells[x][y].wasShip == true) {
                                result = false;break
                            }
                            cells[x][y] = PositionStateBoard(
                                boardPosition = Position(x, y),
                                wasShoot = false, wasShip = true, it.shipType, shipLayout = ShipLayout.UP
                            )
                            count++
                        }
                    }
                }
            }
        } else {
            result = false
        }
        //if fleet creation fail, reset board
        if (!result) {
            resetBoard()
        }
        return result
    }

    // check status of @ship
    fun checkShipStatus(ship: ShipType): StateShip {
        val positionsHits: MutableList<Position> = ArrayList()
        for (i in cells.indices) {
            for (j in cells.indices) {
                if (cells[i][j].wasShip == true && cells[i][j].shipType == ship && cells[i][j].wasShoot) {
                    positionsHits.add(Position(i, j))
                }
            }
        }
        //check if ship is sunk
        val sunk = positionsHits.size >= ship.size

        return StateShip(positionsHits.size, positionsHits, sunk)
    }

    //check fleet status
    fun checkFleetStatus(): StateFleet {
        var nrShipsHits = 0
        var nrShipsSunk = 0
        val listOfTypeShipSunk: MutableList<ShipType> = ArrayList()
        //Check fleet ship status
        val carrier = checkShipStatus(ShipType.CARRIER)
        val battleShip = checkShipStatus(ShipType.BATTLESHIP)
        val submarine = checkShipStatus(ShipType.SUBMARINE)
        val cruiser = checkShipStatus(ShipType.CRUISER)
        val destroyer = checkShipStatus(ShipType.DESTROYER)

        //count number ships sunk and hits
        if (carrier.shipHits > 0) {
            nrShipsHits++
            if (carrier.sunk) {
                listOfTypeShipSunk.add(ShipType.CARRIER)
                nrShipsSunk++
            }
        }
        if (battleShip.shipHits > 0) {
            nrShipsHits++
            if (battleShip.sunk) {
                listOfTypeShipSunk.add(ShipType.BATTLESHIP)
                nrShipsSunk++
            }
        }
        if (submarine.shipHits > 0) {
            nrShipsHits++
            if (submarine.sunk) {
                listOfTypeShipSunk.add(ShipType.SUBMARINE)
                nrShipsSunk++
            }
        }
        if (cruiser.shipHits > 0) {
            nrShipsHits++
            if (cruiser.sunk) {
                listOfTypeShipSunk.add(ShipType.CRUISER)
                nrShipsSunk++
            }
        }
        if (destroyer.shipHits > 0) {
            nrShipsHits++
            if (destroyer.sunk) {
                listOfTypeShipSunk.add(ShipType.DESTROYER)
                nrShipsSunk++
            }
        }
        return StateFleet(nrShipsSunk, nrShipsHits, listOfTypeShipSunk)
    }

    // return the position status of board x,y
    fun getPositionStatus(position: Position): PositionStateBoard = cells[position.row][position.col]

    // shoot position (x,y) in board
    fun doShoot(pos: Position) {
        cells[pos.row][pos.col].wasShoot = true
    }

    // check if the position is already shoot
    fun alreadyShootPosition(position: Position): Boolean = getPositionStatus(position).wasShoot

    //check if exists ship on position x,y
    fun hasShipOnPosition(position: Position): Boolean = getPositionStatus(position).wasShip == true

    //check if all the ships are sunk
    fun allShipsSunk(): Boolean {
        var sunk = true
        cells.forEach { it ->
            it.forEach {
                if (it.wasShip == true && !it.wasShoot) {
                    sunk = false
                }
            }
        }
        return sunk
    }

    //check if set fleet phase is done
    fun boardIsEmpty(): Boolean {
        var empty = true
        cells.forEach { it ->
            it.forEach {
                if (it.wasShip == true) {
                    empty = false
                }
            }
        }
        return empty
    }

    //reset board to initial state, only water
    private fun resetBoard() {
        cells = Array(10) { x ->
            Array(10) { y ->
                PositionStateBoard(
                    boardPosition = Position(x, y),
                    wasShoot = false, wasShip = false, shipType = null, shipLayout = null
                )
            }
        }
    }

    companion object {
        //create initial state, empty board, only water
        fun create() = Board(Array(10) { x ->
            Array(10) { y ->
                PositionStateBoard(
                    boardPosition = Position(x, y),
                    wasShoot = false, wasShip = false, shipType = null, shipLayout = null
                )
            }
        })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (!cells.contentDeepEquals(other.cells)) return false

        return true
    }

    override fun hashCode(): Int {
        return cells.contentDeepHashCode()
    }
}