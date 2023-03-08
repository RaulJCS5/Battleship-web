package pt.isel.daw.battleship.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.domain.fleet.*
import pt.isel.daw.battleship.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.battleship.repository.jdbi.JdbiUsersRepository
import pt.isel.daw.battleship.utils.testWithHandleAndRollback
import java.sql.Timestamp
import java.time.Instant

class GameRepositoryTests {

    @Test
    fun `can create and retrieve`(): Unit = testWithHandleAndRollback { handle ->

        // given: repositories and logic
        val userRepo = JdbiUsersRepository(handle)
        val gameRepo = JdbiGamesRepository(handle)

        // and: two existing users
        userRepo.storeUser("tom", "tom@isel.pt", PasswordValidationInfo("pass1"))
        userRepo.storeUser("jerry", "jerry@isel.pt", PasswordValidationInfo("pass2"))
        val player1 = userRepo.getUserByUsername("tom") ?: fail("user must exist")
        val player2 = userRepo.getUserByUsername("jerry") ?: fail("user must exist")

        // and: game logic to test repository
        val gameLogic = GameLogic()
        val game = gameLogic.createNewGame(player1, player2, 5, 500)

        // then: create board
        val board = Board.create()

        // then: create valid fleet (in this case, for tests purpose use the same fleet p1 and p2)
        val carrier = Ship(ShipType.CARRIER, ShipLayout(ShipLayout.UP), Position(0, 0))
        val battleship = Ship(ShipType.BATTLESHIP, ShipLayout(ShipLayout.UP), Position(0, 1))
        val submarine = Ship(ShipType.SUBMARINE, ShipLayout(ShipLayout.DOWN), Position(4, 7))
        val cruiser = Ship(ShipType.CRUISER, ShipLayout(ShipLayout.LEFT), Position(8, 1))
        val destroyer = Ship(ShipType.DESTROYER, ShipLayout(ShipLayout.RIGHT), Position(9, 6))
        val fleet = Fleet(carrier, battleship, submarine, cruiser, destroyer)
        //Check if fleet is valid, and then set fleet
        assertTrue(board.checkAndSetFleet(fleet))
        gameLogic.setShipsToBoard(game, player1, fleet)
        gameLogic.setShipsToBoard(game, player2, fleet)

        // then: create game and store database generated id
        val gameGeneratedId = gameRepo.create(game)

        // then: update gameId with generated value from database
        if (gameGeneratedId != null) {
            game.id = gameGeneratedId.mapToMap().one()["id"] as Int
        }
        // and: retrieving the game
        var retrievedGame = game.id?.let { gameRepo.getGameById(it) }
        assertNotNull(retrievedGame)

        // then: specs of two games are equal (database and object game)
        if (retrievedGame != null) {
            assertEquals(game.id, retrievedGame.id)
            assertEquals(game.playerOne.username, retrievedGame.playerOne.username)
            assertEquals(game.playerTwo.username, retrievedGame.playerTwo.username)
            assertEquals(game.phase.name, retrievedGame.phase.name)
            assertEquals(game.boardPlayerOne, retrievedGame.boardPlayerOne)
            assertEquals(game.boardPlayerTwo, retrievedGame.boardPlayerTwo)
            assertEquals(game.activeRoundUser, retrievedGame.activeRoundUser)
            assertEquals(game.maxTimePerRound, retrievedGame.maxTimePerRound)
            assertEquals(game.maxShootsRule, retrievedGame.maxShootsRule)
            assertEquals(game.roundNumber, retrievedGame.roundNumber)
            assertEquals(
                game.roundDeadline.toString().substring(0, 19),
                retrievedGame.roundDeadline.toString().substring(0, 19)
            )
            assertEquals(
                game.startDate.toString().substring(0, 19),
                retrievedGame.startDate.toString().substring(0, 19)
            )
            assertEquals(retrievedGame.phase.name, Game.Phase.LAYOUT.name)
        }

        //then: update game with shoot and phase
        game.phase = Game.Phase.SHOOTING_PLAYER_ONE.phase
        //do shoot at position (0,0)
        gameLogic.doShoot(game, player1, Position(0, 0))
        game.updateDate = Timestamp.from(Instant.now())
        gameRepo.update(game)
        // and: retrieving the game again
        retrievedGame = game.id?.let { gameRepo.getGameById(it) }
        assertNotNull(retrievedGame)

        // then: after update specs of two games are equal (database and object game)
        if (retrievedGame != null) {
            assertEquals(game.id, retrievedGame.id)
            assertEquals(game.playerOne.username, retrievedGame.playerOne.username)
            assertEquals(game.playerTwo.username, retrievedGame.playerTwo.username)
            assertEquals(game.phase.name, retrievedGame.phase.name)
            assertEquals(game.boardPlayerOne, retrievedGame.boardPlayerOne)
            assertEquals(game.boardPlayerTwo, retrievedGame.boardPlayerTwo)
            assertEquals(game.activeRoundUser, retrievedGame.activeRoundUser)
            assertEquals(game.maxTimePerRound, retrievedGame.maxTimePerRound)
            assertEquals(game.maxShootsRule, retrievedGame.maxShootsRule)
            assertEquals(game.roundNumber, retrievedGame.roundNumber)
            assertEquals(
                game.roundDeadline.toString().substring(0, 19),
                retrievedGame.roundDeadline.toString().substring(0, 19)
            )
            assertEquals(
                game.startDate.toString().substring(0, 19),
                retrievedGame.startDate.toString().substring(0, 19)
            )
            assertEquals(
                game.updateDate.toString().substring(0, 19),
                retrievedGame.updateDate.toString().substring(0, 19)
            )
            assertEquals(retrievedGame.phase.name, Game.Phase.SHOOTING_PLAYER_ONE.name)
        }
    }

