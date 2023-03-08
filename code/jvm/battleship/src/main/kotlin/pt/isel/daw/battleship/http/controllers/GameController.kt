package pt.isel.daw.battleship.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.Either
import pt.isel.daw.battleship.domain.Game
import pt.isel.daw.battleship.domain.User
import pt.isel.daw.battleship.domain.fleet.*
import pt.isel.daw.battleship.http.Relations
import pt.isel.daw.battleship.http.Uris
import pt.isel.daw.battleship.http.model.*
import pt.isel.daw.battleship.infra.siren
import pt.isel.daw.battleship.service.*

@RestController
class GameController(
    private val gameService: GameService
) {

    companion object {
        const val mediaTypeJsonSiren: String = "application/vnd.siren+json"
        const val mediaTypeJson: String = "application/json"
    }

    // get games history list @user
    @GetMapping(Uris.Game.LIST_GAME_HISTORY)
    fun getGameHistory(user: User): ResponseEntity<*> {
        val ls = gameService.getUserGamesHistory(user)
        return ResponseEntity.status(200)
            .header("Content-Type", UserController.mediaTypeJsonSiren)
            .body(siren(ls) {
                clazz("gameHistory")
                properties
                link(Uris.Game.getGameHistory(), Relations.SELF)
            })
    }

    //allow a user to express their desire to start a new game;
    //users will enter a waiting lobby, where a matchmaking;
    //algorithm will select pairs of users and start games with them;
    //check lobby first if user already wait and if are availableUser
    @PostMapping(Uris.Game.NEW_GAME)
    fun newGame(@RequestBody input: GameInputModel, user: User): ResponseEntity<*> {
        val ls = DefaultAnswerModel()
        return when (val res = gameService.playerIntendsToPlay(user, input.maxShots)) {
            is Either.Right -> {
                val gameResult = GameOutputModel(res.value.gameId)
                ResponseEntity.status(201)
                    .header("Content-Type", UserController.mediaTypeJsonSiren)
                    .header(
                        UserController.LOCATION_HEADER,
                        Uris.Game.getGameById(res.value.gameId).toASCIIString()
                    )
                    .body(siren(gameResult) {
                        clazz("newGame")
                        properties
                        link(Uris.Game.createGame(), Relations.SELF)
                    })
            }

            is Either.Left -> when (res.value) {
                GameCreationError.InvalidPlayer -> {
                    ls.error = ProblemOutputModel.invalidUser
                    ProblemOutputModel.response(420, ls.error!!)
                }

                GameCreationError.EmptyPlayer -> {
                    ls.error = ProblemOutputModel.invalidUser
                    ProblemOutputModel.response(420, ls.error!!)
                }

                GameCreationError.AlreadyInLobby -> {
                    ls.error = ProblemOutputModel.userAlreadyInLobby
                    ProblemOutputModel.response(420, ls.error!!)
                }

                GameCreationError.InvalidRule -> {
                    ls.error = ProblemOutputModel.invalidRule
                    ProblemOutputModel.response(420, ls.error!!)
                }

                GameCreationError.SetInLobby -> {
                    ls.error = ProblemOutputModel.userInLobby
                    ProblemOutputModel.response(420, ls.error!!)
                }

                GameCreationError.ErrorCreatingGame -> {
                    ls.error = ProblemOutputModel.internalError
                    ProblemOutputModel.response(500, ls.error!!)
                }

                GameCreationError.AlreadyInActiveGame -> {
                    ls.error = ProblemOutputModel.alreadyInActiveGame
                    ProblemOutputModel.response(406, ls.error!!)
                }
            }

            null -> {
                ls.error = ProblemOutputModel.internalError
                ProblemOutputModel.response(500, ls.error!!)
            }
        }
    }

    // set @user layout fleet
    @PostMapping(Uris.Game.SET_LAYOUT_FLEET)
    fun setLayoutFleet(
        @PathVariable gameId: Int,
        @RequestBody fleet: List<SetFleetInputModel>, user: User
    ): ResponseEntity<*> {
        val ls = DefaultAnswerModel()
        return when (val res = gameService.setPlayerFleet(user.id, gameId, auxBuildFleet(fleet))) {
            is Either.Right -> {
                //if two fleet ready, then start game
                gameService.startGameIfFleetReady(gameId)
                ResponseEntity.status(200)
                    .header("Content-Type", mediaTypeJsonSiren)
                    .body(siren("Fleet successfully set for user Id ${user.id}.") {
                        clazz("setFleet")
                        properties
                        link(Uris.Game.setGameLayoutFleet(gameId), Relations.SELF)
                    })
            }

            is Either.Left -> when (res.value) {
                FleetCreationError.InvalidGame -> {
                    ls.error = ProblemOutputModel.invalidGame
                    ProblemOutputModel.response(420, ls.error!!)
                }

                FleetCreationError.InvalidFleet -> {
                    ls.error = ProblemOutputModel.invalidFleet
                    ProblemOutputModel.response(420, ls.error!!)
                }

                FleetCreationError.InvalidUser -> {
                    ls.error = ProblemOutputModel.invalidUser
                    ProblemOutputModel.response(420, ls.error!!)
                }
            }
        }
    }

    // get current gamePhase @gameId
    @GetMapping(Uris.Game.GET_GAME_PHASE)
    fun getGamePhase(@PathVariable gameId: Int, user: User): ResponseEntity<*> {
        val ls = DefaultAnswerModel()
        return when (val res = gameService.getCurrentGamePhase(gameId)) {
            is Either.Right -> {
                ResponseEntity.status(200)
                    .header("Content-Type", mediaTypeJsonSiren)
                    .body(siren(res.value) {
                        clazz("getCurrentGamePhase")
                        properties
                        link(Uris.Game.getCurrentGamePhase(gameId), Relations.SELF)
                    })
            }

            is Either.Left -> when (res.value) {
                GamePhaseError.InvalidGame -> {
                    ls.error = ProblemOutputModel.invalidGame
                    ProblemOutputModel.response(420, ls.error!!)
                }
            }
        }
    }


    // shoot opponent fleet
    @PostMapping(Uris.Game.SET_SHOOT)
    fun setShoot(@PathVariable gameId: Int, @RequestBody pos: Position, user: User): ResponseEntity<*> {
        val ls = DefaultAnswerModel()
        return when (val res = gameService.shootOpponentFleet(user.id, gameId, pos)) {
            is Either.Right -> {
                ls.message = "Successfully shoot position [${pos.row},${pos.col}]";
                ResponseEntity.status(200)
                    .header("Content-Type", mediaTypeJsonSiren)
                    .body(siren(ls) {
                        clazz("setShoot")
                        properties
                        link(Uris.Game.setShots(gameId), Relations.SELF)
                    })
            }

            is Either.Left -> when (res.value) {
                ShootBoardError.InvalidGame -> {
                    ls.error = ProblemOutputModel.invalidGame
                    ProblemOutputModel.response(420, ls.error!!)
                }

                ShootBoardError.InvalidShoot -> {
                    ls.error = ProblemOutputModel.invalidShot
                    ProblemOutputModel.response(420, ls.error!!)
                }

                ShootBoardError.InvalidUser -> {
                    ls.error = ProblemOutputModel.invalidUser
                    ProblemOutputModel.response(420, ls.error!!)
                }
            }
        }
    }

    // get personal user board @myBoard=true or opponent board @myBoard=false
    @PostMapping(Uris.Game.GET_FLEET)
    fun getFleet(@RequestBody valBoard: MyBoardModel, @PathVariable gameId: Int, user: User): ResponseEntity<*> {
        val ls = DefaultAnswerModel()
        val game: Game?
        var userBoardId = user.id

        // if user want's check opponent board
        if (!valBoard.myBoard) {
            when (val gameObj = gameService.getGameById(gameId)) {
                is Either.Right -> {
                    game = gameObj.value
                    userBoardId = if (game!!.playerOne == user) {
                        game.playerTwo.id
                    } else {
                        game.playerOne.id
                    }
                }

                else -> {
                    ls.error = ProblemOutputModel.emptyBoard
                    return ProblemOutputModel.response(420, ls.error!!)
                }
            }
        }
        return when (val res = gameService.getUserBoardState(userBoardId, gameId, valBoard.myBoard)) {
            is Either.Right -> {
                ResponseEntity.status(200)
                    .header("Content-Type", mediaTypeJsonSiren)
                    .body(siren(res.value) {
                        clazz("getFleet")
                        properties
                        link(Uris.Game.getGameFleet(gameId), Relations.SELF)
                    })
            }

            is Either.Left -> when (res.value) {
                CheckUserBoardError.EmptyBoard -> {
                    ls.error = ProblemOutputModel.emptyBoard
                    ProblemOutputModel.response(420, ls.error!!)
                }

                CheckUserBoardError.InvalidBoard -> {
                    ls.error = ProblemOutputModel.invalidBoard
                    ProblemOutputModel.response(420, ls.error!!)
                }

                CheckUserBoardError.InvalidGame -> {
                    ls.error = ProblemOutputModel.invalidGame
                    ProblemOutputModel.response(420, ls.error!!)
                }

                CheckUserBoardError.InvalidUser -> {
                    ls.error = ProblemOutputModel.invalidUser
                    ProblemOutputModel.response(420, ls.error!!)
                }
            }
        }
    }

    // get game raking overall
    @GetMapping(Uris.RANKING)
    fun getRanking(): ResponseEntity<*> {
        val ls = DefaultAnswerModel()
        val rankList = gameService.getUsersGamesRankList()

        return if (rankList.size == 0) {
            ls.error = ProblemOutputModel.emptyRanking
            ProblemOutputModel.response(420, ls.error!!)
        } else {
            ResponseEntity.status(200)
                .header("Content-Type", mediaTypeJsonSiren)
                .body(siren(rankList) {
                    clazz("getRanking")
                    properties
                    link(Uris.getRanking(), Relations.SELF)
                })
        }
    }

    // lobby (give up)
    // remove user from lobby
    @PostMapping(Uris.Game.GIVE_UP_LOBBY)
    fun giveUpLobby(user: User): ResponseEntity<*> {
        val ls = DefaultAnswerModel()
        val userInLobby = gameService.userIsAlreadyInLobby(user)
        return if (!userInLobby) {
            ls.error = ProblemOutputModel.userNotExistsLobby
            ProblemOutputModel.response(420, ls.error!!)
        } else {
            gameService.removeUserFromLobby(user)
            ls.message = "User removed from lobby!"
            ResponseEntity.status(200)
                .header("Content-Type", UserController.mediaTypeJson)
                .body(ls)
        }
    }

    // give up game by user's wish
    @PostMapping(Uris.Game.GIVE_UP_GAME)
    fun giveUpGame(user: User): ResponseEntity<*> {
        val ls = DefaultAnswerModel()
        val giveUpSuccess = gameService.giveUpCurrentGame(user)
        return if (!giveUpSuccess) {
            ls.error = ProblemOutputModel.playerCantGiveUp
            ProblemOutputModel.response(420, ls.error!!)
        } else {
            gameService.removeUserFromLobby(user)
            ls.message = "You give up game! The other player was declared winner!"
            ResponseEntity.status(200)
                .header("Content-Type", UserController.mediaTypeJson)
                .body(ls)
        }
    }

    // get current gameId from @user
    @GetMapping(Uris.Game.GET_CURRENT_USER_GAME_ID)
    fun getUserCurrentGameId(user: User): ResponseEntity<*> {
        val ls = DefaultAnswerModel()
        val gameId = gameService.getUserCurrentGameId(user)

        return if (gameId == 0) {
            ls.error = ProblemOutputModel.userNotPlayingGame
            ProblemOutputModel.response(404, ls.error!!)
        } else {
            val gameIdOutput = GameOutputModel(gameId!!)

            ResponseEntity.status(200)
                .header("Content-Type", mediaTypeJsonSiren)
                .body(siren(gameIdOutput) {
                    clazz("getUserCurrentGameId")
                    properties
                    link(Uris.Game.getCurrentUserGameId(), Relations.SELF)
                })
        }
    }

    // aux to build ship fleet
    private fun auxBuildFleet(fleet: List<SetFleetInputModel>): Fleet {
        var carrier: Ship? = null
        var battleship: Ship? = null
        var submarine: Ship? = null
        var cruiser: Ship? = null
        var destroyer: Ship? = null

        fleet.forEach {
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
        return Fleet(carrier!!, battleship!!, submarine!!, cruiser!!, destroyer!!)
    }
}