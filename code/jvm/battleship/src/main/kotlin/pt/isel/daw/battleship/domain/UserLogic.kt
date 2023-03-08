package pt.isel.daw.battleship.domain

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class UserLogic {

    fun generateToken(): String =
        ByteArray(TOKEN_BYTE_SIZE).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun canBeToken(token: String): Boolean = try {
        Base64.getUrlDecoder()
            .decode(token).size == TOKEN_BYTE_SIZE
    } catch (ex: IllegalArgumentException) {
        false
    }

    companion object {
        private const val TOKEN_BYTE_SIZE = 256 / 8
    }

    //Password must be at least 8 characters.
    //It must have at least 1 lowercase and at least 1 uppercase letter.
    //It must have one special character like ! or + or - or similar
    //It must have at least 1 digit
    fun isSafePassword(password: String): Boolean {
        return password.isNotEmpty() && password.count(Char::isDigit) > 0
                && password.any(Char::isLowerCase)
                && password.any(Char::isUpperCase) && password.any { it in "!,+^#$" }
    }
}