package pt.isel.daw.battleship.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.battleship.Either
import pt.isel.daw.battleship.domain.UserLogic
import pt.isel.daw.battleship.utils.Sha256TokenEncoder
import pt.isel.daw.battleship.utils.testWithTransactionManagerAndRollback
import java.util.*

class UserServiceTests {

    @Test
    fun `can create user, token, and retrieve by token`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->

            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )

            // when: creating a user with insure password, must be empty
            when (val createUserResult = userService.createUser("tiago2", "blabla@sapo.pt", "aaa")) {
                is Either.Left -> assertEquals(UserCreationError.InsecurePassword, createUserResult.value)
                is Either.Right -> assertTrue(createUserResult.value > 0)
            }

            // then: the creation is successful
            when (val createUserResult = userService.createUser("raul2", "qqq@sapo.pt", "Chuck!123")) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> assertTrue(createUserResult.value > 0)
            }

            // then: the creation is successful
            val creationResult = when (val loginUserResult = userService.loginUser("raul2", "Chuck!123")) {
                is Either.Left -> Assertions.fail(loginUserResult.toString())
                is Either.Right -> loginUserResult.value
            }
            // and: the token bytes have the expected length
            val tokenBytes = Base64.getUrlDecoder().decode(creationResult.token)
            assertEquals(256 / 8, tokenBytes.size)

            // when: retrieving the user by token
            val user = userService.getUserByToken(creationResult.token)
            // then: a user is found
            assertNotNull(user)
            // and: has the expected name
            assertEquals("raul2", user?.username)
            assertEquals("qqq@sapo.pt", user?.email)

            // check: if it can get userIdByUsername
            // option 1
            // then: the creation is error
            when (val userIdResult = userService.getUserIdByUsername("raul77")) {
                is Either.Left -> assertEquals(UserIdFetchError.UserNameAreInvalid, userIdResult.value)
                is Either.Right -> userIdResult.value
            }

            // option 2
            // then: the creation is success
            when (val userIdResult2 = userService.getUserIdByUsername("raul2")) {
                is Either.Left -> fail("Unexpected $userIdResult2")
                is Either.Right -> assertTrue(userIdResult2.value > 0)
            }
        }

    @Test
    fun `can create user, token, retrieve by token and invalidate token`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->

            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )

            // then: the creation is successful
            when (val createUserResult = userService.createUser("tiagoduarte", "a42525@isel.pt", "Chuck!123")) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> assertTrue(createUserResult.value > 0)
            }

            // then: the login is successful
            val creationResult = when (val loginUserResult = userService.loginUser("tiagoduarte", "Chuck!123")) {
                is Either.Left -> Assertions.fail(loginUserResult.toString())
                is Either.Right -> loginUserResult.value
            }
            // and: the token bytes have the expected length
            val tokenBytes = Base64.getUrlDecoder().decode(creationResult.token)
            assertEquals(256 / 8, tokenBytes.size)

            // when: retrieving the user by token
            val user = userService.getUserByToken(creationResult.token)
            // then: a user is found
            assertNotNull(user)
            // and: has the expected name
            assertEquals("tiagoduarte", user?.username)
            assertEquals("a42525@isel.pt", user?.email)

            // then: invalidate session with bad token
            val badResult = userService.invalidateToken("badToken")
            assertFalse(badResult)
            // then: invalidate session with good token
            val goodResult = userService.invalidateToken(creationResult.token)
            assertTrue(goodResult)
            // then: check token is no longer valid
            val user2 = userService.getUserByToken(creationResult.token)
            assertNull(user2)
        }

    @Test
    fun `can request password recovery`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->

            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            // then: the creation of user is successful
            when (val createUserResult = userService.createUser("tiagoduarte", "a42525@isel.pt", "Chuck!123")) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> assertTrue(createUserResult.value > 0)
            }
            // then: generate recovery token is successful
            val opResult = when (val recoveryResult = userService.requestPasswordRecovery("a42525@isel.pt")) {
                is Either.Left -> Assertions.fail(recoveryResult.toString())
                is Either.Right -> recoveryResult.value
            }
            // and: the token exists
            assertTrue(opResult)
        }

    //TODO more tests, after recovery change password and login
}