    @Test
    fun `lobby room tests`(): Unit = testWithHandleAndRollback { handle ->
        // given: repositories and logic
        val userRepo = JdbiUsersRepository(handle)
        val gameRepo = JdbiGamesRepository(handle)

        // and: two existing users
        userRepo.storeUser("tom", "tom@isel.pt", PasswordValidationInfo("pass1"))
        userRepo.storeUser("tom2", "tom2@isel.pt", PasswordValidationInfo("pass1"))
        val player1 = userRepo.getUserByUsername("tom") ?: fail("user must exist")
        val player2 = userRepo.getUserByUsername("tom2") ?: fail("user must exist")

        // then: put user to lobby waiting room
        val lobby = gameRepo.setUserToWaitingLobby(player1, 1)
        assertEquals(lobby, 1)

        // then check if user is already in lobby
        val userInLobby = gameRepo.isUserAlreadyInLobby(player1)
        assertTrue(userInLobby)

        // then remove user from lobby
        gameRepo.removeUsersFromLobby(player1)

        // then check if user is already in lobby
        val userInLobbyAfter = gameRepo.isUserAlreadyInLobby(player1)
        assertFalse(userInLobbyAfter)

        // then: put two user in lobby waiting room
        gameRepo.setUserToWaitingLobby(player1, 1)
        gameRepo.setUserToWaitingLobby(player2, 2)
        // then: validate if the users are in lobby
        assertTrue(gameRepo.isUserAlreadyInLobby(player1))
        assertTrue(gameRepo.isUserAlreadyInLobby(player2))

        // then remove user1 from lobby
        gameRepo.removeUsersFromLobby(player1)
        assertFalse(gameRepo.isUserAlreadyInLobby(player1))
        assertTrue(gameRepo.isUserAlreadyInLobby(player2))

        // then remove user1 & user2 from lobby
        // need to add user 1 again to lobby
        gameRepo.setUserToWaitingLobby(player1, 2)
        gameRepo.removeUsersFromLobby(player1)
        gameRepo.removeUsersFromLobby(player2)
        assertFalse(gameRepo.isUserAlreadyInLobby(player1))
        assertFalse(gameRepo.isUserAlreadyInLobby(player2))
    }

