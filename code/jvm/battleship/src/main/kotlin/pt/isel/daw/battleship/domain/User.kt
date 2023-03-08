package pt.isel.daw.battleship.domain

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val passwordValidation: PasswordValidationInfo,
)