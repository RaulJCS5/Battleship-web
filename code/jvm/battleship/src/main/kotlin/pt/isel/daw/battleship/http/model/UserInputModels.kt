package pt.isel.daw.battleship.http.model

data class UserCreateInputModel(
    val username: String,
    val email: String,
    val password: String,
)

data class LoginInputModel(
    val username: String,
    val password: String,
)

data class RecoveryPasswordInputModel(
    val email: String
)