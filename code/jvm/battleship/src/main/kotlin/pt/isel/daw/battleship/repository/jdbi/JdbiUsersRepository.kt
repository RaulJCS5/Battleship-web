package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.Nested
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.http.model.UserOutputModel
import pt.isel.daw.battleship.repository.UsersRepository
import pt.isel.daw.battleship.repository.jdbi.models.*

class JdbiUsersRepository(
    private val handle: Handle
) : UsersRepository {

    override fun storeUser(username: String, email: String, passwordValidation: PasswordValidationInfo): Int =
        handle.createUpdate(
            """
                insert into ${UserDbModel.getTableName()} (${UserDbModel.getUsername()},${UserDbModel.getEmail()},
                ${UserDbModel.getPasswordValidation()},${UserDbModel.getCreateDate()}) values (:username, :email, :password_validation,Current_timestamp)
                """
        )
            .bind("username", username)
            .bind("email", email)
            .bind("password_validation", passwordValidation.validationInfo)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun isUserStoredByUsername(username: String): Boolean =
        handle.createQuery(
            """
                select count(*) from  ${UserDbModel.getTableName()}
                where ${UserDbModel.getUsername()} = :username
                """
        )
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun isUserStoredByEmail(email: String): Boolean =
        handle.createQuery(
            """
                select count(*) from  ${UserDbModel.getTableName()}
                where ${UserDbModel.getEmail()} = :username
                """
        )
            .bind("username", email)
            .mapTo<Int>()
            .single() == 1

    override fun createRecoveryToken(userId: Int, token: TokenValidationInfo) {
        handle.createUpdate(
            """
                insert into ${RecoveryTokenDbModel.getTableName()} (${RecoveryTokenDbModel.getUserId()},
                ${RecoveryTokenDbModel.getToken()},${RecoveryTokenDbModel.getIsUsed()},
                ${RecoveryTokenDbModel.getCreateDate()},${RecoveryTokenDbModel.getExpireDate()}) 
                values (:user_id, :token_validation, :isUsed, Current_timestamp,Current_timestamp + interval '30 minutes')
                """
        )
            .bind("user_id", userId)
            .bind("token_validation", token.validationInfo)
            .bind("isUsed", false)
            .execute()
    }

    override fun recoveryTokenIsValid(token: TokenValidationInfo): Boolean =
        handle.createQuery(
            """
                select ${RecoveryTokenDbModel.getToken()} as validationInfo from
                (select MAX(${RecoveryTokenDbModel.getCreateDate()}) crDate from ${RecoveryTokenDbModel.getTableName()} 
                where ${RecoveryTokenDbModel.getToken()}= :token 
                and ${RecoveryTokenDbModel.getIsUsed()} = :isUsed and ${RecoveryTokenDbModel.getExpireDate()} >= current_timestamp) a
                join ${RecoveryTokenDbModel.getTableName()} b on a.crDate = b.${RecoveryTokenDbModel.getCreateDate()}
                """
        )
            .bind("token", token.validationInfo)
            .bind("isUsed", false)
            .mapTo<TokenValidationInfo>()
            .singleOrNull() != null

    override fun updateUserPasswordWithRecoveryToken(token: TokenValidationInfo, newPassword: PasswordValidationInfo) {
        // update password
        handle.createUpdate(
            """
                update ${UserDbModel.getTableName()} SET ${UserDbModel.getPasswordValidation()} = :newPassword 
                where ${UserDbModel.getUserId()} = 
                (select ${RecoveryTokenDbModel.getUserId()} from ${RecoveryTokenDbModel.getTableName()} 
                 where ${RecoveryTokenDbModel.getToken()}= :token 
                 and ${RecoveryTokenDbModel.getIsUsed()} = :isUsed and ${RecoveryTokenDbModel.getExpireDate()} >= current_timestamp)
                """
        )
            .bind("newPassword", newPassword.validationInfo)
            .bind("token", token.validationInfo)
            .bind("isUsed", false)
            .execute();

        // invalidate token
        handle.createUpdate(
            """
                update ${RecoveryTokenDbModel.getTableName()} SET ${RecoveryTokenDbModel.getIsUsed()} = :isUsed 
                where ${RecoveryTokenDbModel.getToken()} = :token
                """
        )
            .bind("isUsed", true)
            .bind("token", token.validationInfo)
            .execute();
    }

    override fun createToken(userId: Int, token: TokenValidationInfo) {
        handle.createUpdate(
            """
                insert into ${TokenDbModel.getTableName()} (${TokenDbModel.getUserId()},
                ${TokenDbModel.getToken()},${TokenDbModel.getIsValid()},${TokenDbModel.getCreateDate()}) 
                values (:user_id, :token_validation, :isValid, Current_timestamp)
                """
        )
            .bind("user_id", userId)
            .bind("token_validation", token.validationInfo)
            .bind("isValid", true)
            .execute()
    }

    override fun invalidateToken(token: TokenValidationInfo) {
        handle.createUpdate(
            """
                update ${TokenDbModel.getTableName()} SET ${TokenDbModel.getIsValid()} = :isValid,
                ${TokenDbModel.getInvalidateDate()}= Current_timestamp
                where ${TokenDbModel.getToken()}= :token
                """
        )
            .bind("isValid", false)
            .bind("token", token.validationInfo)
            .execute();
    }

    override fun getUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): User? =
        handle.createQuery(
            """
                select users.${UserDbModel.getUserId()},users.${UserDbModel.getUsername()}, users.${UserDbModel.getEmail()}, users.${UserDbModel.getPasswordValidation()} from 
                ${UserDbModel.getTableName()} as users inner join  ${TokenDbModel.getTableName()}
                as token on users.${UserDbModel.getUserId()}= token.${TokenDbModel.getUserId()} where 
                token.${TokenDbModel.getToken()} = :validation_information and token.${TokenDbModel.getIsValid()}= :isValid
                """
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .bind("isValid", true)
            .mapTo<User>()
            .singleOrNull()


    override fun getUserByUsername(username: String): User? =
        handle.createQuery(
            """
                select * from ${UserDbModel.getTableName()} where ${UserDbModel.getUsername()} = :username
                """
        )
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun getUserByEmail(email: String): User? =
        handle.createQuery(
            """
                select * from ${UserDbModel.getTableName()} where ${UserDbModel.getEmail()} = :email
                """
        )
            .bind("email", email)
            .mapTo<User>()
            .singleOrNull()

    override fun getUserById(id: Int): User? =
        handle.createQuery(
            """
                select * from ${UserDbModel.getTableName()} where ${UserDbModel.getUserId()} = :id
                """
        )
            .bind("id", id)
            .mapTo<User>()
            .singleOrNull()

    override fun getUserRank(): HashMap<Int, GameRankTotals> {
        val usersRank: HashMap<Int, GameRankTotals> = HashMap()
        handle.createQuery(
            """
                select 
                users.${UserDbModel.getUserId()} as userRank_${UserDbModel.getUserId()},
                users.${UserDbModel.getUsername()} as userRank_${UserDbModel.getUsername()},
                users.${UserDbModel.getEmail()} as userRank_${UserDbModel.getEmail()},
                users.${UserDbModel.getPasswordValidation()} as userRank_${UserDbModel.getPasswordValidation()},
                status.${StatusDbModel.getPlayedGames()}, status.${StatusDbModel.getWinGames()}, 
                status.${StatusDbModel.getPlayedGames()}-status.${StatusDbModel.getWinGames()} as lostGames, 
                status.${StatusDbModel.getRankPoints()}
                from ${StatusDbModel.getTableName()} status 
                inner join ${UserDbModel.getTableName()} users 
                on users.${UserDbModel.getUserId()} = status.${StatusDbModel.getUserId()}
                order by ${StatusDbModel.getRankPoints()} asc
                """
        )
            .mapTo<GameRankTotalsModel>()
            .list()
            .forEach() {
                val rank = it.toRank()
                usersRank[rank.user.id] = rank
            }
        return usersRank
    }

    override fun setUserRank(userId: Int, playedGames: Int, winGames: Int, rankPoints: Int) {
        val alreadyExists = userRankAlreadyExists(userId)
        if (alreadyExists) {
            handle.createUpdate(
                """
                update ${StatusDbModel.getTableName()} set ${StatusDbModel.getWinGames()} = :winGames,
                ${StatusDbModel.getPlayedGames()} = :playedGames,
                ${StatusDbModel.getRankPoints()} = :rankPoints ,
                ${StatusDbModel.getUpdateDate()}= Current_timestamp
                where ${StatusDbModel.getUserId()}= :userId
                """
            )
                .bind("winGames", winGames)
                .bind("playedGames", playedGames)
                .bind("rankPoints", rankPoints)
                .bind("userId", userId)
                .execute();
        } else {
            handle.createUpdate(
                """
                insert into ${StatusDbModel.getTableName()} (${StatusDbModel.getUserId()},
                ${StatusDbModel.getPlayedGames()},${StatusDbModel.getWinGames()},
                ${StatusDbModel.getRankPoints()},${StatusDbModel.getCreateDate()}) 
                values(:userId,:playedGames,:winGames,:rankPoints,Current_timestamp)
                """
            )
                .bind("userId", userId)
                .bind("winGames", winGames)
                .bind("playedGames", playedGames)
                .bind("rankPoints", rankPoints)
                .execute();
        }
    }

    override fun userRankAlreadyExists(userId: Int): Boolean =
        handle.createQuery(
            """
                select users.${UserDbModel.getUserId()},users.${UserDbModel.getUsername()},
                users.${UserDbModel.getEmail()},users.${UserDbModel.getPasswordValidation()}
                from ${StatusDbModel.getTableName()} status 
                inner join ${UserDbModel.getTableName()} users 
                on users.${UserDbModel.getUserId()} = status.${StatusDbModel.getUserId()}
                where status.${StatusDbModel.getUserId()} = :userId
                """
        )
            .bind("userId", userId)
            .mapTo<User>()
            .singleOrNull() != null

}

class GameRankTotalsModel(
    @Nested("userRank")
    val user: User,
    private val playedGames: Int,
    private val winGames: Int,
    private val lostGames: Int,
    private val rankPoints: Int,
) {
    fun toRank() =
        GameRankTotals(
            UserOutputModel(user.id,user.username,user.email),
            playedGames,
            winGames,
            lostGames,
            rankPoints
        )
}