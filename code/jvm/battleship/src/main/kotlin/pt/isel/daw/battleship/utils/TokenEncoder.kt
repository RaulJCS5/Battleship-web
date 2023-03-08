package pt.isel.daw.battleship.utils

import pt.isel.daw.battleship.domain.TokenValidationInfo

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
    fun validate(validationInfo: TokenValidationInfo, token: String): Boolean
}