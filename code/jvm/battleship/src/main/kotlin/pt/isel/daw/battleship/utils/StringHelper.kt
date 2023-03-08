package pt.isel.daw.battleship.utils

class StringHelper {
    companion object {
        fun getDatabaseString(): String = "jdbc:postgresql://host.docker.internal:5432/battleship?user=postgres&password=group05"
        fun getHomeCredits(): String = "BattleShip API, Made by L51N G05 group! Check our public page"
        fun getPublicVersion(): String = "0.1.0 (phase 1)"
        fun getPublicAuthors(): String = "Tiago Duarte (42525); Raul Santos(44806);"
        fun cookieUserKey():String = "user-auth"
    }
}