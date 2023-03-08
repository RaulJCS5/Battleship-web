package pt.isel.daw.battleship.repository

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}