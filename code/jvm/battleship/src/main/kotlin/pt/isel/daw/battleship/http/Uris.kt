package pt.isel.daw.battleship.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    const val HOME = "/api"
    const val PUBLIC = "/api/public"
    const val RANKING = "/api/public/ranking/"

    fun getHome(): URI = URI(HOME)
    fun getPublic(): URI = URI(PUBLIC)
    fun getRanking(): URI = URI(RANKING)

    object Users {
        const val HOME = "/api/me"
        const val REGISTER = "/api/users/register"
        const val LOGIN = "/api/users/auth"
        const val RECOVERY = "/api/users/recovery"
        const val LOGOUT = "/api/users/logout"
        const val GET_USER_BY_ID = "/api/users/{id}"

        fun getUserById(id: Int) = UriTemplate(GET_USER_BY_ID).expand(id)
        fun getUserHome(): URI = URI(HOME)
        fun getRegisterUser(): URI = URI(REGISTER)
        fun getRecoveryUser(): URI = URI(RECOVERY)
        fun getLogin(): URI = URI(LOGIN)
        fun getLogout(): URI = URI(LOGOUT)
    }

    object Game {
        const val NEW_GAME = "/api/users/game/new"
        const val GIVE_UP_LOBBY = "/api/users/game/giveUpLobby"
        const val LIST_GAME_HISTORY = "/api/users/game/history"
        const val SET_LAYOUT_FLEET = "/api/users/game/{gameId}/setFleet"
        const val GET_FLEET = "/api/users/game/{gameId}/getFleet"
        const val SET_SHOOT = "/api/users/game/{gameId}/shoot"
        const val GET_GAME_PHASE = "/api/users/game/{gameId}/getCurrentPhase"
        const val GET_CURRENT_USER_GAME_ID = "/api/users/game"
        const val GIVE_UP_GAME = "/api/users/game/giveUp"
        const val GET_GAME_BY_ID = "/api/users/game/{gameId}"

        fun createGame(): URI = URI(NEW_GAME)
        fun getNewGame(): URI = URI(NEW_GAME)
        fun getGameHistory(): URI = URI(LIST_GAME_HISTORY)
        fun getGameById(id: Int) = UriTemplate(GET_GAME_BY_ID).expand(id)
        fun setGameLayoutFleet(gameId: Int) = UriTemplate(SET_LAYOUT_FLEET).expand(gameId)
        fun getGameFleet(gameId: Int) = UriTemplate(GET_FLEET).expand(gameId)
        fun getCurrentGamePhase(gameId: Int) = UriTemplate(GET_GAME_PHASE).expand(gameId)
        fun setShots(gameId: Int): URI = UriTemplate(SET_SHOOT).expand(gameId)
        fun getCurrentUserGameId(): URI = URI(GET_CURRENT_USER_GAME_ID)
    }

    object Chat {
        const val LISTEN = "/api/chat/listen/{username}"
        const val SEND = "/api/chat/send"
        const val REMOVE = "/api/chat/remove"
    }
}