package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.http.model.UserOutputModel

data class GameHistory(
    val gameId:Int,
    val playerOne: UserOutputModel,
    val playerTwo:UserOutputModel,
    val gamePhase: GamePhase,
    val roundNumber: Int,
    val playerWin:UserOutputModel?,
)
