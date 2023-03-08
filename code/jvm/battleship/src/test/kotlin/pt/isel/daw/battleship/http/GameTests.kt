package pt.isel.daw.battleship.http

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import pt.isel.daw.battleship.domain.Game
import pt.isel.daw.battleship.domain.GamePhase
import pt.isel.daw.battleship.domain.GameRankTotals
import pt.isel.daw.battleship.http.model.*
import pt.isel.daw.battleship.infra.SirenModel
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 80

    @Test
    fun `can create an game`() {
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
        val resultToken = client.post().uri(Uris.Users.LOGIN)
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

        assertNotNull(resultToken!!.token)

        //player 2
        // and: a random user
        val usernamePlayer2 = UUID.randomUUID().toString()
        val emailPlayer2 = UUID.randomUUID().toString()
        val passwordPlayer2 = "Chuck!123"

        // when: creating a user
        // then: the response is a 201 with a proper Location header
        client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to usernamePlayer2,
                    "email" to emailPlayer2,
                    "password" to passwordPlayer2
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }

        // when: creating a token
        // then: the response is a 200
        val resultTokenPlayer2 = client.post().uri(Uris.Users.LOGIN)
            .bodyValue(
                mapOf(
                    "username" to usernamePlayer2,
                    "password" to passwordPlayer2
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(UserTokenCreateOutputModel::class.java)
            .returnResult()
            .responseBody

        assertNotNull(resultTokenPlayer2!!.token)

        // same for the two players
        val randomShot = (1..100000).random()

        // when: start game
        val gameStartPlayer1 = client.post().uri(Uris.Game.NEW_GAME)
            .header("Authorization", "Bearer ${resultToken.token}")
            .bodyValue(
                mapOf(
                    "maxShots" to randomShot,
                )
            )
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(ProblemOutputModel::class.java)
            .returnResult()
            .responseBody

        assertEquals(ProblemOutputModel.userInLobby.type, gameStartPlayer1!!.type)

        // then: other user start game (with same rule)
        val gameStartPlayer2 = client.post().uri(Uris.Game.NEW_GAME)
            .header("Authorization", "Bearer ${resultTokenPlayer2.token}")
            .bodyValue(
                mapOf(
                    "maxShots" to randomShot,
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody

        val postResultPlayer2 = Gson().toJson(gameStartPlayer2!!.properties)
        val postOutputPlayer2 = Gson().fromJson(postResultPlayer2, GameOutputModel::class.java)
        assertTrue(postOutputPlayer2.gameId > 0)
    }

    @Test
    fun `define the layout of fleet in game, check fleet and shoot`() {
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
        val resultToken = client.post().uri(Uris.Users.LOGIN)
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

        //player 2
        // and: a random user
        val usernamePlayer2 = UUID.randomUUID().toString()
        val emailPlayer2 = UUID.randomUUID().toString()
        val passwordPlayer2 = "Chuck!123"

        // when: creating a user
        // then: the response is a 201 with a proper Location header
        client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to usernamePlayer2,
                    "email" to emailPlayer2,
                    "password" to passwordPlayer2
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }

        // when: creating a token
        // then: the response is a 200
        val resultTokenPlayer2 = client.post().uri(Uris.Users.LOGIN)
            .bodyValue(
                mapOf(
                    "username" to usernamePlayer2,
                    "password" to passwordPlayer2
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(UserTokenCreateOutputModel::class.java)
            .returnResult()
            .responseBody

        // same for the two players
        val randomShot = (1..100000).random()

        // when: start game
        client.post().uri(Uris.Game.NEW_GAME)
            .header("Authorization", "Bearer ${resultToken!!.token}")
            .bodyValue(
                mapOf(
                    "maxShots" to randomShot,
                )
            )
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(ProblemOutputModel::class.java)
            .returnResult()
            .responseBody

        // then: other user start game (with same rule)
        val gameStartPlayer2 = client.post().uri(Uris.Game.NEW_GAME)
            .header("Authorization", "Bearer ${resultTokenPlayer2!!.token}")
            .bodyValue(
                mapOf(
                    "maxShots" to randomShot,
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody

        val postResultPlayer2 = Gson().toJson(gameStartPlayer2!!.properties)
        val postOutputPlayer2 = Gson().fromJson(postResultPlayer2, GameOutputModel::class.java)
        assertTrue(postOutputPlayer2.gameId > 0)

        // then set layout for player one
        client.post().uri(Uris.Game.SET_LAYOUT_FLEET, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """
                [
                  {
                    "shipType": "CARRIER",
                    "shipLayout": "UP",
                    "referencePoint": {
                        "row":0,
                        "col":0
                    }
                  },
                    {
                    "shipType": "BATTLESHIP",
                    "shipLayout": "UP",
                    "referencePoint": {
                        "row":0,
                        "col":1
                    }
                  },
                    {
                    "shipType": "SUBMARINE",
                    "shipLayout": "DOWN",
                    "referencePoint": {
                        "row":4,
                        "col":7
                    }
                  },
                    {
                    "shipType": "CRUISER",
                    "shipLayout": "LEFT",
                    "referencePoint": {
                        "row":8,
                        "col":1
                    }
                  },
                    {
                    "shipType": "DESTROYER",
                    "shipLayout": "RIGHT",
                    "referencePoint": {
                        "row":9,
                        "col":6
                    }
                  }
                ]
            """
            )
            .exchange()
            .expectStatus().isOk

        // then set layout for player two
        client.post().uri(Uris.Game.SET_LAYOUT_FLEET, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultTokenPlayer2.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """
                [
                  {
                    "shipType": "CARRIER",
                    "shipLayout": "UP",
                    "referencePoint": {
                        "row":0,
                        "col":0
                    }
                  },
                    {
                    "shipType": "BATTLESHIP",
                    "shipLayout": "UP",
                    "referencePoint": {
                        "row":0,
                        "col":1
                    }
                  },
                    {
                    "shipType": "SUBMARINE",
                    "shipLayout": "DOWN",
                    "referencePoint": {
                        "row":4,
                        "col":7
                    }
                  },
                    {
                    "shipType": "CRUISER",
                    "shipLayout": "LEFT",
                    "referencePoint": {
                        "row":8,
                        "col":1
                    }
                  },
                    {
                    "shipType": "DESTROYER",
                    "shipLayout": "RIGHT",
                    "referencePoint": {
                        "row":9,
                        "col":6
                    }
                  }
                ]
            """
            )
            .exchange()
            .expectStatus().isOk

        //then checkFleet playerOne
        val checkFleetPlayerOne = client.method(HttpMethod.POST).uri(Uris.Game.GET_FLEET, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultTokenPlayer2.token}")
            .header("Content-Type", "application/json")
            .body(BodyInserters.fromValue("{\"myBoard\":true}"))
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody

        val getFleetResultPlayerOne = Gson().toJson(checkFleetPlayerOne!!.properties)
        assertTrue(
            getFleetResultPlayerOne.contains(
                "{\"boardPosition\":{\"row\":0,\"col\":0}," +
                        "\"wasShoot\":false,\"wasShip\":true,\"shipType\":\"CARRIER\",\"shipLayout\":\"UP\"}"
            )
        )

        //then check game phase is shooting playerOne
        val currentGamePhaseGetMethod = client.get().uri(Uris.Game.GET_GAME_PHASE, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody

        val currentGamePhase = Gson().toJson(currentGamePhaseGetMethod!!.properties)
        val getGamePhase = Gson().fromJson(currentGamePhase, GamePhase::class.java)
        assertEquals(getGamePhase.name, Game.Phase.SHOOTING_PLAYER_ONE.phase.name)

        //then shot playerTwoBoard
        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":0,"col":0}"""
            )
            .exchange()
            .expectStatus().isOk
    }


    @Test
    fun `simple game and check user rank`() {
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
        val resultToken = client.post().uri(Uris.Users.LOGIN)
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

        //player 2
        // and: a random user
        val usernamePlayer2 = UUID.randomUUID().toString()
        val emailPlayer2 = UUID.randomUUID().toString()
        val passwordPlayer2 = "Chuck!123"

        // when: creating a user
        // then: the response is a 201 with a proper Location header
        client.post().uri(Uris.Users.REGISTER)
            .bodyValue(
                mapOf(
                    "username" to usernamePlayer2,
                    "email" to emailPlayer2,
                    "password" to passwordPlayer2
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }

        // when: creating a token
        // then: the response is a 200
        val resultTokenPlayer2 = client.post().uri(Uris.Users.LOGIN)
            .bodyValue(
                mapOf(
                    "username" to usernamePlayer2,
                    "password" to passwordPlayer2
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(UserTokenCreateOutputModel::class.java)
            .returnResult()
            .responseBody

        // same for the two players
        val randomShot = (100..100000).random()

        // when: start game
        client.post().uri(Uris.Game.NEW_GAME)
            .header("Authorization", "Bearer ${resultToken!!.token}")
            .bodyValue(
                mapOf(
                    "maxShots" to randomShot,
                )
            )
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(ProblemOutputModel::class.java)
            .returnResult()
            .responseBody

        // then: other user start game (with same rule)
        val gameStartPlayer2 = client.post().uri(Uris.Game.NEW_GAME)
            .header("Authorization", "Bearer ${resultTokenPlayer2!!.token}")
            .bodyValue(
                mapOf(
                    "maxShots" to randomShot,
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody

        val postResultPlayer2 = Gson().toJson(gameStartPlayer2!!.properties)
        val postOutputPlayer2 = Gson().fromJson(postResultPlayer2, GameOutputModel::class.java)
        assertTrue(postOutputPlayer2.gameId > 0)

        // then set layout for player one
        client.post().uri(Uris.Game.SET_LAYOUT_FLEET, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """
                [
                  {
                    "shipType": "CARRIER",
                    "shipLayout": "UP",
                    "referencePoint": {
                        "row":0,
                        "col":0
                    }
                  },
                    {
                    "shipType": "BATTLESHIP",
                    "shipLayout": "UP",
                    "referencePoint": {
                        "row":0,
                        "col":1
                    }
                  },
                    {
                    "shipType": "SUBMARINE",
                    "shipLayout": "DOWN",
                    "referencePoint": {
                        "row":4,
                        "col":7
                    }
                  },
                    {
                    "shipType": "CRUISER",
                    "shipLayout": "LEFT",
                    "referencePoint": {
                        "row":8,
                        "col":1
                    }
                  },
                    {
                    "shipType": "DESTROYER",
                    "shipLayout": "RIGHT",
                    "referencePoint": {
                        "row":9,
                        "col":6
                    }
                  }
                ]
            """
            )
            .exchange()
            .expectStatus().isOk

        // then set layout for player two
        client.post().uri(Uris.Game.SET_LAYOUT_FLEET, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultTokenPlayer2.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """
                [
                  {
                    "shipType": "CARRIER",
                    "shipLayout": "UP",
                    "referencePoint": {
                        "row":0,
                        "col":0
                    }
                  },
                    {
                    "shipType": "BATTLESHIP",
                    "shipLayout": "UP",
                    "referencePoint": {
                        "row":0,
                        "col":1
                    }
                  },
                    {
                    "shipType": "SUBMARINE",
                    "shipLayout": "DOWN",
                    "referencePoint": {
                        "row":4,
                        "col":7
                    }
                  },
                    {
                    "shipType": "CRUISER",
                    "shipLayout": "LEFT",
                    "referencePoint": {
                        "row":8,
                        "col":1
                    }
                  },
                    {
                    "shipType": "DESTROYER",
                    "shipLayout": "RIGHT",
                    "referencePoint": {
                        "row":9,
                        "col":6
                    }
                  }
                ]
            """
            )
            .exchange()
            .expectStatus().isOk


        //then check game phase is shooting playerOne
        val currentGamePhaseGetMethod = client.get().uri(Uris.Game.GET_GAME_PHASE, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody

        val currentGamePhase = Gson().toJson(currentGamePhaseGetMethod!!.properties)
        val getGamePhase = Gson().fromJson(currentGamePhase, GamePhase::class.java)
        assertEquals(getGamePhase.name, Game.Phase.SHOOTING_PLAYER_ONE.phase.name)

        //Do the shoot, sunk all without mercy!
        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":0,"col":0}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":1,"col":0}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":2,"col":0}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":3,"col":0}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":4,"col":0}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":0,"col":1}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":1,"col":1}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":2,"col":1}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":3,"col":1}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":2,"col":7}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":3,"col":7}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":4,"col":7}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":8,"col":1}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":8,"col":2}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":8,"col":3}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":9,"col":5}"""
            )
            .exchange()
            .expectStatus().isOk

        client.post().uri(Uris.Game.SET_SHOOT, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .bodyValue(
                """{"row":9,"col":6}"""
            )
            .exchange()
            .expectStatus().isOk

        //then check game phase is playerOne win
        val currentGamePhaseGetMethodWin = client.get().uri(Uris.Game.GET_GAME_PHASE, postOutputPlayer2.gameId)
            .header("Authorization", "Bearer ${resultToken.token}")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody

        val currentGamePhase2 = Gson().toJson(currentGamePhaseGetMethodWin!!.properties)
        val getGamePhase2 = Gson().fromJson(currentGamePhase2, GamePhase::class.java)
        assertEquals(getGamePhase2.name, Game.Phase.PLAYER_ONE_WON.phase.name)

        //then check playerOne rank points
        val getPlayerRank = client.get().uri(Uris.RANKING)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody

        val rankFromSiren = Gson().toJson(getPlayerRank!!.properties)
        val itemType = object : TypeToken<List<GameRankTotals>>() {}.type
        val rankList = Gson().fromJson<List<GameRankTotals>>(rankFromSiren, itemType)

        rankList.forEach {
            if (it.user.username == username) {
                //player one win and get 100 points
                assertEquals(it.rankPoints, 100)
            }
        }
    }
}