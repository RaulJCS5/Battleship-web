package pt.isel.daw.battleship.service

import org.springframework.stereotype.Component
import pt.isel.daw.battleship.Either
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.domain.fleet.*
import pt.isel.daw.battleship.http.model.GameOutputModel
import pt.isel.daw.battleship.http.model.UserOutputModel
import pt.isel.daw.battleship.repository.TransactionManager
import pt.isel.daw.battleship.repository.jdbi.models.GameDbModel

sealed class GamePairError {
    object InvalidRule : GamePairError()
}

sealed class GameCreationError {
    object InvalidPlayer : GameCreationError()
    object EmptyPlayer : GameCreationError()
    object ErrorCreatingGame : GameCreationError()
    object AlreadyInLobby : GameCreationError()
    object SetInLobby : GameCreationError()
    object InvalidRule : GameCreationError()
    object AlreadyInActiveGame : GameCreationError()
}

sealed class GamePhaseError {
    object InvalidGame : GamePhaseError()
}

sealed class FleetCreationError {
    object InvalidGame : FleetCreationError()
    object InvalidFleet : FleetCreationError()
    object InvalidUser : FleetCreationError()
}

sealed class CheckUserBoardError {
    object InvalidGame : CheckUserBoardError()
    object InvalidUser : CheckUserBoardError()
    object EmptyBoard : CheckUserBoardError()
    object InvalidBoard : CheckUserBoardError()
}

sealed class ShootBoardError {
    object InvalidGame : ShootBoardError()
    object InvalidShoot : ShootBoardError()
    object InvalidUser : ShootBoardError()
}

sealed class GameObjectError {
    object InvalidGame : GameObjectError()
    object InvalidUser : GameObjectError()
    object GameNotReady : GameObjectError()
    object PositionAccessDeny : GameObjectError()
    object PositionStateError : GameObjectError()
}

typealias GameCreationResult = Either<GameCreationError, GameOutputModel>
typealias GameObjectResult = Either<GameObjectError, Game?>
typealias FleetObjectResult = Either<FleetCreationError, Boolean>
typealias FleetObjectState = Either<CheckUserBoardError, Array<Array<PositionStateBoard>>>
typealias ShootObjectResult = Either<ShootBoardError, Boolean>
typealias StateFleetObjectResult = Either<GameObjectError, StateFleet>
typealias StartGameObjectResult = Either<GameObjectError, Boolean>
typealias PairObjectResult = Either<GamePairError, User?>
typealias PositionFleetObjectState = Either<GameObjectError, PositionStateBoard>
typealias GamePhaseObject = Either<GamePhaseError, GamePhase>

