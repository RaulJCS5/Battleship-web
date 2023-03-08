package pt.isel.daw.battleship.http.pipeline

import org.springframework.stereotype.Component
import pt.isel.daw.battleship.domain.User
import pt.isel.daw.battleship.service.UsersService

@Component
class CookieProcessor(
    val usersService: UsersService
) {
    fun process(cookieVal: String?): User? {
        if (cookieVal == null) {
            return null
        }
        return usersService.getUserByToken(cookieVal)
    }
}