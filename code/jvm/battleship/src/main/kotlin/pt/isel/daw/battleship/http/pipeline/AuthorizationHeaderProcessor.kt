package pt.isel.daw.battleship.http.pipeline

import org.springframework.stereotype.Component
import pt.isel.daw.battleship.domain.User
import pt.isel.daw.battleship.service.UsersService

@Component
class AuthorizationHeaderProcessor(
    val usersService: UsersService
) {

    fun process(authorizationValue: String?): User? {
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME) {
            return null
        }
        return usersService.getUserByToken(parts[1])
    }

    companion object {
        const val SCHEME = "bearer"
    }
}