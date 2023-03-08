package pt.isel.daw.battleship.repository.jdbi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.Nested
import org.jdbi.v3.core.result.ResultBearing
import org.jdbi.v3.core.statement.Update
import org.postgresql.util.PGobject
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.domain.fleet.PositionStateBoard
import pt.isel.daw.battleship.repository.GamesRepository
import pt.isel.daw.battleship.repository.jdbi.models.*
import java.sql.Timestamp
import java.util.*

class JdbiGamesRepository(
    private val handle: Handle,
) : GamesRepository {
    override fun create(game: Game): ResultBearing? {
        return handle.createUpdate(
            """
                insert into ${GameDbModel.getTableName()}(${GameDbModel.getPhaseId()}, 
                ${GameDbModel.getBoardPlayerOne()},${GameDbModel.getBoardPlayerTwo()},  ${GameDbModel.getStartDate()}, ${GameDbModel.getUpdateDate()},
                ${GameDbModel.getRoundDeadline()},  ${GameDbModel.getPlayerOne()}, ${GameDbModel.getPlayerTwo()},
                ${GameDbModel.getActiveRoundUserId()},${GameDbModel.getMaxTimePerRound()},
                ${GameDbModel.getMaxShootsRule()},${GameDbModel.getRoundNumber()},${GameDbModel.getShootsPerRoundCount()}) 
                values(:state, :boardPlayerOne, :boardPlayerTwo, :created, :updated, :deadline, 
                :playerOne, :playerTwo, :activeUser, :maxTimeRound, :maxShoots, :roundNumber, :shootsCount)
                """
        )
            .bind("state", game.phase.id)
            .bindBoard("boardPlayerOne", game.boardPlayerOne)
            .bindBoard("boardPlayerTwo", game.boardPlayerTwo)
            .bind("created", game.startDate)
            .bind("updated", game.updateDate)
            .bind("deadline", game.roundDeadline)
            .bind("playerOne", game.playerOne.id)
            .bind("playerTwo", game.playerTwo.id)
            .bind("activeUser", game.activeRoundUser.id)
            .bind("maxTimeRound", game.maxTimePerRound)
            .bind("maxShoots", game.maxShootsRule)
            .bind("roundNumber", game.roundNumber)
            .bind("shootsCount", game.shootsPerRoundCount)
            .executeAndReturnGeneratedKeys();
    }

    override fun update(game: Game) {
        handle.createUpdate(
            """
                update ${GameDbModel.getTableName()} SET ${GameDbModel.getPhaseId()} = :state,
                ${GameDbModel.getBoardPlayerOne()}= :boardPlayerOne,${GameDbModel.getBoardPlayerTwo()}= :boardPlayerTwo,
                ${GameDbModel.getStartDate()}= :created, ${GameDbModel.getUpdateDate()}= :updated,
                ${GameDbModel.getRoundDeadline()}= :deadline, ${GameDbModel.getPlayerOne()}= :playerOne,${GameDbModel.getPlayerTwo()}= :playerTwo,
                ${GameDbModel.getActiveRoundUserId()}= :activeUser,${GameDbModel.getMaxTimePerRound()}= :maxTimeRound,
                ${GameDbModel.getMaxShootsRule()}= :maxShoots,${GameDbModel.getRoundNumber()}= :roundNumber,
                ${GameDbModel.getShootsPerRoundCount()}= :shootsCount
                where ${GameDbModel.getId()}= :id
                """
        )
            .bind("state", game.phase.id)
            .bindBoard("boardPlayerOne", game.boardPlayerOne)
            .bindBoard("boardPlayerTwo", game.boardPlayerTwo)
            .bind("created", game.startDate)
            .bind("updated", game.updateDate)
            .bind("deadline", game.roundDeadline)
            .bind("playerOne", game.playerOne.id)
            .bind("playerTwo", game.playerTwo.id)
            .bind("activeUser", game.activeRoundUser.id)
            .bind("maxTimeRound", game.maxTimePerRound)
            .bind("maxShoots", game.maxShootsRule)
            .bind("roundNumber", game.roundNumber)
            .bind("shootsCount", game.shootsPerRoundCount)
            .bind("id", game.id)
            .execute();
    }

    override fun getGameById(id: Int): Game? =

        handle.createQuery(
            """
               select games.${GameDbModel.getId()}, games.${GameDbModel.getBoardPlayerOne()}, games.${GameDbModel.getBoardPlayerTwo()},
                games.${GameDbModel.getStartDate()}, games.${GameDbModel.getUpdateDate()}, games.${GameDbModel.getRoundDeadline()},
                games.${GameDbModel.getActiveRoundUserId()},games.${GameDbModel.getMaxTimePerRound()},games.${GameDbModel.getMaxShootsRule()},
                games.${GameDbModel.getShootsPerRoundCount()},
                user_1.${UserDbModel.getUserId()} as player1_${UserDbModel.getUserId()}, 
                user_1.${UserDbModel.getUsername()} as player1_${UserDbModel.getUsername()},
                user_1.${UserDbModel.getEmail()} as player1_${UserDbModel.getEmail()},
                user_1.${UserDbModel.getPasswordValidation()} as player1_${UserDbModel.getPasswordValidation()}, 
                user_2.${UserDbModel.getUserId()} as player2_${UserDbModel.getUserId()}, 
                user_2.${UserDbModel.getUsername()} as player2_${UserDbModel.getUsername()},
                user_2.${UserDbModel.getEmail()} as player2_${UserDbModel.getEmail()},
                user_2.${UserDbModel.getPasswordValidation()} as player2_${UserDbModel.getPasswordValidation()},
                games.${GameDbModel.getRoundNumber()},
                user_round.${UserDbModel.getUserId()} as roundUser_${UserDbModel.getUserId()}, 
                user_round.${UserDbModel.getUsername()} as roundUser_${UserDbModel.getUsername()},
                user_round.${UserDbModel.getEmail()} as roundUser_${UserDbModel.getEmail()}, 
                user_round.${UserDbModel.getPasswordValidation()} as roundUser_${UserDbModel.getPasswordValidation()},
                phase.${GamePhaseDbModel.getPhaseId()} as phaseObj_${GamePhaseDbModel.getPhaseId()},
                phase.${GamePhaseDbModel.getPhaseName()} as phaseObj_${GamePhaseDbModel.getPhaseName()}
                from ${GameDbModel.getTableName()} games
                inner join ${UserDbModel.getTableName()} user_1 on games.${GameDbModel.getPlayerOne()} = user_1.${UserDbModel.getUserId()}
                inner join ${UserDbModel.getTableName()} user_2 on games.${GameDbModel.getPlayerTwo()} = user_2.${UserDbModel.getUserId()}
                inner join ${UserDbModel.getTableName()} user_round on games.${GameDbModel.getActiveRoundUserId()} = user_round.${UserDbModel.getUserId()}
                inner join ${GamePhaseDbModel.getTableName()} phase on games.${GameDbModel.getPhaseId()} = phase.${GamePhaseDbModel.getPhaseId()}
                where games.${GameDbModel.getId()} = :id
               """
        )
            .bind("id", id)
            .mapTo<GameModel>()
            .singleOrNull()
            ?.run {
                toGame()
            }

    override fun getUserGameHistory(user: User): MutableList<Game> {
        val games: MutableList<Game> = ArrayList()
        handle.createQuery(
            """
               select games.${GameDbModel.getId()}, games.${GameDbModel.getBoardPlayerOne()}, games.${GameDbModel.getBoardPlayerTwo()},
                games.${GameDbModel.getStartDate()}, games.${GameDbModel.getUpdateDate()}, games.${GameDbModel.getRoundDeadline()},
                games.${GameDbModel.getActiveRoundUserId()},games.${GameDbModel.getMaxTimePerRound()},games.${GameDbModel.getMaxShootsRule()},
                games.${GameDbModel.getShootsPerRoundCount()},
                user_1.${UserDbModel.getUserId()} as player1_${UserDbModel.getUserId()}, 
                user_1.${UserDbModel.getUsername()} as player1_${UserDbModel.getUsername()},
                user_1.${UserDbModel.getEmail()} as player1_${UserDbModel.getEmail()},
                user_1.${UserDbModel.getPasswordValidation()} as player1_${UserDbModel.getPasswordValidation()}, 
                user_2.${UserDbModel.getUserId()} as player2_${UserDbModel.getUserId()}, 
                user_2.${UserDbModel.getUsername()} as player2_${UserDbModel.getUsername()},
                user_2.${UserDbModel.getEmail()} as player2_${UserDbModel.getEmail()},
                user_2.${UserDbModel.getPasswordValidation()} as player2_${UserDbModel.getPasswordValidation()},
                games.${GameDbModel.getRoundNumber()},
                user_round.${UserDbModel.getUserId()} as roundUser_${UserDbModel.getUserId()}, 
                user_round.${UserDbModel.getUsername()} as roundUser_${UserDbModel.getUsername()},
                user_round.${UserDbModel.getEmail()} as roundUser_${UserDbModel.getEmail()}, 
                user_round.${UserDbModel.getPasswordValidation()} as roundUser_${UserDbModel.getPasswordValidation()},
                phase.${GamePhaseDbModel.getPhaseId()} as phaseObj_${GamePhaseDbModel.getPhaseId()},
                phase.${GamePhaseDbModel.getPhaseName()} as phaseObj_${GamePhaseDbModel.getPhaseName()}
                from ${GameDbModel.getTableName()} games
                inner join ${UserDbModel.getTableName()} user_1 on games.${GameDbModel.getPlayerOne()} = user_1.${UserDbModel.getUserId()}
                inner join ${UserDbModel.getTableName()} user_2 on games.${GameDbModel.getPlayerTwo()} = user_2.${UserDbModel.getUserId()}
                inner join ${UserDbModel.getTableName()} user_round on games.${GameDbModel.getActiveRoundUserId()} = user_round.${UserDbModel.getUserId()}
                inner join ${GamePhaseDbModel.getTableName()} phase on games.${GameDbModel.getPhaseId()} = phase.${GamePhaseDbModel.getPhaseId()}
                where games.${GameDbModel.getPlayerOne()} = :id or games.${GameDbModel.getPlayerTwo()} = :id 
               """
        )
            .bind("id", user.id)
            .mapTo<GameModel>()
            .list()
            .forEach() {
                games.add(it.toGame())
            }
        return games
    }

    override fun setUserToWaitingLobby(user: User, rulePair: Int): Int {
        return handle.createUpdate(
            """
                insert into ${LobbyRoomDbModel.getTableName()} (${LobbyRoomDbModel.getUserId()}, ${LobbyRoomDbModel.getRulePair()},
                ${LobbyRoomDbModel.getEntryDate()}) 
                values(:userId, :rule, Current_timestamp)
                """
        )
            .bind("userId", user.id)
            .bind("rule", rulePair)
            .execute();
    }

    override fun isUserAlreadyInLobby(user: User): Boolean =
        handle.createQuery(
            """
                select count(*) from  ${LobbyRoomDbModel.getTableName()}
                where ${LobbyRoomDbModel.getUserId()} = :userId
                """
        )
            .bind("userId", user.id)
            .mapTo<Int>()
            .single() == 1

    override fun removeUsersFromLobby(user: User) {
        handle.createUpdate(
            """
                delete from ${LobbyRoomDbModel.getTableName()}
                where ${LobbyRoomDbModel.getUserId()}= :userId
                """
        )
            .bind("userId", user.id)
            .execute();
    }

    override fun pairLobbyUser(rulePair: Int): User? =
        handle.createQuery(
            """
                select users.${UserDbModel.getUserId()},users.${UserDbModel.getUsername()},users.${UserDbModel.getEmail()},
                users.${UserDbModel.getPasswordValidation()} from 
                ${UserDbModel.getTableName()} as users 
                inner join  ${LobbyRoomDbModel.getTableName()}
                as lobby on users.${UserDbModel.getUserId()}= lobby.${LobbyRoomDbModel.getUserId()} where 
                lobby.${LobbyRoomDbModel.getRulePair()} = :rule
                """
        )
            .bind("rule", rulePair)
            .mapTo<User>()
            //First user match
            .firstOrNull()

    override fun playerAlreadyInActiveGame(user: User): Boolean =
        handle.createQuery(
            """
                select count(*) from  ${GameDbModel.getTableName()}
                where (${GameDbModel.getPlayerOne()} = :userId or ${GameDbModel.getPlayerTwo()} = :userId)
                and ${GameDbModel.getPhaseId()}<4
                """
        )
            .bind("userId", user.id)
            .mapTo<Int>()
            .single() == 1

    override fun getPlayerCurrentGameId(user: User): Int? =
        handle.createQuery(
            """
                select  ${GameDbModel.getId()} from  ${GameDbModel.getTableName()}
                where (${GameDbModel.getPlayerOne()} = :userId or ${GameDbModel.getPlayerTwo()} = :userId)
                and ${GameDbModel.getPhaseId()}<4
                """
        )
            .bind("userId", user.id)
            .mapTo<Int>()
            .firstOrNull()

    companion object {
        private fun Update.bindBoard(name: String, board: Board) = run {
            bind(
                name,
                PGobject().apply {
                    type = "jsonb"
                    value = serializeBoardToJson(board)
                }
            )
        }

        private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

        private fun serializeBoardToJson(board: Board): String = objectMapper.writeValueAsString(board.cells)

        fun deserializeBoardFromJson(json: String) = Board(
            objectMapper.readValue(json, Array<Array<PositionStateBoard>>::class.java),
        )
    }
}

class GameModel(
    private val id: Int,
    private val boardPlayerOne: Board,
    private val boardPlayerTwo: Board,
    private val startDate: Timestamp,
    private val updateDate: Timestamp?,
    private val roundDeadline: Timestamp?,
    private val maxTimePerRound: Int,
    private val maxShootsRule: Int,
    private val shootsPerRoundCount: Int,
    @Nested("player1")
    val playerOne: User,
    @Nested("player2")
    val playerTwo: User,
    private val roundNumber: Int,
    @Nested("roundUser")
    private val activeRoundUser: User,
    @Nested("phaseObj")
    private val phase: GamePhase,
) {
    fun toGame() =
        Game(
            id,
            phase,
            boardPlayerOne,
            boardPlayerTwo,
            startDate,
            updateDate,
            playerOne,
            playerTwo,
            maxShootsRule,
            roundDeadline,
            roundNumber,
            shootsPerRoundCount,
            activeRoundUser,
            maxTimePerRound,
        )
}