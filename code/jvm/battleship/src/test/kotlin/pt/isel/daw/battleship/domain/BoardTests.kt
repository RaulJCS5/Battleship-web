package pt.isel.daw.battleship.domain

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pt.isel.daw.battleship.domain.fleet.*

class BoardTests {

    @Test
    fun `create board set fleet and sunk all`() {
        // first: create board
        val board = Board.create()
        // second: check if board is empty (test method)
        assertTrue(board.boardIsEmpty())
        // third: create valid fleet
        val carrier = Ship(ShipType.CARRIER, ShipLayout(ShipLayout.UP), Position(0, 0))
        val battleship = Ship(ShipType.BATTLESHIP, ShipLayout(ShipLayout.UP), Position(0, 1))
        val submarine = Ship(ShipType.SUBMARINE, ShipLayout(ShipLayout.DOWN), Position(4, 7))
        val cruiser = Ship(ShipType.CRUISER, ShipLayout(ShipLayout.LEFT), Position(8, 1))
        val destroyer = Ship(ShipType.DESTROYER, ShipLayout(ShipLayout.RIGHT), Position(9, 6))
        val fleet = Fleet(carrier, battleship, submarine, cruiser, destroyer)
        //Check if fleet is valid, and then set fleet
        assertTrue(board.checkAndSetFleet(fleet))

        //Do the shoot, sunk all without mercy!
        //sunk carrier
        board.doShoot(Position(0, 0))
        board.doShoot(Position(1, 0))
        board.doShoot(Position(2, 0))
        board.doShoot(Position(3, 0))
        board.doShoot(Position(4, 0))
        //sunk battleship
        board.doShoot(Position(0, 1))
        board.doShoot(Position(1, 1))
        board.doShoot(Position(2, 1))
        board.doShoot(Position(3, 1))
        //sunk submarine
        board.doShoot(Position(2, 7))
        board.doShoot(Position(3, 7))
        board.doShoot(Position(4, 7))
        //sunk cruiser
        board.doShoot(Position(8, 1))
        board.doShoot(Position(8, 2))
        board.doShoot(Position(8, 3))
        //sunk destroyer
        board.doShoot(Position(9, 5))
        board.doShoot(Position(9, 6))

        //check if all ships of fleet is sunk
        assertTrue(board.allShipsSunk())

        //submarine is sunk with checkShipStatus() method
        assertTrue(board.checkShipStatus(ShipType.SUBMARINE).sunk)

        //check that all five boats of fleet are shot with (checkFleetStatus)
        assertTrue(board.checkFleetStatus().numberShipsHit == 5)
    }

    @Test
    fun `create board, set fleet and check fleet`() {
        // first: create board
        val board = Board.create()
        // second: check if board is empty (test method)
        assertTrue(board.boardIsEmpty())
        // third: create valid fleet
        val carrier = Ship(ShipType.CARRIER, ShipLayout(ShipLayout.UP), Position(0, 0))
        val battleship = Ship(ShipType.BATTLESHIP, ShipLayout(ShipLayout.UP), Position(0, 1))
        val submarine = Ship(ShipType.SUBMARINE, ShipLayout(ShipLayout.DOWN), Position(4, 7))
        val cruiser = Ship(ShipType.CRUISER, ShipLayout(ShipLayout.LEFT), Position(8, 1))
        val destroyer = Ship(ShipType.DESTROYER, ShipLayout(ShipLayout.RIGHT), Position(9, 6))
        val fleet = Fleet(carrier, battleship, submarine, cruiser, destroyer)
        //Check if fleet is valid, and then set fleet
        assertTrue(board.checkAndSetFleet(fleet))

        //Check if was CARRIER in position (0,0)
        val c00: PositionStateBoard = board.getPositionStatus(Position(0, 0))
        assertTrue(c00.wasShip == true && c00.shipType == ShipType.CARRIER)
        //Check if was CARRIER in position (4,0)
        val c40: PositionStateBoard = board.getPositionStatus(Position(4, 0))
        assertTrue(c40.wasShip == true && c40.shipType == ShipType.CARRIER)
        //Check if was DESTROYER in position (9,5)
        val c95: PositionStateBoard = board.getPositionStatus(Position(9, 5))
        assertTrue(c95.wasShip == true && c95.shipType == ShipType.DESTROYER)
        //Check if was noShip in position (4,4)
        val c44: PositionStateBoard = board.getPositionStatus(Position(4, 4))
        assertTrue(!c44.wasShip!! && c44.shipType == null)
        //Check hasShipOnPosition() method
        assertTrue(board.hasShipOnPosition(Position(0, 0)))
        assertFalse(board.hasShipOnPosition(Position(4, 4)))
        //Check doShoot method
        board.doShoot(Position(0, 0))
        assertTrue(board.alreadyShootPosition(Position(0, 0)))
        assertFalse(board.alreadyShootPosition(Position(4, 4)))
    }
}