    @Test
    fun `lobby room pair tests`(): Unit = testWithHandleAndRollback { handle ->
        // given: repositories and logic
        val userRepo = JdbiUsersRepository(handle)
        val gameRepo = JdbiGamesRepository(handle)

        // and: one existing users
        userRepo.storeUser("tom", "tom@isel.pt", PasswordValidationInfo("pass1"))
        val player1 = userRepo.getUserByUsername("tom") ?: fail("user must exist")

        // then: put user to lobby waiting room
        gameRepo.setUserToWaitingLobby(player1, 1)

        // then check if user is already in lobby
        val userInLobby = gameRepo.isUserAlreadyInLobby(player1)
        assertTrue(userInLobby)
        assertNull(gameRepo.pairLobbyUser(5))

        // pair user
        val pairUser = gameRepo.pairLobbyUser(1)
        assertEquals(pairUser?.username, player1.username)

        // then remove player from lobby
        if (pairUser != null) {
            gameRepo.removeUsersFromLobby(pairUser)
        }
        val userInLobby2 = gameRepo.isUserAlreadyInLobby(player1)
        assertFalse(userInLobby2)
    }

    @Test
    fun `can get history of games (User)`(): Unit = testWithHandleAndRollback { handle ->

        // given: repositories and logic
        val userRepo = JdbiUsersRepository(handle)
        val gameRepo = JdbiGamesRepository(handle)

        // and: two existing users
        userRepo.storeUser("tom", "tom@isel.pt", PasswordValidationInfo("pass1"))
        userRepo.storeUser("jerry", "jerry@isel.pt", PasswordValidationInfo("pass2"))
        val player1 = userRepo.getUserByUsername("tom") ?: fail("user must exist")
        val player2 = userRepo.getUserByUsername("jerry") ?: fail("user must exist")

        // and: game logic to test repository
        val gameLogic = GameLogic()
        val game = gameLogic.createNewGame(player1, player2, 5, 10)
        //Mix players
        val game2 = gameLogic.createNewGame(player2, player1, 5, 10)

        // then: create board
        val board = Board.create()

        // then: create valid fleet (in this case, for tests purpose use the same fleet p1 and p2)
        val carrier = Ship(ShipType.CARRIER, ShipLayout(ShipLayout.UP), Position(0, 0))
        val battleship = Ship(ShipType.BATTLESHIP, ShipLayout(ShipLayout.UP), Position(0, 1))
        val submarine = Ship(ShipType.SUBMARINE, ShipLayout(ShipLayout.DOWN), Position(4, 7))
        val cruiser = Ship(ShipType.CRUISER, ShipLayout(ShipLayout.LEFT), Position(8, 1))
        val destroyer = Ship(ShipType.DESTROYER, ShipLayout(ShipLayout.RIGHT), Position(9, 6))
        val fleet = Fleet(carrier, battleship, submarine, cruiser, destroyer)
        //Check if fleet is valid, and then set fleet
        assertTrue(board.checkAndSetFleet(fleet))
        gameLogic.setShipsToBoard(game, player1, fleet)
        gameLogic.setShipsToBoard(game, player2, fleet)
        gameLogic.setShipsToBoard(game2, player2, fleet)
        gameLogic.setShipsToBoard(game2, player1, fleet)

        // then: create game and store database generated id
        val gameGeneratedId = gameRepo.create(game)
        val gameGeneratedId2 = gameRepo.create(game2)

        // then: update gameId with generated value from database
        if (gameGeneratedId != null) {
            game.id = gameGeneratedId.mapToMap().one()["id"] as Int
        }
        // and: retrieving the game
        var retrievedGame = game.id?.let { gameRepo.getGameById(it) }
        assertNotNull(retrievedGame)

        // then: update gameId with generated value from database
        if (gameGeneratedId2 != null) {
            game2.id = gameGeneratedId2.mapToMap().one()["id"] as Int
        }
        // and: retrieving the game
        var retrievedGame2 = game2.id?.let { gameRepo.getGameById(it) }
        assertNotNull(retrievedGame2)
        // check game id is not the same
        assertNotEquals(retrievedGame, retrievedGame2)

        //check history of games
        val games: List<Game> = gameRepo.getUserGameHistory(player1)
        games.forEach() {
            //"Tom player" is player 1 in first game and player 2 in second game
            assertTrue(it.playerOne.username == player1.username || it.playerTwo.username == player1.username)
        }
        //Check the two created games
        assertTrue(games.size == 2)
    }
}