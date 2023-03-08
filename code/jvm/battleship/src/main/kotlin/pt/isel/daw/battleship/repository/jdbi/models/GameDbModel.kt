package pt.isel.daw.battleship.repository.jdbi.models

class GameDbModel {
    companion object {
        fun getTableName(): String = "dbo.GAME_BT"
        fun getId(): String = "id"
        fun getPhaseId(): String = "phaseId"
        fun getBoardPlayerOne(): String = "boardPlayerOne"
        fun getBoardPlayerTwo(): String = "boardPlayerTwo"
        fun getStartDate(): String = "startDate"
        fun getUpdateDate(): String = "updateDate"
        fun getPlayerOne(): String = "playerOne"
        fun getPlayerTwo(): String = "playerTwo"
        fun getMaxShootsRule(): String = "maxShootsRule"
        fun getRoundDeadline(): String = "roundDeadline"
        fun getRoundNumber(): String = "roundNumber"
        fun getShootsPerRoundCount(): String = "shootsPerRoundCount"
        fun getActiveRoundUserId(): String = "activeRoundUserId"
        fun getMaxTimePerRound(): String = "maxTimePerRound"
    }
}