@Component
class GameService(
    private val transactionManager: TransactionManager,
    private val gameLogic: GameLogic,
) {

    //create game given two player username @playerOne @playerTwo, rulePair @maxShoots and maxTimePerRound @maxTimeRound
    //returns game id if successful, or error instead
    fun createNewGame(playerOne: String, playerTwo: String, maxShoots: Int): GameCreationResult {
        if (playerOne.isBlank() || playerTwo.isBlank()) {
            return Either.Left(GameCreationError.EmptyPlayer)
        }
        if (maxShoots == 0) {
            return Either.Left(GameCreationError.InvalidRule)
        }
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val usersRepository = it.usersRepository
            if (!usersRepository.isUserStoredByUsername(playerOne) || !usersRepository.isUserStoredByUsername(playerTwo)) {
                Either.Left(GameCreationError.InvalidPlayer)
            } else {
                val p1 = usersRepository.getUserByUsername(playerOne)
                    ?: return@run Either.Left(GameCreationError.InvalidPlayer)
                val p2 = usersRepository.getUserByUsername(playerTwo)
                    ?: return@run Either.Left(GameCreationError.InvalidPlayer)
                val game = gameLogic.createNewGame(p1, p2, maxShoots, GameConstants.maxTimeRound)
                val gameGeneratedId = gamesRepository.create(game)
                var gameId: Int = -1
                if (gameGeneratedId != null) {
                    gameId = gameGeneratedId.mapToMap().one()[GameDbModel.getId()] as Int
                }
                if (gameId == -1) {
                    Either.Left(GameCreationError.ErrorCreatingGame)
                }
                Either.Right(GameOutputModel(gameId))
            }
        }
    }

    // start game if the two fleet are ready and set in game/db
    fun startGameIfFleetReady(gameId: Int): StartGameObjectResult {

        return transactionManager.run {
            val gameRepository = it.gamesRepository
            val game = gameRepository.getGameById(gameId)
            if (game == null) {
                Either.Left(GameObjectError.InvalidGame)
            } else {
                val startResult = gameLogic.ifLayoutReadyStartGame(game)
                if (startResult) {
                    gameRepository.update(game)
                    Either.Right(true)
                } else {
                    Either.Left(GameObjectError.GameNotReady)
                }
            }
        }
    }

    // Allow a user to define the layout of their fleet in the grid.
    fun setPlayerFleet(playerId: Int, gameId: Int, fleetShips: Fleet): FleetObjectResult {

        var result: FleetObjectResult = Either.Left(FleetCreationError.InvalidFleet)

        transactionManager.run {
            val gameRepository = it.gamesRepository
            val userRepository = it.usersRepository
            val game = gameRepository.getGameById(gameId)
            val user = userRepository.getUserById(playerId)

            result = if (game == null) {
                Either.Left(FleetCreationError.InvalidGame)
            } else if (user == null) {
                Either.Left(FleetCreationError.InvalidUser)
            } else {
                if (gameLogic.setShipsToBoard(game, user, fleetShips)) {
                    //update database
                    gameRepository.update(game)
                    Either.Right(true)
                } else {
                    Either.Left(FleetCreationError.InvalidFleet)
                }
            }
        }
        return result
    }

    // return the @user board/fleet state
    fun getUserBoardState(playerId: Int, gameId: Int, myBoard: Boolean): FleetObjectState {

        return transactionManager.run {
            val gameRepository = it.gamesRepository
            val userRepository = it.usersRepository
            val game = gameRepository.getGameById(gameId)
            val user = userRepository.getUserById(playerId)
            if (game == null) {
                Either.Left(CheckUserBoardError.InvalidGame)
            } else if (user == null) {
                Either.Left(CheckUserBoardError.InvalidUser)
            } else {
                val board = game.getUserBoard(user)
                if (board == null) {
                    Either.Left(CheckUserBoardError.InvalidBoard)
                } else if (board.boardIsEmpty()) {
                    Either.Left(CheckUserBoardError.EmptyBoard)
                } else {
                    if (myBoard) {
                        Either.Right(board.cells)
                    } else {
                        Either.Right(getBoardToOpponent(board))
                    }
                }
            }
        }
    }

    // Aux to hide board specs from Opponent
    private fun getBoardToOpponent(userBoard: Board): Array<Array<PositionStateBoard>> {

        val boardCellsToShow = userBoard.cells
        boardCellsToShow.forEach { b1 ->
            b1.forEach { b2 ->
                if (!b2.wasShoot) {
                    b2.wasShip = null
                    b2.shipLayout = null
                    b2.shipType = null
                } else {
                    val currentPositionShipType = b2.shipType
                    if (currentPositionShipType != null) {
                        val sunk = userBoard.checkShipStatus(currentPositionShipType).sunk
                        if (!sunk) {
                            b2.shipLayout = null
                            b2.shipType = null
                        }
                    }
                }
            }
        }
        return boardCellsToShow
    }

    //Shoots opponent fleet if game already started @check game logic
    fun shootOpponentFleet(playerId: Int, gameId: Int, pos: Position): ShootObjectResult {

        return transactionManager.run {
            val gameRepository = it.gamesRepository
            val userRepository = it.usersRepository
            val game = gameRepository.getGameById(gameId)
            val user = userRepository.getUserById(playerId)
            if (game == null) {
                Either.Left(ShootBoardError.InvalidGame)
            } else if (user == null) {
                Either.Left(ShootBoardError.InvalidUser)
            } else {
                val shootSuccess = gameLogic.doShoot(game, user, pos)
                if (shootSuccess) {
                    //if success set next round (same player if round not ended yet)
                    val nextRoundExists = gameLogic.setNextRoundUser(game)
                    if (!nextRoundExists) {
                        //check if game is ended
                        if (gameLogic.weHaveWinner(game)) {
                            //update rank
                            updateUserRank(game)
                        }
                    }
                    //update game status in db
                    gameRepository.update(game)

                    Either.Right(true)
                } else {
                    Either.Left(ShootBoardError.InvalidShoot)
                }
            }
        }
    }

    //Get state fleet
    //inform the user about the state of fleet: yours or opponent @myBoard
    fun getStateFleet(playerId: Int, gameId: Int, myBoard: Boolean): StateFleetObjectResult {
        return transactionManager.run {
            val gameRepository = it.gamesRepository
            val userRepository = it.usersRepository
            val game = gameRepository.getGameById(gameId)
            val user = userRepository.getUserById(playerId)
            if (game == null) {
                Either.Left(GameObjectError.InvalidGame)
            } else if (user == null) {
                Either.Left(GameObjectError.InvalidUser)
            } else {
                val fleetStatus = game.getFleetStatus(user, myBoard)
                if (fleetStatus != null) {
                    Either.Right(fleetStatus)
                } else {
                    Either.Left(GameObjectError.InvalidUser)
                }
            }
        }
    }

    //inform the user about the state of position@pos Board: yours or opponent @myBoard
    fun getStatePosition(playerId: Int, gameId: Int, pos: Position, myBoard: Boolean): PositionFleetObjectState {
        return transactionManager.run {
            val gameRepository = it.gamesRepository
            val userRepository = it.usersRepository
            val game = gameRepository.getGameById(gameId)
            val user = userRepository.getUserById(playerId)
            if (game == null) {
                Either.Left(GameObjectError.InvalidGame)
            } else if (user == null) {
                Either.Left(GameObjectError.InvalidUser)
            } else {
                val positionStatus = game.getPositionStatus(user, pos, myBoard)
                if (positionStatus == null) {
                    if (!myBoard) {
                        Either.Left(GameObjectError.PositionAccessDeny)
                    } else {
                        Either.Left(GameObjectError.PositionStateError)
                    }
                } else {
                    Either.Right(positionStatus)
                }
            }
        }
    }

    //Inform the user about the overall state of a game, namely:
    //game phase (layout definition phase, shooting phase, completed phase).
    fun getCurrentGamePhase(gameId: Int): GamePhaseObject {
        return transactionManager.run {
            val gameRepository = it.gamesRepository
            val game = gameRepository.getGameById(gameId)
            if (game == null) {
                Either.Left(GamePhaseError.InvalidGame)
            } else {
                Either.Right(game.currentGamePhase())
            }
        }
    }

    //Check if game is over
    fun gameIsEnd(gameId: Int): Boolean {
        return when (val currentPhase = getCurrentGamePhase(gameId)) {
            is Either.Left -> false
            is Either.Right -> currentPhase.value == Game.Phase.PLAYER_ONE_WON.phase ||
                    currentPhase.value == Game.Phase.PLAYER_TWO_WON.phase
        }
    }

    // return object Game with @id
    fun getGameById(id: Int): GameObjectResult {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val game: Game? = gamesRepository.getGameById(id)

            if (game != null) {
                Either.Right(game)
            } else {
                Either.Left(GameObjectError.InvalidGame)
            }
        }
    }

    //TODO update transaction secure (Serializable)
    //Remove user from lobby (give up or pairing)
    fun removeUserFromLobby(user: User) {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.removeUsersFromLobby(user)
        }
    }

    //TODO update transaction secure (Serializable)
    //Check if user is already in lobby
    fun userIsAlreadyInLobby(user: User): Boolean {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.isUserAlreadyInLobby(user)
        }
    }

    //TODO update transaction secure (Serializable)
    //Check if exists player with same rules in waiting lobby
    fun checkLobbyPlayers(rulePair: Int): PairObjectResult {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            if (rulePair < 1) {
                Either.Left(GamePairError.InvalidRule)
            } else {
                val pairUser = gamesRepository.pairLobbyUser(rulePair)
                Either.Right(pairUser)
            }
        }
    }

    //TODO update transaction secure (Serializable)
    //Indicates to the system the intention to play (with rule)
    //If satisfied create a game
    fun playerIntendsToPlay(user: User, rulePair: Int): GameCreationResult? {
        var result: GameCreationResult? = null

        //player cant play two games simultaneously
        if (playerAlreadyInActiveGame(user)) {
            result = Either.Left(GameCreationError.AlreadyInActiveGame)
        } else {
            transactionManager.run {
                val gamesRepository = it.gamesRepository
                when (val res = checkLobbyPlayers(rulePair)) {
                    is Either.Right -> {
                        if (res.value != null && res.value.id != user.id) {
                            result =
                                when (val createGameResult =
                                    createNewGame(
                                        res.value.username,
                                        user.username,
                                        rulePair
                                    )) {
                                    is Either.Right -> {
                                        removeUserFromLobby(user)
                                        removeUserFromLobby(res.value)
                                        createGameResult
                                    }

                                    is Either.Left -> {
                                        createGameResult
                                    }
                                }
                        } else {
                            result = if (!gamesRepository.isUserAlreadyInLobby(user)) {
                                gamesRepository.setUserToWaitingLobby(user, rulePair)
                                Either.Left(GameCreationError.SetInLobby)
                            } else {
                                Either.Left(GameCreationError.AlreadyInLobby)
                            }
                        }
                    }

                    is Either.Left -> {
                        result = Either.Left(GameCreationError.InvalidRule)
                    }
                }
            }
        }
        return result
    }

    // check if player is already in active game
    private fun playerAlreadyInActiveGame(player: User): Boolean {
        var result = false

        transactionManager.run {
            val gamesRepository = it.gamesRepository
            result = gamesRepository.playerAlreadyInActiveGame(player)
        }
        return result
    }

    // returns current user gameId
    fun getUserCurrentGameId(player: User): Int? {
        var result = 0
        if (playerAlreadyInActiveGame(player)) {
            transactionManager.run {
                val gamesRepository = it.gamesRepository
                result = gamesRepository.getPlayerCurrentGameId(player)!!
            }
        }
        return result
    }

    //List user games history
    fun getUserGamesHistory(user: User): MutableList<GameHistory>? {
        val gamesHistoryListResult: MutableList<GameHistory> = ArrayList()

        transactionManager.run {
            val gamesRepository = it.gamesRepository
            val gamesHistory = gamesRepository.getUserGameHistory(user)
            if (gamesHistory.isNotEmpty()) {
                gamesHistory.forEach { itHistory ->
                    var playerWin: UserOutputModel? = null
                    if (itHistory.phase.id == Game.Phase.PLAYER_ONE_WON.phase.id) {
                        playerWin = UserOutputModel(
                            itHistory.playerOne.id,
                            itHistory.playerOne.username,
                            itHistory.playerOne.email
                        )
                    } else if (itHistory.phase.id == Game.Phase.PLAYER_TWO_WON.phase.id) {
                        playerWin = UserOutputModel(
                            itHistory.playerTwo.id,
                            itHistory.playerTwo.username,
                            itHistory.playerTwo.email
                        )
                    }
                    gamesHistoryListResult.add(
                        GameHistory(
                            itHistory.id!!,
                            UserOutputModel(
                                itHistory.playerOne.id,
                                itHistory.playerOne.username,
                                itHistory.playerOne.email
                            ),
                            UserOutputModel(
                                itHistory.playerTwo.id,
                                itHistory.playerTwo.username,
                                itHistory.playerTwo.email
                            ),
                            itHistory.phase,
                            itHistory.roundNumber,
                            playerWin,
                        )
                    )
                }
            }
        }
        return gamesHistoryListResult
    }

    //get list of all users Rank (order first to last place)
    fun getUsersGamesRankList(): MutableList<GameRankTotals> {

        val orderList: MutableList<GameRankTotals> = arrayListOf()

        transactionManager.run {
            val userRepository = it.usersRepository
            val getAllUsersIt = userRepository.getUserRank().iterator()

            while (getAllUsersIt.hasNext()) {
                orderList.add(getAllUsersIt.next().value)
            }
            //order by rank value
            orderList.sortByDescending { rank -> rank.rankPoints }
        }
        return orderList
    }

    //give up game by user's wish
    fun giveUpCurrentGame(player: User): Boolean {
        var result = false

        //first get current user game if exists
        val currentGameId = getUserCurrentGameId(player)
        if (currentGameId != null && currentGameId > 0) {
            when (val game = getGameById(currentGameId)) {
                is Either.Left -> result = false
                is Either.Right -> {
                    var currentGame = game.value
                    if (currentGame!!.playerOne == player) {
                        currentGame.phase = Game.Phase.PLAYER_TWO_WON.phase
                    } else {
                        currentGame.phase = Game.Phase.PLAYER_ONE_WON.phase
                    }
                    //update game in database
                    transactionManager.run {
                        val gamesRepository = it.gamesRepository
                        gamesRepository.update(currentGame)
                    }
                    // at end update user rank
                    updateUserRank(currentGame)
                    //success
                    result = true
                }
            }
        }
        return result
    }

    // aux method: update players rank after game ended
    private fun updateUserRank(game: Game) {

        transactionManager.run {
            val userRepository = it.usersRepository
            val playerOneId = game.playerOne.id
            val playerTwoId = game.playerTwo.id
            val playerOneRank = userRepository.getUserRank()[playerOneId]
            val playerTwoRank = userRepository.getUserRank()[playerTwoId]
            val playedGamesP1 = (playerOneRank?.playedGames ?: 0) + 1
            var winGamesP1 = (playerOneRank?.winGames ?: 0)
            val playedGamesP2 = (playerTwoRank?.playedGames ?: 0) + 1
            var winGamesP2 = (playerTwoRank?.winGames ?: 0)
            //check winner
            if (game.phase == Game.Phase.PLAYER_ONE_WON.phase) {
                winGamesP1++
            } else if (game.phase == Game.Phase.PLAYER_TWO_WON.phase) {
                winGamesP2++
            }
            // calculate rank points
            val rankPointsP1 = kotlin.math.abs((winGamesP1 * 100) - ((playedGamesP1 - winGamesP1) * 50))
            val rankPointsP2 = kotlin.math.abs((winGamesP2 * 100) - ((playedGamesP2 - winGamesP2) * 50))
            // update rank in database
            userRepository.setUserRank(playerOneId, playedGamesP1, winGamesP1, rankPointsP1)
            userRepository.setUserRank(playerTwoId, playedGamesP2, winGamesP2, rankPointsP2)
        }
    }

    class GameConstants {
        companion object {
            //Value in seconds, to set initial fleet and start play
            //Value in seconds, for each game round
            const val maxTimeRound = 180
        }
    }
}