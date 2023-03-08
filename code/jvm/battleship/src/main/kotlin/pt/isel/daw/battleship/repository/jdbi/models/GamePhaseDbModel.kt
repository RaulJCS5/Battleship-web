package pt.isel.daw.battleship.repository.jdbi.models

class GamePhaseDbModel {
    companion object {
        fun getTableName(): String = "dbo.GAME_PHASE_BT"
        fun getPhaseId(): String = "id"
        fun getPhaseName(): String = "name"
    }
}