package pt.isel.daw.battleship.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.battleship.Either
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.domain.fleet.*
import pt.isel.daw.battleship.http.model.SetFleetInputModel
import pt.isel.daw.battleship.utils.Sha256TokenEncoder
import pt.isel.daw.battleship.utils.testWithTransactionManagerAndRollback

const val PLAYER1 = "tiagoForRollback"
const val PLAYER1_EMAIL = "blabla@sapo.pt"
const val PLAYER1_PASS = "Ronaldo!123"
const val PLAYER2 = "raulForRollback"
const val PLAYER2_EMAIL = "qqq@sapo.pt"
const val PLAYER2_PASS = "Chuck!123"
const val NOT_EXIST_PLAYER = "Random"

class GameServiceTests {

    @Test
    fun `can create game`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->

            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )

            // given: a game service
            val gameService = GameService(
                transactionManager,
                GameLogic()
            )
            creationOfUsers(userService)

            // then: not creating a game with empty players
            when (val createGameResult = gameService.createNewGame(playerOne = "", playerTwo = "", 5)) {
                is Either.Left -> assertEquals(GameCreationError.EmptyPlayer, createGameResult.value)
                is Either.Right -> fail("Unexpected $createGameResult")
            }

            // then: not creating a game with non-existing players
            when (val createGameResult =
                gameService.createNewGame(playerOne = NOT_EXIST_PLAYER, playerTwo = NOT_EXIST_PLAYER, 5)) {
                is Either.Left -> assertEquals(GameCreationError.InvalidPlayer, createGameResult.value)
                is Either.Right -> fail("Unexpected $createGameResult")
            }

            // then: the creation is success
            when (val createGameResult = gameService.createNewGame(playerOne = PLAYER1, playerTwo = PLAYER2, 5)) {
                is Either.Left -> fail("Unexpected $createGameResult")
                is Either.Right -> assertTrue(createGameResult.value.gameId > 0)
            }
        }

    @Test
    fun `check players game`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->

            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )

            // given: a game service
            val gameService = GameService(
                transactionManager,
                GameLogic()
            )

            // when: the creation is successful
            val playerOneId: Int
            when (val createUserResult = userService.createUser(PLAYER1, PLAYER1_EMAIL, PLAYER1_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerOneId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }

            // when: the creation is successful
            val playerTwoId: Int
            when (val createUserResult = userService.createUser(PLAYER2, PLAYER2_EMAIL, PLAYER2_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerTwoId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }

            // then: the creation is success
            val createdGameId: Int
            when (val createGameResult = gameService.createNewGame(playerOne = PLAYER1, playerTwo = PLAYER2, 5)) {
                is Either.Left -> fail("Unexpected $createGameResult")
                is Either.Right -> {
                    createdGameId = createGameResult.value.gameId
                    assertTrue(createdGameId > 0)
                }
            }

            // then: the check if users of game are correct
            when (val gameObjectResult = gameService.getGameById(createdGameId)) {
                is Either.Left -> fail("Unexpected $gameObjectResult")
                is Either.Right -> {
                    assertEquals(gameObjectResult.value?.playerOne?.id, playerOneId)
                    assertEquals(gameObjectResult.value?.playerTwo?.id, playerTwoId)
                }
            }
        }

    @Test
    fun `set player fleet && check my layout fleet`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->
            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            // given: a game service
            val gameService = GameService(
                transactionManager,
                GameLogic()
            )
            // when: the creation is successful
            val playerOneId: Int
            when (val createUserResult = userService.createUser(PLAYER1, PLAYER1_EMAIL, PLAYER1_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerOneId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }
            // when: the creation is successful
            when (val createUserResult = userService.createUser(PLAYER2, PLAYER2_EMAIL, PLAYER2_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    assertTrue(createUserResult.value > 0)
                }
            }
            // then: the creation is success
            val createdGameId: Int
            when (val createGameResult = gameService.createNewGame(playerOne = PLAYER1, playerTwo = PLAYER2, 5)) {
                is Either.Left -> fail("Unexpected $createGameResult")
                is Either.Right -> {
                    createdGameId = createGameResult.value.gameId
                    assertTrue(createdGameId > 0)
                }
            }
            //then set fleet for playerOne
            val successFleet: Boolean
            val gson = Gson()

            //Use gson lib
            val jsonFleetShips =
                """[{"shipType":"CARRIER","shipLayout":"UP","referencePoint":{"row":0,"col":0}},
                    {"shipType":"BATTLESHIP","shipLayout":"UP","referencePoint":{"row":0,"col":1}},
                    {"shipType":"SUBMARINE","shipLayout":"DOWN","referencePoint":{"row":4,"col":7}},
                    {"shipType":"CRUISER","shipLayout":"LEFT","referencePoint":{"row":8,"col":1}},
                    {"shipType":"DESTROYER","shipLayout":"RIGHT","referencePoint":{"row":9,"col":6}}]"""
            val shipArrayType = object : TypeToken<Array<SetFleetInputModel>>() {}.type
            val shipsInputArray: Array<SetFleetInputModel> = gson.fromJson(jsonFleetShips, shipArrayType)

            var carrier: Ship? = null
            var battleship: Ship? = null
            var submarine: Ship? = null
            var cruiser: Ship? = null
            var destroyer: Ship? = null

            shipsInputArray.forEach {
                when (it.shipType) {
                    "CARRIER" -> {
                        carrier = Ship(
                            ShipType.CARRIER,
                            ShipLayout(it.shipLayout),
                            it.referencePoint
                        )
                    }

                    "BATTLESHIP" -> {
                        battleship = Ship(
                            ShipType.BATTLESHIP,
                            ShipLayout(it.shipLayout),
                            it.referencePoint
                        )
                    }

                    "SUBMARINE" -> {
                        submarine = Ship(
                            ShipType.SUBMARINE,
                            ShipLayout(it.shipLayout),
                            it.referencePoint
                        )
                    }

                    "CRUISER" -> {
                        cruiser = Ship(
                            ShipType.CRUISER,
                            ShipLayout(it.shipLayout),
                            it.referencePoint
                        )
                    }

                    "DESTROYER" -> {
                        destroyer = Ship(
                            ShipType.DESTROYER,
                            ShipLayout(it.shipLayout),
                            it.referencePoint
                        )
                    }
                }
            }

            val fleet = Fleet(carrier!!, battleship!!, submarine!!, cruiser!!, destroyer!!)

            when (val setFleetResult = gameService.setPlayerFleet(playerOneId, createdGameId, fleet)) {
                is Either.Left -> fail("Unexpected $setFleetResult")
                is Either.Right -> {
                    successFleet = setFleetResult.value
                    assertTrue(successFleet)
                }
            }

            // then: check if board updated correctly in database
            when (val gameObjectResult = gameService.getGameById(createdGameId)) {
                is Either.Left -> fail("Unexpected $gameObjectResult")
                is Either.Right -> {
                    assertEquals(
                        gameObjectResult.value?.boardPlayerOne?.getPositionStatus(Position(0, 0))?.shipType,
                        ShipType.CARRIER
                    )
                }
            }

            // then: check if board updated correctly in database
            when (val boardObjectResult = gameService.getUserBoardState(playerOneId, createdGameId, true)) {
                is Either.Left -> fail("Unexpected $boardObjectResult")
                is Either.Right -> {
                    val p: Array<Array<PositionStateBoard>> = boardObjectResult.value
                    val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                    val jsonBoard: String = gsonPretty.toJson(p)
                    val stringToCompare = "{\n" +
                            "      \"boardPosition\": {\n" +
                            "        \"row\": 8,\n" +
                            "        \"col\": 1\n" +
                            "      },\n" +
                            "      \"wasShoot\": false,\n" +
                            "      \"wasShip\": true,\n" +
                            "      \"shipType\": \"CRUISER\",\n" +
                            "      \"shipLayout\": \"LEFT\"\n" +
                            "    },"
                    assertTrue(jsonBoard.contains(stringToCompare))
                }
            }
        }

    @Test
    fun `set playerOne and playerTwo fleet && check shoot && opponent fleet`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->
            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            // given: a game service
            val gameService = GameService(
                transactionManager,
                GameLogic()
            )
            // when: the creation is successful
            val playerOneId: Int
            when (val createUserResult = userService.createUser(PLAYER1, PLAYER1_EMAIL, PLAYER1_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerOneId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }
            // when: the creation is successful
            val playerTwoId: Int
            when (val createUserResult = userService.createUser(PLAYER2, PLAYER2_EMAIL, PLAYER2_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerTwoId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }
            // then: the creation is success
            val createdGameId: Int
            when (val createGameResult = gameService.createNewGame(playerOne = PLAYER1, playerTwo = PLAYER2, 5)) {
                is Either.Left -> fail("Unexpected $createGameResult")
                is Either.Right -> {
                    createdGameId = createGameResult.value.gameId
                    assertTrue(createdGameId > 0)
                }
            }
            //then set fleet for playerOne && playerTwo
            val carrier = Ship(ShipType.CARRIER, ShipLayout(ShipLayout.UP), Position(0, 0))
            val battleship = Ship(ShipType.BATTLESHIP, ShipLayout(ShipLayout.UP), Position(0, 1))
            val submarine = Ship(ShipType.SUBMARINE, ShipLayout(ShipLayout.DOWN), Position(4, 7))
            val cruiser = Ship(ShipType.CRUISER, ShipLayout(ShipLayout.LEFT), Position(8, 1))
            val destroyer = Ship(ShipType.DESTROYER, ShipLayout(ShipLayout.RIGHT), Position(9, 6))
            val fleet = Fleet(carrier, battleship, submarine, cruiser, destroyer)

            when (val setFleetResult = gameService.setPlayerFleet(playerOneId, createdGameId, fleet)) {
                is Either.Left -> fail("Unexpected $setFleetResult")
                is Either.Right -> {
                    val successFleet = setFleetResult.value
                    assertTrue(successFleet)
                }
            }
            when (val setFleetResult = gameService.setPlayerFleet(playerTwoId, createdGameId, fleet)) {
                is Either.Left -> fail("Unexpected $setFleetResult")
                is Either.Right -> {
                    val successFleet = setFleetResult.value
                    assertTrue(successFleet)
                }
            }
            //then startGame
            gameService.startGameIfFleetReady(createdGameId)

            //Then shoot fleet playerTwo
            when (val shootResult = gameService.shootOpponentFleet(playerOneId, createdGameId, Position(0, 0))) {
                is Either.Left -> fail("Unexpected $shootResult")
                is Either.Right -> {
                    val shootFleet = shootResult.value
                    assertTrue(shootFleet)
                }
            }

            //Then shoot fleet playerTwo take 2
            when (val shootResult = gameService.shootOpponentFleet(playerOneId, createdGameId, Position(9, 5))) {
                is Either.Left -> fail("Unexpected $shootResult")
                is Either.Right -> {
                    val shootFleet = shootResult.value
                    assertTrue(shootFleet)
                }
            }

            //Then shoot fleet playerTwo take 3
            when (val shootResult = gameService.shootOpponentFleet(playerOneId, createdGameId, Position(9, 6))) {
                is Either.Left -> fail("Unexpected $shootResult")
                is Either.Right -> {
                    val shootFleet = shootResult.value
                    assertTrue(shootFleet)
                }
            }

            //then check shot position state
            when (val checkPos = gameService.getStatePosition(playerOneId, createdGameId, Position(0, 0), false)) {
                is Either.Left -> fail("Unexpected $checkPos")
                is Either.Right -> {
                    val checkResult = checkPos.value.wasShoot
                    assertTrue(checkResult)
                }
            }
            //check current gamePhase
            when (val currentPhase = gameService.getCurrentGamePhase(createdGameId)) {
                is Either.Left -> fail("Unexpected $currentPhase")
                is Either.Right -> {
                    val phase = currentPhase.value
                    assertEquals(Game.Phase.SHOOTING_PLAYER_ONE.phase, phase)
                }
            }
            //Check game is not ended
            assertFalse(gameService.gameIsEnd(createdGameId))

            // then: check opponent board status
            when (val boardObjectResult = gameService.getUserBoardState(playerTwoId, createdGameId, false)) {
                is Either.Left -> fail("Unexpected $boardObjectResult")
                is Either.Right -> {
                    val p: Array<Array<PositionStateBoard>> = boardObjectResult.value
                    val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                    val jsonBoard: String = gsonPretty.toJson(p)
                    val stringToCompare = "{\n" +
                            "      \"boardPosition\": {\n" +
                            "        \"row\": 0,\n" +
                            "        \"col\": 0\n" +
                            "      },\n" +
                            "      \"wasShoot\": true,\n" +
                            "      \"wasShip\": true\n" +
                            "    }"
                    assertTrue(jsonBoard.contains(stringToCompare))
                    // if boat is sunk, playerOne can check all specs
                    val stringToCompare2 = "{\n" +
                            "      \"boardPosition\": {\n" +
                            "        \"row\": 9,\n" +
                            "        \"col\": 5\n" +
                            "      },\n" +
                            "      \"wasShoot\": true,\n" +
                            "      \"wasShip\": true,\n" +
                            "      \"shipType\": \"DESTROYER\",\n" +
                            "      \"shipLayout\": \"RIGHT\"\n" +
                            "    }"
                    assertTrue(jsonBoard.contains(stringToCompare2))
                }
            }

            //player one check fleet opponent status specs
            when (val fleetStatusObjectResult = gameService.getStateFleet(playerOneId, createdGameId, false)) {
                is Either.Left -> fail("Unexpected $fleetStatusObjectResult")
                is Either.Right -> {
                    assertTrue(fleetStatusObjectResult.value.numberShipsHit == 2)
                    assertTrue(fleetStatusObjectResult.value.numberShipsSunk == 1)
                }
            }
        }

    @Test
    fun `check players lobby (wants to play)`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->
            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            // given: a game service
            val gameService = GameService(
                transactionManager,
                GameLogic()
            )
            // when: the creation is successful
            val playerOneId: Int
            when (val createUserResult = userService.createUser(PLAYER1, PLAYER1_EMAIL, PLAYER1_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerOneId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }
            // when: the creation is successful
            val playerTwoId: Int
            when (val createUserResult = userService.createUser(PLAYER2, PLAYER2_EMAIL, PLAYER2_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerTwoId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }
            // then validate lobby
            val userP1 = userService.getUserById(playerOneId)
            val userP2 = userService.getUserById(playerTwoId)
            assertEquals(playerOneId, userP1?.id)
            assertEquals(playerTwoId, userP2?.id)

            var createdGameId = -1
            if (userP1 != null && userP2 != null) {
                when (val result1 = gameService.playerIntendsToPlay(userP1, 99)) {
                    is Either.Left -> assertEquals(result1.value, GameCreationError.SetInLobby)
                    is Either.Right -> fail("Unexpected result1")
                    null -> fail("Unexpected result1")
                }
                when (val result2 = gameService.playerIntendsToPlay(userP1, 99)) {
                    is Either.Left -> assertEquals(result2.value, GameCreationError.AlreadyInLobby)
                    is Either.Right -> fail("Unexpected result2")
                    null -> fail("Unexpected result2")
                }
                when (val result3 = gameService.playerIntendsToPlay(userP2, 99)) {
                    is Either.Left -> fail("Unexpected $result3")
                    is Either.Right -> {
                        createdGameId = result3.value.gameId
                        assertTrue(createdGameId > 0)
                    }

                    null -> fail("Unexpected: game not created")
                }
            }
            // then: the check if users of game are correct
            when (val gameObjectResult = gameService.getGameById(createdGameId)) {
                is Either.Left -> fail("Unexpected $gameObjectResult")
                is Either.Right -> {
                    assertEquals(gameObjectResult.value?.playerOne?.id, playerOneId)
                    assertEquals(gameObjectResult.value?.playerTwo?.id, playerTwoId)
                }
            }
        }

    @Test
    fun `can create game and check user game history`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->

            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )

            // given: a game service
            val gameService = GameService(
                transactionManager,
                GameLogic()
            )

            creationOfUsers(userService)

            // then: the creation is success
            when (val createGameResult = gameService.createNewGame(playerOne = PLAYER1, playerTwo = PLAYER2, 5)) {
                is Either.Left -> fail("Unexpected $createGameResult")
                is Either.Right -> assertTrue(createGameResult.value.gameId > 0)
            }
            // get user by username
            var userP1: User? = null
            when (val userObjectResult = userService.getUserIdByUsername(PLAYER1)) {
                is Either.Left -> fail("Unexpected $userObjectResult")
                is Either.Right -> {
                    userP1 = userService.getUserById(userObjectResult.value)
                }
            }
            // check user is not null
            assertNotNull(userP1)
            if (userP1 != null) {
                val size = gameService.getUserGamesHistory(userP1)?.size
                assertNotNull(size)
                if (size != null) {
                    assertTrue(size > 0)
                }
            }
        }

    @Test
    fun `turn round test`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->
            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            // given: a game service
            val gameService = GameService(
                transactionManager,
                GameLogic()
            )
            // when: the creation is successful
            val playerOneId: Int
            when (val createUserResult = userService.createUser(PLAYER1, PLAYER1_EMAIL, PLAYER1_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerOneId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }
            // when: the creation is successful
            val playerTwoId: Int
            when (val createUserResult = userService.createUser(PLAYER2, PLAYER2_EMAIL, PLAYER2_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerTwoId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }
            // then: the creation is success
            val createdGameId: Int
            when (val createGameResult = gameService.createNewGame(playerOne = PLAYER1, playerTwo = PLAYER2, 1)) {
                is Either.Left -> fail("Unexpected $createGameResult")
                is Either.Right -> {
                    createdGameId = createGameResult.value.gameId
                    assertTrue(createdGameId > 0)
                }
            }
            //then set fleet for playerOne && playerTwo
            val carrier = Ship(ShipType.CARRIER, ShipLayout(ShipLayout.UP), Position(0, 0))
            val battleship = Ship(ShipType.BATTLESHIP, ShipLayout(ShipLayout.UP), Position(0, 1))
            val submarine = Ship(ShipType.SUBMARINE, ShipLayout(ShipLayout.DOWN), Position(4, 7))
            val cruiser = Ship(ShipType.CRUISER, ShipLayout(ShipLayout.LEFT), Position(8, 1))
            val destroyer = Ship(ShipType.DESTROYER, ShipLayout(ShipLayout.RIGHT), Position(9, 6))
            val fleet = Fleet(carrier, battleship, submarine, cruiser, destroyer)

            when (val setFleetResult = gameService.setPlayerFleet(playerOneId, createdGameId, fleet)) {
                is Either.Left -> fail("Unexpected $setFleetResult")
                is Either.Right -> {
                    val successFleet = setFleetResult.value
                    assertTrue(successFleet)
                }
            }
            when (val setFleetResult = gameService.setPlayerFleet(playerTwoId, createdGameId, fleet)) {
                is Either.Left -> fail("Unexpected $setFleetResult")
                is Either.Right -> {
                    val successFleet = setFleetResult.value
                    assertTrue(successFleet)
                }
            }
            //then startGame
            gameService.startGameIfFleetReady(createdGameId)

            //Do the shoot, p1!
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(0, 0))

            // check game round turn
            when (val phaseResult = gameService.getCurrentGamePhase(createdGameId)) {
                is Either.Left -> fail("Unexpected $phaseResult")
                is Either.Right -> {
                    val successPhase = phaseResult.value
                    assertEquals(successPhase, Game.Phase.SHOOTING_PLAYER_TWO.phase)
                }
            }

            //Do the shoot, p2!
            gameService.shootOpponentFleet(playerTwoId, createdGameId, Position(0, 0))

            // check game round turn again
            when (val phaseResult = gameService.getCurrentGamePhase(createdGameId)) {
                is Either.Left -> fail("Unexpected $phaseResult")
                is Either.Right -> {
                    val successPhase = phaseResult.value
                    assertEquals(successPhase, Game.Phase.SHOOTING_PLAYER_ONE.phase)
                }
            }
        }

    @Test
    fun `win game and check rank`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->
            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            // given: a game service
            val gameService = GameService(
                transactionManager,
                GameLogic()
            )
            // when: the creation is successful
            val playerOneId: Int
            when (val createUserResult = userService.createUser(PLAYER1, PLAYER1_EMAIL, PLAYER1_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerOneId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }
            // when: the creation is successful
            val playerTwoId: Int
            when (val createUserResult = userService.createUser(PLAYER2, PLAYER2_EMAIL, PLAYER2_PASS)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> {
                    playerTwoId = createUserResult.value
                    assertTrue(createUserResult.value > 0)
                }
            }
            // then: the creation is success
            val createdGameId: Int
            when (val createGameResult = gameService.createNewGame(playerOne = PLAYER1, playerTwo = PLAYER2, 200)) {
                is Either.Left -> fail("Unexpected $createGameResult")
                is Either.Right -> {
                    createdGameId = createGameResult.value.gameId
                    assertTrue(createdGameId > 0)
                }
            }
            //then set fleet for playerOne && playerTwo
            val carrier = Ship(ShipType.CARRIER, ShipLayout(ShipLayout.UP), Position(0, 0))
            val battleship = Ship(ShipType.BATTLESHIP, ShipLayout(ShipLayout.UP), Position(0, 1))
            val submarine = Ship(ShipType.SUBMARINE, ShipLayout(ShipLayout.DOWN), Position(4, 7))
            val cruiser = Ship(ShipType.CRUISER, ShipLayout(ShipLayout.LEFT), Position(8, 1))
            val destroyer = Ship(ShipType.DESTROYER, ShipLayout(ShipLayout.RIGHT), Position(9, 6))
            val fleet = Fleet(carrier, battleship, submarine, cruiser, destroyer)

            when (val setFleetResult = gameService.setPlayerFleet(playerOneId, createdGameId, fleet)) {
                is Either.Left -> fail("Unexpected $setFleetResult")
                is Either.Right -> {
                    val successFleet = setFleetResult.value
                    assertTrue(successFleet)
                }
            }
            when (val setFleetResult = gameService.setPlayerFleet(playerTwoId, createdGameId, fleet)) {
                is Either.Left -> fail("Unexpected $setFleetResult")
                is Either.Right -> {
                    val successFleet = setFleetResult.value
                    assertTrue(successFleet)
                }
            }
            //then startGame
            gameService.startGameIfFleetReady(createdGameId)

            //Do the shoot, sunk all without mercy!
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(0, 0))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(1, 0))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(2, 0))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(3, 0))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(4, 0))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(0, 1))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(1, 1))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(2, 1))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(3, 1))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(2, 7))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(3, 7))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(4, 7))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(8, 1))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(8, 2))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(8, 3))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(9, 5))
            gameService.shootOpponentFleet(playerOneId, createdGameId, Position(9, 6))

            // check game is over player one wins
            when (val phaseResult = gameService.getCurrentGamePhase(createdGameId)) {
                is Either.Left -> fail("Unexpected $phaseResult")
                is Either.Right -> {
                    val successPhase = phaseResult.value
                    assertEquals(successPhase, Game.Phase.PLAYER_ONE_WON.phase)
                }
            }

            //check user rank
            val ranks = gameService.getUsersGamesRankList()
            ranks.forEach() {
                if (it.user.id == playerOneId) {
                    assertTrue(it.playedGames == 1)
                    assertTrue(it.winGames == 1)
                    assertTrue(it.rankPoints == 100)
                    assertTrue(it.lostGames == 0)
                } else if (it.user.id == playerTwoId) {
                    assertTrue(it.playedGames == 1)
                    assertTrue(it.winGames == 0)
                    assertTrue(it.rankPoints == 50)
                    assertTrue(it.lostGames == 1)
                }
            }
        }

    //Aux for some tests
    private fun creationOfUsers(userService: UsersService) {
        // when: the creation is successful
        when (val createUserResult = userService.createUser(PLAYER1, PLAYER1_EMAIL, PLAYER1_PASS)) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value > 0)
        }

        // when: the creation is successful
        when (val createUserResult = userService.createUser(PLAYER2, PLAYER2_EMAIL, PLAYER2_PASS)) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value > 0)
        }

        // when: the creation is successful
        when (val loginUserResult = userService.loginUser(PLAYER1, PLAYER1_PASS)) {
            is Either.Left -> Assertions.fail(loginUserResult.toString())
            is Either.Right -> loginUserResult.value
        }

        // when: the creation is successful
        when (val loginUserResult = userService.loginUser(PLAYER2, PLAYER2_PASS)) {
            is Either.Left -> Assertions.fail(loginUserResult.toString())
            is Either.Right -> loginUserResult.value
        }
    }
}