package pt.isel.daw.battleship.repository.jdbi.models

class TokenDbModel {
    companion object {
        fun getTableName(): String = "dbo.TOKEN_BT"
        fun getUserId(): String = "userId"
        fun getToken(): String = "tokenValidation"
        fun getIsValid(): String = "isValid"
        fun getCreateDate(): String = "createDate"
        fun getInvalidateDate(): String = "invalidateDate"
    }
}