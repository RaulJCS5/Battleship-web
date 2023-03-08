package pt.isel.daw.battleship.http.model

class UserTokenCreateOutputModel(
    var userId: Int,
    var token: String
)

class UserOutputModel(
    val id: Int,
    val username: String,
    val email: String
)

class RecoveryTokenCreateOutputModel(
    val token: String
)