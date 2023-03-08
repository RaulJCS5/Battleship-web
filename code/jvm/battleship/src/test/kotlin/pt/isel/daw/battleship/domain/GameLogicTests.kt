package pt.isel.daw.battleship.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.daw.battleship.domain.fleet.*

class GameLogicTests {

    companion object {
        private val gameLogic = GameLogic(
        )

        // our test players
        private val td = User(1, "tiago", "tduarte@pse.pt", PasswordValidationInfo("pass1"))
        private val ronaldo = User(2, "ronaldo", "ronaldo@fpf.pt", PasswordValidationInfo("pass2"))
    }

    @Test
    fun `simple game`() {
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
        //Game is not ready
        Assertions.assertFalse(gameLogic.ifLayoutReadyStartGame(game))
        Assertions.assertEquals(game.phase, Game.Phase.LAYOUT.phase)
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
        //User turn not end yet, because has two shoots
        Assertions.assertFalse(gameLogic.setNextRoundUser(game))
        activeRoundUser = game.getGameRoundUser()
        Assertions.assertEquals(activeRoundUser, td)
        //Do second shoot
        Assertions.assertTrue(gameLogic.doShoot(game, activeRoundUser, Position(1, 1)))
        Assertions.assertTrue(game.shootsPerRoundCount == 2)
        //User turn end
        gameLogic.setNextRoundUser(game)
        Assertions.assertEquals(game.phase, Game.Phase.SHOOTING_PLAYER_TWO.phase)
        Assertions.assertTrue(game.shootsPerRoundCount == 0)
        //Player one can't shoot, wrong turn
        Assertions.assertFalse(gameLogic.doShoot(game, td, Position(7, 7)))
        //Player two shoot
        Assertions.assertTrue(gameLogic.doShoot(game, ronaldo, Position(0, 0)))
        Assertions.assertTrue(gameLogic.doShoot(game, ronaldo, Position(1, 1)))
        //Player one turn again
        gameLogic.setNextRoundUser(game)
        Assertions.assertEquals(game.phase, Game.Phase.SHOOTING_PLAYER_ONE.phase)
    }

    @Test
    fun `game with a winner`() {
        // given: a game with 150 shoot to easy win
        val game = gameLogic.createNewGame(td, ronaldo, 17, 180)
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
        //Do the shoot, sunk all without mercy!
        //sunk carrier
        gameLogic.doShoot(game, game.activeRoundUser, Position(0, 0))
        gameLogic.doShoot(game, game.activeRoundUser, Position(1, 0))
        gameLogic.doShoot(game, game.activeRoundUser, Position(2, 0))
        gameLogic.doShoot(game, game.activeRoundUser, Position(3, 0))
        gameLogic.doShoot(game, game.activeRoundUser, Position(4, 0))
        //sunk battleship
        gameLogic.doShoot(game, game.activeRoundUser, Position(0, 1))
        gameLogic.doShoot(game, game.activeRoundUser, Position(1, 1))
        gameLogic.doShoot(game, game.activeRoundUser, Position(2, 1))
        gameLogic.doShoot(game, game.activeRoundUser, Position(3, 1))
        //sunk submarine
        gameLogic.doShoot(game, game.activeRoundUser, Position(2, 7))
        gameLogic.doShoot(game, game.activeRoundUser, Position(3, 7))
        gameLogic.doShoot(game, game.activeRoundUser, Position(4, 7))
        //sunk cruiser
        gameLogic.doShoot(game, game.activeRoundUser, Position(8, 1))
        gameLogic.doShoot(game, game.activeRoundUser, Position(8, 2))
        gameLogic.doShoot(game, game.activeRoundUser, Position(8, 3))
        //sunk destroyer
        gameLogic.doShoot(game, game.activeRoundUser, Position(9, 5))
        gameLogic.doShoot(game, game.activeRoundUser, Position(9, 6))

        gameLogic.setNextRoundUser(game)
        //Not exits next phase, player one win!
        Assertions.assertEquals(game.phase, Game.Phase.PLAYER_ONE_WON.phase)
    }
}