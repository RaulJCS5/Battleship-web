package pt.isel.daw.battleship.repository

import org.jdbi.v3.core.result.ResultBearing
import pt.isel.daw.battleship.domain.Game
import pt.isel.daw.battleship.domain.User

interface GamesRepository {
    fun create(game: Game): ResultBearing?
    fun update(game: Game)
    fun getGameById(id: Int): Game?
    fun getUserGameHistory(user: User): MutableList<Game>
    fun setUserToWaitingLobby(user: User, rulePair: Int): Int
    fun isUserAlreadyInLobby(user: User): Boolean
    fun removeUsersFromLobby(user: User)
    fun pairLobbyUser(rulePair: Int): User?
    fun playerAlreadyInActiveGame(user:User):Boolean
    fun getPlayerCurrentGameId(user:User):Int?
}