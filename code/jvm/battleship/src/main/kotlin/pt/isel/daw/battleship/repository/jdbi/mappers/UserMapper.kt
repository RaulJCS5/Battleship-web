package pt.isel.daw.battleship.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.daw.battleship.domain.PasswordValidationInfo
import pt.isel.daw.battleship.domain.User
import java.sql.ResultSet
import java.sql.SQLException

class UserMapper : ColumnMapper<User> {
    @Throws(SQLException::class)
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): User {
        return User(
            r.getInt(columnNumber),
            r.getString(columnNumber),
            r.getString(columnNumber),
            PasswordValidationInfo(r.getString(columnNumber))
        )
    }
}