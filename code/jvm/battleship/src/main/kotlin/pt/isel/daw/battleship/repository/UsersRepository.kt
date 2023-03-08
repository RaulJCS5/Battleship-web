package pt.isel.daw.battleship.repository

import pt.isel.daw.battleship.domain.GameRankTotals
import pt.isel.daw.battleship.domain.PasswordValidationInfo
import pt.isel.daw.battleship.domain.TokenValidationInfo
import pt.isel.daw.battleship.domain.User

interface UsersRepository {

    fun storeUser(
        username: String,
        email: String,
        passwordValidation: PasswordValidationInfo,
    ): Int

    fun getUserByUsername(username: String): User?
    fun getUserByEmail(email: String): User?
    fun getUserById(id: Int): User?
    fun isUserStoredByUsername(username: String): Boolean
    fun isUserStoredByEmail(email: String): Boolean

    //User Raking
    fun getUserRank(): HashMap<Int,GameRankTotals>
    fun setUserRank(userId: Int, playedGames: Int, winGames: Int, rankPoints: Int)
    fun userRankAlreadyExists(userId: Int): Boolean

    //Session
    fun createToken(userId: Int, token: TokenValidationInfo)
    fun invalidateToken(token: TokenValidationInfo)
    fun getUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): User?

    //Recovery password (Android)
    fun createRecoveryToken(userId: Int, token: TokenValidationInfo)
    fun recoveryTokenIsValid(token: TokenValidationInfo): Boolean
    fun updateUserPasswordWithRecoveryToken(token: TokenValidationInfo, newPassword: PasswordValidationInfo)
}