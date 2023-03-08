package pt.isel.daw.battleship.repository.jdbi.models

class UserDbModel {
    companion object {
        fun getTableName(): String = "dbo.USER_BT"
        fun getUserId(): String = "id"
        fun getUsername(): String = "username"
        fun getEmail(): String = "email"
        fun getPasswordValidation(): String = "passwordValidation"
        fun getCreateDate(): String = "createDate"
        fun getUpdateDate(): String = "updateDate"
    }
}