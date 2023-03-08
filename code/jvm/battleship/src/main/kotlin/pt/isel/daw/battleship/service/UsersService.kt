package pt.isel.daw.battleship.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pt.isel.daw.battleship.Either
import pt.isel.daw.battleship.domain.PasswordValidationInfo
import pt.isel.daw.battleship.domain.TokenValidationInfo
import pt.isel.daw.battleship.domain.User
import pt.isel.daw.battleship.domain.UserLogic
import pt.isel.daw.battleship.http.model.RecoveryTokenCreateOutputModel
import pt.isel.daw.battleship.http.model.UserTokenCreateOutputModel
import pt.isel.daw.battleship.repository.TransactionManager
import pt.isel.daw.battleship.utils.TokenEncoder

sealed class UserCreationError {
    object UserAlreadyExists : UserCreationError()
    object EmailAlreadyExists : UserCreationError()
    object InsecurePassword : UserCreationError()
}
typealias UserCreationResult = Either<UserCreationError, Int>

sealed class TokenCreationError {
    object UserOrPasswordAreInvalid : TokenCreationError()
}
typealias TokenCreationResult = Either<TokenCreationError, UserTokenCreateOutputModel>

sealed class UserIdFetchError {
    object UserNameAreInvalid : UserIdFetchError()
}
typealias UserIdResult = Either<UserIdFetchError, Int>

sealed class RecoveryTokenCreationError {
    object InvalidEmail : RecoveryTokenCreationError()
    object InternalError : RecoveryTokenCreationError()
}
typealias RecoveryTokenCreationResult = Either<RecoveryTokenCreationError, Boolean>

sealed class UpdateUserPasswordError {
    object InsecurePassword : UpdateUserPasswordError()
    object InvalidToken : UpdateUserPasswordError()
}
typealias UpdateUserPasswordResult = Either<UpdateUserPasswordError, Boolean>

@Component
class UsersService(
    private val transactionManager: TransactionManager,
    private val userLogic: UserLogic,
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
) {

    fun requestPasswordRecovery(email: String): RecoveryTokenCreationResult {
        if (email.isBlank()) {
            return Either.Left(RecoveryTokenCreationError.InvalidEmail)
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            if (usersRepository.isUserStoredByEmail(email)) {
                val userId = usersRepository.getUserByEmail(email)?.id
                if (userId != null) {
                    val token = userLogic.generateToken()
                    usersRepository.createRecoveryToken(userId, tokenEncoder.createValidationInformation(token))
                    //TODO send recovery token
                    return@run Either.Right(true)
                }
                return@run Either.Left(RecoveryTokenCreationError.InternalError)
            } else {
                return@run Either.Left(RecoveryTokenCreationError.InvalidEmail)
            }
        }
    }

    fun updateUserPassword(token: String, password: String): UpdateUserPasswordResult {
        if (!userLogic.canBeToken(token)) {
            return Either.Left(UpdateUserPasswordError.InvalidToken)
        }
        if (!userLogic.isSafePassword(password)) {
            return Either.Left(UpdateUserPasswordError.InsecurePassword)
        }
        val passwordValidationInfo = PasswordValidationInfo(
            passwordEncoder.encode(password)
        )
        return transactionManager.run {
            val usersRepository = it.usersRepository
            //Check if is a valid token to recover password
            if (usersRepository.recoveryTokenIsValid(TokenValidationInfo(token))) {
                usersRepository.updateUserPasswordWithRecoveryToken(TokenValidationInfo(token), passwordValidationInfo)
                return@run Either.Right(true)
            } else {
                return@run Either.Left(UpdateUserPasswordError.InvalidToken)
            }
        }
    }

    fun createUser(username: String, email: String, password: String): UserCreationResult {
        if (!userLogic.isSafePassword(password)) {
            return Either.Left(UserCreationError.InsecurePassword)
        }
        val passwordValidationInfo = PasswordValidationInfo(
            passwordEncoder.encode(password)
        )

        return transactionManager.run {
            val usersRepository = it.usersRepository
            if (usersRepository.isUserStoredByUsername(username)) {
                Either.Left(UserCreationError.UserAlreadyExists)
            } else if (usersRepository.isUserStoredByEmail(email)) {
                Either.Left(UserCreationError.EmailAlreadyExists)
            } else {
                val id = usersRepository.storeUser(username, email, passwordValidationInfo)
                Either.Right(id)
            }
        }
    }

    fun loginUser(username: String, password: String): TokenCreationResult {
        if (username.isBlank() || password.isBlank()) {
            Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val user: User = usersRepository.getUserByUsername(username) ?: return@run userNotFound()
            if (!passwordEncoder.matches(password, user.passwordValidation.validationInfo)) {
                return@run Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
            }
            val token = userLogic.generateToken()
            usersRepository.createToken(user.id, tokenEncoder.createValidationInformation(token))
            Either.Right(UserTokenCreateOutputModel(user.id, token))
        }
    }

    fun getUserByToken(token: String): User? {
        if (!userLogic.canBeToken(token)) {
            return null
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val tokenValidationInfo = tokenEncoder.createValidationInformation(token)
            usersRepository.getUserByTokenValidationInfo(tokenValidationInfo)
        }
    }

    fun invalidateToken(token: String): Boolean {
        if (!userLogic.canBeToken(token)) {
            return false
        }
        transactionManager.run {
            val usersRepository = it.usersRepository
            val tokenValidationInfo = tokenEncoder.createValidationInformation(token)
            usersRepository.invalidateToken(tokenValidationInfo)
        }
        return true
    }

    //deprecated (to validate)
    fun getUserIdByUsername(username: String): UserIdResult {
        if (username.isBlank()) {
            Either.Left(UserIdFetchError.UserNameAreInvalid)
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val user: User = usersRepository.getUserByUsername(username) ?: return@run userIdNotFound()
            Either.Right(user.id)
        }
    }

    //deprecated (to validate)
    fun getUserById(id: Int): User? {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.getUserById(id)
        }
    }

    private fun userNotFound(): TokenCreationResult {
        return Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
    }

    private fun userIdNotFound(): UserIdResult {
        return Either.Left(UserIdFetchError.UserNameAreInvalid)
    }
}