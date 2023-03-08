package pt.isel.daw.battleship.repository.jdbi.models

class RecoveryTokenDbModel {
    companion object {
        fun getTableName(): String = "dbo.RECOVERY_TOKEN_BT"
        fun getUserId(): String = "userId"
        fun getToken(): String = "tokenRecovery"
        fun getIsUsed(): String = "isUsed"
        fun getCreateDate(): String = "createDate"
        fun getExpireDate(): String = "expireDate"
    }
}