package pt.isel.daw.battleship.http.controllers

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.Either
import pt.isel.daw.battleship.domain.User
import pt.isel.daw.battleship.http.Relations
import pt.isel.daw.battleship.http.Uris
import pt.isel.daw.battleship.http.model.*
import pt.isel.daw.battleship.infra.siren
import pt.isel.daw.battleship.service.RecoveryTokenCreationError
import pt.isel.daw.battleship.service.TokenCreationError
import pt.isel.daw.battleship.service.UserCreationError
import pt.isel.daw.battleship.service.UsersService
import pt.isel.daw.battleship.utils.StringHelper
import javax.servlet.http.HttpServletRequest


@RestController
class UserController(
    private val userService: UsersService
) {
    companion object {
        const val mediaTypeJsonSiren: String = "application/vnd.siren+json"
        const val mediaTypeJson: String = "application/json"
        const val NAME_AUTHORIZATION_HEADER = "Authorization"
        const val LOCATION_HEADER = "Location"
    }

    // Register user in database
    @PostMapping(Uris.Users.REGISTER)
    fun create(@RequestBody input: UserCreateInputModel): ResponseEntity<*> {
        val res = userService.createUser(input.username, input.email, input.password)
        val ls = DefaultAnswerModel()

        return when (res) {
            is Either.Right -> {
                ls.message = "User [${input.username}] successfully created!"
                ResponseEntity.status(201)
                    .header("Content-Type", mediaTypeJson)
                    .header(
                        LOCATION_HEADER,
                        Uris.Users.getUserById(res.value).toASCIIString()
                    )
                    .body(ls)
            }

            is Either.Left -> when (res.value) {
                UserCreationError.UserAlreadyExists -> {
                    ls.error = ProblemOutputModel.userNameAlreadyExists
                    ProblemOutputModel.response(420, ls.error!!)
                }

                UserCreationError.EmailAlreadyExists -> {
                    ls.error = ProblemOutputModel.emailAlreadyExists
                    ProblemOutputModel.response(420, ls.error!!)
                }

                UserCreationError.InsecurePassword -> {
                    ls.error = ProblemOutputModel.insecurePassword
                    ProblemOutputModel.response(420, ls.error!!)
                }
            }
        }
    }

    // User login and get token
    @PostMapping(Uris.Users.LOGIN)
    fun login(@RequestBody input: LoginInputModel): ResponseEntity<*> {
        val res = userService.loginUser(input.username, input.password)
        val ls = DefaultAnswerModel()
        return when (res) {
            is Either.Right -> {
                //Create cookie to browser
                val cookie = ResponseCookie.from(StringHelper.cookieUserKey(), res.value.token)
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(3600)
                    .build()
                ResponseEntity.status(200)
                    .header("Content-Type", mediaTypeJson)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(res.value)
            }

            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> {
                    ls.error = ProblemOutputModel.userOrPasswordAreInvalid
                    ProblemOutputModel.response(401, ls.error!!)
                }
            }
        }
    }

    // User login and get token
    @PostMapping(Uris.Users.RECOVERY)
    fun recoveryPassword(@RequestBody input: RecoveryPasswordInputModel): ResponseEntity<*> {
        val res = userService.requestPasswordRecovery(input.email)
        val ls = DefaultAnswerModel()
        return when (res) {
            is Either.Right -> {
                ls.message = "Request password success! Check your email inbox!"
                ResponseEntity.status(200)
                    .header("Content-Type", mediaTypeJson)
                    .body(ls)
            }

            is Either.Left -> when (res.value) {
                RecoveryTokenCreationError.InvalidEmail -> {
                    ls.error = ProblemOutputModel.invalidEmail
                    ProblemOutputModel.response(420, ls.error!!)
                }

                RecoveryTokenCreationError.InternalError -> {
                    ls.error = ProblemOutputModel.internalError
                    ProblemOutputModel.response(500, ls.error!!)
                }
            }
        }
    }

    // Logout user and invalidate token
    @PostMapping(Uris.Users.LOGOUT)
    fun logout(user: User, request: HttpServletRequest): ResponseEntity<*> {
        val authToken = request.getHeader(NAME_AUTHORIZATION_HEADER).toString()
        val parts = authToken.trim().split(" ")
        val res = userService.invalidateToken(parts[1])
        val ls = DefaultAnswerModel()
        ls.message = res.toString()
        return if (res) {
            //inform browser that cookie has invalidated
            val deleteCookie = ResponseCookie
                .from(StringHelper.cookieUserKey(), "")
                .build()
            ResponseEntity.status(200)
                .header("Content-Type", mediaTypeJson)
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(null)
        } else {
            ls.error = ProblemOutputModel.badTokenFormat
            ProblemOutputModel.response(422, ls.error!!)
        }
    }

    // get user home @user
    @GetMapping(Uris.Users.HOME)
    fun getUserHome(user: User): ResponseEntity<*> {
        val ls = UserOutputModel(user.id, user.username, user.email)
        return ResponseEntity.status(200)
            .header("Content-Type", mediaTypeJsonSiren)
            .body(siren(ls) {
                clazz("userHome")
                properties
                link(Uris.Users.getUserHome(), Relations.SELF)
                action("newGame", Uris.Game.getNewGame(), HttpMethod.POST, mediaTypeJson) { numberField("maxShots") }
                action("gameHistory", Uris.Game.getGameHistory(), HttpMethod.GET, mediaTypeJson) {}
                action("logout", Uris.Users.getLogout(), HttpMethod.POST, mediaTypeJson) {}
            })
    }
}