package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.domain.fleet.Position
import pt.isel.daw.battleship.domain.fleet.PositionStateBoard
import pt.isel.daw.battleship.domain.fleet.StateFleet
import java.sql.Timestamp

data class Game(
    var id: Int?,
    var phase: GamePhase,
    val boardPlayerOne: Board,
    val boardPlayerTwo: Board,
    val startDate: Timestamp,
    var updateDate: Timestamp?,
    val playerOne: User,
    val playerTwo: User,
    val maxShootsRule: Int,
    var roundDeadline: Timestamp?,
    var roundNumber: Int,
    var shootsPerRoundCount: Int,
    var activeRoundUser: User,
    val maxTimePerRound: Int
) {
    enum class Phase(val phase: GamePhase) {
        LAYOUT(GamePhase(1, "LAYOUT")),
        SHOOTING_PLAYER_ONE(GamePhase(2, "SHOOTING_PLAYER_ONE")),
        SHOOTING_PLAYER_TWO(GamePhase(3, "SHOOTING_PLAYER_TWO")),
        PLAYER_ONE_WON(GamePhase(4, "PLAYER_ONE_WON")),
        PLAYER_TWO_WON(GamePhase(5, "PLAYER_TWO_WON"))
    }

    //get user board
    fun getUserBoard(user: User): Board? {
        return when (user) {
            playerOne -> boardPlayerOne
            playerTwo -> boardPlayerTwo
            else -> {
                null
            }
        }
    }

    //get status of position @pos in board
    //opponent only can check status if already shoot position
    fun getPositionStatus(user: User, pos: Position, myBoard: Boolean): PositionStateBoard? {
        if (isPlayerOne(user)) {
            if (myBoard) {
                return boardPlayerOne.getPositionStatus(pos)
            } else {
                if (boardPlayerTwo.alreadyShootPosition(pos)) {
                    return boardPlayerTwo.getPositionStatus(pos)
                }
            }
        } else if (isPlayerTwo(user)) {
            if (myBoard) {
                return boardPlayerTwo.getPositionStatus(pos)
            } else {
                if (boardPlayerOne.alreadyShootPosition(pos)) {
                    return boardPlayerOne.getPositionStatus(pos)
                }
            }
        }
        return null
    }

    //get status of fleet in board
    fun getFleetStatus(user: User, myBoard: Boolean): StateFleet? {
        if (isPlayerOne(user)) {
            return if (myBoard) {
                boardPlayerOne.checkFleetStatus()
            } else {
                boardPlayerTwo.checkFleetStatus()
            }
        } else if (isPlayerTwo(user)) {
            return if (myBoard) {
                boardPlayerTwo.checkFleetStatus()
            } else {
                boardPlayerOne.checkFleetStatus()
            }
        }
        return null
    }

    // get active round @User
    fun getGameRoundUser(): User {
        return activeRoundUser
    }

    // get current gamePhase
    fun currentGamePhase(): GamePhase {
        return phase
    }

    // check if @user is playerOne
    private fun isPlayerOne(player: User): Boolean {
        return player == playerOne
    }

    // check if @user is playerTwo
    private fun isPlayerTwo(player: User): Boolean {
        return player == playerTwo
    }
}