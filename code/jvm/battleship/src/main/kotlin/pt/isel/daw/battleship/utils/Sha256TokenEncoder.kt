package pt.isel.daw.battleship.utils

import pt.isel.daw.battleship.domain.TokenValidationInfo
import java.security.MessageDigest
import java.util.Base64

class Sha256TokenEncoder : TokenEncoder {

    override fun createValidationInformation(token: String): TokenValidationInfo =
        TokenValidationInfo(hash(token))

    override fun validate(validationInfo: TokenValidationInfo, token: String): Boolean =
        validationInfo.validationInfo == hash(token)

    private fun hash(input: String): String {
        val messageDigest = MessageDigest.getInstance("SHA256")
        return Base64.getUrlEncoder().encodeToString(
            messageDigest.digest(
                Charsets.UTF_8.encode(input).array()
            )
        )
    }
}