package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.http.model.UserOutputModel

data class GameRankTotals(
    /**
     * winGames -> get from database
     * lostGames -> calculated in runtime : playedGames - winGames = lostGames
     * rankPoints -> calculated in runtime  ABS((winGames * 100 ) - (lostGames * 50))
     * */
    val user: UserOutputModel,
    val playedGames: Int,
    val winGames: Int,
    val lostGames: Int,
    val rankPoints: Int
)
