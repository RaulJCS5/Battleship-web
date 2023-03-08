package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.daw.battleship.repository.jdbi.mappers.BoardMapper
import pt.isel.daw.battleship.repository.jdbi.mappers.PasswordValidationInfoMapper
import pt.isel.daw.battleship.repository.jdbi.mappers.TokenValidationInfoMapper
import pt.isel.daw.battleship.repository.jdbi.mappers.UserMapper

fun Jdbi.configure(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(PasswordValidationInfoMapper())
    registerColumnMapper(TokenValidationInfoMapper())
    registerColumnMapper(BoardMapper())
    registerColumnMapper(UserMapper())

    return this
}