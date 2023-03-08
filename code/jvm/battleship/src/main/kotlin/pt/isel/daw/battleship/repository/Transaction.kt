package pt.isel.daw.battleship.repository

interface Transaction {

    val usersRepository: UsersRepository
    val gamesRepository: GamesRepository

    fun rollback()
    fun commit();
}