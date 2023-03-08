package pt.isel.daw.battleship.repository.jdbi.models

class StatusDbModel {
    companion object {
        fun getTableName(): String = "dbo.STATUS_BT"
        fun getId(): String = "id"
        fun getUserId(): String = "userId"
        fun getPlayedGames(): String = "playedGames"
        fun getWinGames(): String = "winGames"
        fun getRankPoints(): String = "rankPoints"
        fun getCreateDate(): String = "createDate"
        fun getUpdateDate(): String = "updateDate"
    }
}