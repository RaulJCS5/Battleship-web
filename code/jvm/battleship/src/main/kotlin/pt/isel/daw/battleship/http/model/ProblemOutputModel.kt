package pt.isel.daw.battleship.http.model

import org.springframework.http.ResponseEntity
import java.net.URI

class ProblemOutputModel(
    val type: URI,
    val title: String,
    val detail: String,
) {
    companion object {
        const val MEDIA_TYPE = "application/problem+json"
        fun response(status: Int, problem: ProblemOutputModel) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body(problem)

        val userNameAlreadyExists = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/username-already-exists"
            ), "Username already exists!", "Register user fail. Username already exists!"
        )
        val insecurePassword = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/insecure-password"
            ), "Insecure password!", "Given password does not meet safety requirements!"
        )
        val emailAlreadyExists = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/email-already-exists"
            ),
            "Email already exists!", "Given email is being used!",
        )

        val userOrPasswordAreInvalid = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/user-or-password-are-invalid"
            ), "Username or password are invalid!", "Given username or password are invalid!"
        )

        val badTokenFormat = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/bad-token-format"
            ), "Bad token format!", "Bad token format sent to server!"
        )

        val invalidEmail = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/email-not-exists"
            ), "Email not exists!", "Email not exists in our game system!"
        )

        val internalError = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/internal-server-error"
            ),
            "Internal server error!",
            "Internal server error, contact system administrator!"
        )

        val alreadyInActiveGame = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/already-in-active-game"
            ),
            "User already playing!",
            "User already playing, can't join lobby!"
        )

        val invalidUser = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/invalid-user"
            ), "Invalid user!", "Invalid user to play!"
        )

        val userAlreadyInLobby = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/already-in-lobby"
            ), "User already in lobby!", "User already in lobby, wait for pair!"
        )

        val invalidPair = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/invalid-pair"
            ), "Invalid pair!", "Invalid pair, try again!"
        )

        val userInLobby = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/set-in-lobby"
            ), "User set in lobby", "User in lobby, wait for pair!"
        )

        val invalidRule = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/set-in-lobby"
            ), "Invalid pair rule!", "Invalid pair rule! MaxShoot >=1"
        )

        val invalidFleet = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/invalid-fleet"
            ), "Invalid fleet!", "Invalid fleet sent to API!"
        )

        val invalidGame = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/invalid-game"
            ), "Invalid game!", "Invalid game!"
        )

        val invalidBoard = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/invalid-board"
            ), "Invalid board!", "Invalid board!"
        )

        val emptyBoard = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/empty-board"
            ), "Empty board!", "Empty board!"
        )

        val invalidShot = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/invalid-shot"
            ), "Invalid shoot", "Error, the shoot is invalid!"
        )

        val emptyRanking = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/empty-ranking"
            ), "Ranking is empty", "No played games, raking is empty!"
        )

        val userNotPlayingGame = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/user-not-playing-game"
            ), "User not playing game", "User not playing game, join lobby!"
        )


        val userNotExistsLobby = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/user-lobby-missing"
            ), "User not exists in lobby room", "User not exists in lobby room! Operation failed!"
        )

        val playerCantGiveUp = ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/player-cant-give-up"
            ), "Player can't give up", "Player can't give up, check if is in active game!"
        )
    }
}