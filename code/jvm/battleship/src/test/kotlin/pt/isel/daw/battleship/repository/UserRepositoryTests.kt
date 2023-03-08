package pt.isel.daw.battleship.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.daw.battleship.domain.PasswordValidationInfo
import pt.isel.daw.battleship.domain.TokenValidationInfo
import pt.isel.daw.battleship.repository.jdbi.JdbiUsersRepository
import pt.isel.daw.battleship.utils.testWithHandleAndRollback


class UserRepositoryTests {

    @Test
    fun `can create and retrieve`(): Unit = testWithHandleAndRollback { handle ->

        // given: repositories
        val userRepo = JdbiUsersRepository(handle)

        // add three users
        userRepo.storeUser("tiago2", "tduarte@sapo.pt", PasswordValidationInfo(""))
        userRepo.storeUser("pedro2", "pedro@sapo.pt", PasswordValidationInfo(""))
        userRepo.storeUser("raul2", "raul@sapo.pt", PasswordValidationInfo(""))

        // check if users exists
        val td = userRepo.getUserByUsername("tiago2") ?: fail("user must exist")
        val pp = userRepo.getUserByUsername("pedro2") ?: fail("user must exist")
        val rf = userRepo.getUserByUsername("raul2") ?: fail("user must exist")

        //check if email is correct
        assertEquals(td.email, "tduarte@sapo.pt")
        assertEquals(pp.email, "pedro@sapo.pt")
        assertEquals(rf.email, "raul@sapo.pt")

        // check if it can get user by email
        val tdEmail = userRepo.isUserStoredByEmail("tduarte@sapo.pt")
        val ppEmail = userRepo.isUserStoredByEmail("pedro@sapo.pt")
        val rfEmail = userRepo.isUserStoredByEmail("raul@sapo.pt")
        assertTrue(tdEmail)
        assertTrue(ppEmail)
        assertTrue(rfEmail)

        // check if can het user by username
        val tdUsername = userRepo.isUserStoredByUsername("tiago2")
        val pfUsername = userRepo.isUserStoredByUsername("pedro2")
        val rfUsername = userRepo.isUserStoredByUsername("raul2")
        assertTrue(tdUsername)
        assertTrue(pfUsername)
        assertTrue(rfUsername)

        //create token to user
        userRepo.createToken(td.id, TokenValidationInfo("testTokenTD"))
        userRepo.createToken(td.id, TokenValidationInfo("testTokenTD2"))
        userRepo.createToken(pp.id, TokenValidationInfo("testTokenPP"))
        userRepo.createToken(rf.id, TokenValidationInfo("testTokenRF"))
        val userToken = userRepo.getUserByTokenValidationInfo(TokenValidationInfo("testTokenTD"))
        val userToken2 = userRepo.getUserByTokenValidationInfo(TokenValidationInfo("testTokenTD2"))
        val userToken3 = userRepo.getUserByTokenValidationInfo(TokenValidationInfo("testTokenPP"))
        val userToken4 = userRepo.getUserByTokenValidationInfo(TokenValidationInfo("testTokenRF"))

        //check if exist user by token
        assertNotNull(userToken)
        assertNotNull(userToken2)
        assertNotNull(userToken3)
        assertNotNull(userToken4)

        //check if token can retrieve user email
        if (userToken != null) {
            assertEquals(userToken.email, "tduarte@sapo.pt")
        }
        if (userToken2 != null) {
            assertEquals(userToken2.email, "tduarte@sapo.pt")
        }
        if (userToken3 != null) {
            assertEquals(userToken3.email, "pedro@sapo.pt")
        }
        if (userToken4 != null) {
            assertEquals(userToken4.email, "raul@sapo.pt")
        }
    }


