package pt.isel.daw.battleship.http

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.battleship.http.model.ProblemOutputModel
import pt.isel.daw.battleship.http.model.UserOutputModel
import pt.isel.daw.battleship.http.model.UserTokenCreateOutputModel
import pt.isel.daw.battleship.infra.SirenModel
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 80

    @Test
    fun `can create an user`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val email = UUID.randomUUID().toString()
        val password = "Chuck!123"

        // when: creating a user
        // then: the response is a 201 with a proper Location header
        client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }
    }

    @Test
    fun `can create an user, obtain a token, and access user home`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val email = UUID.randomUUID().toString()
        val password = "Chuck!123"

        // when: creating a user
        // then: the response is a 201 with a proper Location header
        client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }

        // when: creating a token
        // then: the response is a 200
        val result = client.post().uri(Uris.Users.LOGIN)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(UserTokenCreateOutputModel::class.java)
            .returnResult()
            .responseBody

        val loginResult = Gson().toJson(result)
        val loginOutputResult = Gson().fromJson(loginResult, UserTokenCreateOutputModel::class.java)
        assertNotNull(loginOutputResult.token)
        assertNotNull(loginOutputResult.userId)

        // when: getting the user home with a valid token
        // then: the response is a 200 with the proper representation
        val result2 = client.get().uri(Uris.Users.HOME)
            .header("Authorization", "Bearer ${loginOutputResult.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody

        val homeResult = Gson().toJson(result2?.properties)
        val homeResultOutputResult = Gson().fromJson(homeResult, UserOutputModel::class.java)
        assertEquals(homeResultOutputResult.username, username)

        // when: getting the user home with an invalid token
        // then: the response is a 401 with the proper problem
        client.get().uri(Uris.Users.HOME)
            .header("Authorization", "Bearer ${loginOutputResult.token}-invalid")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().valueEquals("WWW-Authenticate", "bearer")
    }

    @Test
    fun `user creation produces an error if user already exists`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val email = UUID.randomUUID().toString()
        val password = "Chuck!123"

        // when: creating a user
        // then: the response is a 201 with a proper Location header
        client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }

        // when: creating the same user again
        // then: the response is a 420 with the proper type
        val result = client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().is4xxClientError
            .expectHeader().contentType(ProblemOutputModel.MEDIA_TYPE)
            .expectBody(ProblemOutputModel::class.java)
            .returnResult()
            .responseBody

        val registerResult = Gson().toJson(result)
        val registerOutputResult = Gson().fromJson(registerResult, ProblemOutputModel::class.java)
        assertEquals(registerOutputResult.type, ProblemOutputModel.userNameAlreadyExists.type)
    }

    @Test
    fun `user creation produces an error if email already exists`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val email = UUID.randomUUID().toString()
        val password = "Chuck!123"

        // when: creating a user
        // then: the response is a 201 with a proper Location header
        client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }

        // when: creating the same user again with different username
        val username2 = UUID.randomUUID().toString()
        // then: the response is a 400 with the proper type
        val result = client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to username2,
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().is4xxClientError
            .expectHeader().contentType(ProblemOutputModel.MEDIA_TYPE)
            .expectBody(ProblemOutputModel::class.java)
            .returnResult()
            .responseBody

        val registerResult = Gson().toJson(result)
        val registerOutputResult = Gson().fromJson(registerResult, ProblemOutputModel::class.java)
        assertEquals(registerOutputResult.type, ProblemOutputModel.emailAlreadyExists.type)
    }

    @Test
    fun `user creation produces an error if password is weak`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val email = UUID.randomUUID().toString()
        val password = "weakPassword"

        // then: the response is a 400 with the proper type
        val result = client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().is4xxClientError
            .expectHeader().contentType(ProblemOutputModel.MEDIA_TYPE)
            .expectBody(ProblemOutputModel::class.java)
            .returnResult()
            .responseBody

        val registerResult = Gson().toJson(result)
        val registerOutputResult = Gson().fromJson(registerResult, ProblemOutputModel::class.java)
        assertEquals(registerOutputResult.type, ProblemOutputModel.insecurePassword.type)
    }

    @Test
    fun `can create an user, obtain a token, and can logout`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val email = UUID.randomUUID().toString()
        val password = "Chuck!123"

        // when: creating a user
        // then: the response is a 201 with a proper Location header
        client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }

        // when: creating a token
        // then: the response is a 200
        val result = client.post().uri(Uris.Users.LOGIN)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(UserTokenCreateOutputModel::class.java)
            .returnResult()
            .responseBody

        val loginResult = Gson().toJson(result)
        val loginOutputResult = Gson().fromJson(loginResult, UserTokenCreateOutputModel::class.java)
        assertNotNull(loginOutputResult.token)
        assertNotNull(loginOutputResult.userId)

        // then: logout - the response is a 200
        client.post().uri(Uris.Users.LOGOUT)
            .header("Authorization", "Bearer ${loginOutputResult.token}")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `can do recovery password request for user`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val email = UUID.randomUUID().toString()
        val password = "Chuck!123"

        // when: creating a user
        // then: the response is a 201 with a proper Location header
        client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }

        // then: request password (200)
        client.post().uri(Uris.Users.RECOVERY)
            .bodyValue(
                mapOf(
                    "email" to email,
                )
            )
            .exchange()
            .expectStatus().isOk
    }
}