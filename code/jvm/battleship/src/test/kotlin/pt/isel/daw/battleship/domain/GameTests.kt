package pt.isel.daw.battleship.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.daw.battleship.domain.fleet.*

class GameTests {

    companion object {
        private val gameLogic = GameLogic(
        )

        // our test players
        private val td = User(1, "tiago", "tduarte@pse.pt", PasswordValidationInfo("pass1"))
        private val ronaldo = User(2, "ronaldo", "ronaldo@fpf.pt", PasswordValidationInfo("pass2"))
    }

    @Test
    fun `check pos & fleet state `() {
        // given: a game
        val game = gameLogic.createNewGame(td, ronaldo, 2, 180)
        // first: create board
        val board = Board.create()
        // second: check if board is empty (test method)
        Assertions.assertTrue(board.boardIsEmpty())
        // third: create valid fleet
        val carrier = Ship(ShipType.CARRIER, ShipLayout(ShipLayout.UP), Position(0, 0))
        val battleship = Ship(ShipType.BATTLESHIP, ShipLayout(ShipLayout.UP), Position(0, 1))
        val submarine = Ship(ShipType.SUBMARINE, ShipLayout(ShipLayout.DOWN), Position(4, 7))
        val cruiser = Ship(ShipType.CRUISER, ShipLayout(ShipLayout.LEFT), Position(8, 1))
        val destroyer = Ship(ShipType.DESTROYER, ShipLayout(ShipLayout.RIGHT), Position(9, 6))
        val fleet = Fleet(carrier, battleship, submarine, cruiser, destroyer)
        //Check if fleet is valid, and then set fleet
        Assertions.assertTrue(board.checkAndSetFleet(fleet))
        gameLogic.setShipsToBoard(game, td, fleet)
        gameLogic.setShipsToBoard(game, ronaldo, fleet)
        //Game is ready
        Assertions.assertTrue(gameLogic.ifLayoutReadyStartGame(game))
        Assertions.assertEquals(game.phase, Game.Phase.SHOOTING_PLAYER_ONE.phase)
        // first round user is player one
        var activeRoundUser = game.getGameRoundUser()
        Assertions.assertEquals(activeRoundUser, td)
        Assertions.assertTrue(game.playerOne == td)
        //Do one shoot
        Assertions.assertTrue(gameLogic.doShoot(game, td, Position(0, 0)))
        //Do second shoot
        Assertions.assertTrue(gameLogic.doShoot(game, td, Position(1, 1)))
        //next round
        gameLogic.setNextRoundUser(game)
        //playerTwo
        Assertions.assertEquals(game.getGameRoundUser(), ronaldo)
        //validate position status
        val myPositionStatus = game.getPositionStatus(ronaldo,Position(0,0),true)
        Assertions.assertNotNull(myPositionStatus)
        myPositionStatus?.let { Assertions.assertTrue(it.wasShoot)}
        myPositionStatus?.let { Assertions.assertTrue(it.wasShip == true)}
        Assertions.assertEquals(myPositionStatus!!.shipType,ShipType.CARRIER)
        //validate fleet status
        val myFleetStatus = game.getFleetStatus(ronaldo,true)
        Assertions.assertNotNull(myFleetStatus)
        //Two ships of fleet are hit
        Assertions.assertTrue(myFleetStatus!!.numberShipsHit==2)
        Assertions.assertTrue(myFleetStatus.numberShipsSunk==0)
        //validate opponent fleet status
        val opponentFleetStatus = game.getFleetStatus(td,false)
        Assertions.assertNotNull(opponentFleetStatus)
        //Two ships of fleet are hit
        Assertions.assertTrue(opponentFleetStatus!!.numberShipsHit==2)
        Assertions.assertTrue(opponentFleetStatus.numberShipsSunk==0)
        //Final user cant check opponent position if no hit that position yet
        val opponentPositionStatus = game.getPositionStatus(td,Position(7,7),false)
        Assertions.assertNull(opponentPositionStatus)
        //But can check opponent position if already hit
        val opponentPositionStatus2 = game.getPositionStatus(td,Position(0,0),false)
        Assertions.assertNotNull(opponentPositionStatus2)
    }
}