    @Test
    fun `can create and retrieve and logout`(): Unit = testWithHandleAndRollback { handle ->

        // given: repositories
        val userRepo = JdbiUsersRepository(handle)
        // add one user
        userRepo.storeUser("tiago2", "tduarte@sapo.pt", PasswordValidationInfo(""))
        // check if user exists
        val td = userRepo.getUserByUsername("tiago2") ?: fail("user must exist")

        // create two token to user
        userRepo.createToken(td.id, TokenValidationInfo("testTokenTD"))
        userRepo.createToken(td.id, TokenValidationInfo("testTokenTD2"))

        val userToken = userRepo.getUserByTokenValidationInfo(TokenValidationInfo("testTokenTD"))
        val userToken2 = userRepo.getUserByTokenValidationInfo(TokenValidationInfo("testTokenTD2"))

        // check if exist user by token
        assertNotNull(userToken)
        assertNotNull(userToken2)

        // invalidate token number two
        userRepo.invalidateToken(TokenValidationInfo("testTokenTD2"))

        // then: check result
        val userTokenInvalid = userRepo.getUserByTokenValidationInfo(TokenValidationInfo("testTokenTD2"))
        val userTokenOk = userRepo.getUserByTokenValidationInfo(TokenValidationInfo("testTokenTD"))
        assertNull(userTokenInvalid)
        assertNotNull(userTokenOk)
    }

    @Test
    fun `can create recoveryToken`(): Unit = testWithHandleAndRollback { handle ->

        // given: repositories
        val userRepo = JdbiUsersRepository(handle)
        // add one user
        userRepo.storeUser("tiago2", "tduarte@sapo.pt", PasswordValidationInfo("password"))
        // check if user exists
        val td = userRepo.getUserByUsername("tiago2") ?: fail("user must exist")
        // create recoveryToken to user
        userRepo.createRecoveryToken(td.id, TokenValidationInfo("aaa"))
        // get recoveryToken
        val recoveryTokenIsValid = userRepo.recoveryTokenIsValid(TokenValidationInfo("aaa"))
        //Check if token is valid
        assertTrue(recoveryTokenIsValid)

        // recovery token not exists
        assertFalse(userRepo.recoveryTokenIsValid(TokenValidationInfo("xxxxx")))

        // update password with recovery token
        userRepo.updateUserPasswordWithRecoveryToken(
            TokenValidationInfo("aaa"),
            PasswordValidationInfo(("newPasswordOK"))
        )

        // check if password is changed
        val tdNewPassword = userRepo.getUserByUsername("tiago2") ?: fail("user must exist")
        assertEquals(tdNewPassword.passwordValidation.validationInfo, "newPasswordOK")

        //Check if token is valid
        assertFalse(userRepo.recoveryTokenIsValid(TokenValidationInfo("aaa")))

        // can't update password with recovery token (already used)
        userRepo.updateUserPasswordWithRecoveryToken(TokenValidationInfo("aaa"), PasswordValidationInfo(("saasdXxx")))
        val tdNewPasswordWrong = userRepo.getUserByUsername("tiago2") ?: fail("user must exist")
        assertEquals(tdNewPasswordWrong.passwordValidation.validationInfo, "newPasswordOK")
    }

    @Test
    fun `can update and get statusRank`(): Unit = testWithHandleAndRollback { handle ->

        // given: repositories
        val userRepo = JdbiUsersRepository(handle)
        // add one user
        userRepo.storeUser("tiagoRank", "tduarte@sapo.pt", PasswordValidationInfo("password"))
        // check if user exists
        val td = userRepo.getUserByUsername("tiagoRank") ?: fail("user must exist")
        // update user rank
        userRepo.setUserRank(td.id, 10, 5, 500)
        // get total user rank
        val rank1 = userRepo.getUserRank()
        assertTrue(rank1[td.id] != null)
        assertEquals(rank1[td.id]!!.playedGames, 10)
        assertEquals(rank1[td.id]!!.winGames, 5)
        assertEquals(rank1[td.id]!!.rankPoints, 500)

        // update existing user rank
        userRepo.setUserRank(td.id, 12, 7, 700)
        // get total user rank
        val rank2 = userRepo.getUserRank()
        assertTrue(rank2[td.id] != null)
        assertEquals(rank2[td.id]!!.playedGames, 12)
        assertEquals(rank2[td.id]!!.winGames, 7)
        assertEquals(rank2[td.id]!!.rankPoints, 700)
    }
}