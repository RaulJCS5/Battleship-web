package pt.isel.daw.battleship.repository.jdbi.models

class LobbyRoomDbModel {
    companion object {
        fun getTableName(): String = "dbo.LOBBY_ROOM_BT"
        fun getId(): String = "id"
        fun getUserId(): String = "userId"
        fun getRulePair(): String = "shotsRule"
        fun getEntryDate(): String = "entryDate"
    }
}