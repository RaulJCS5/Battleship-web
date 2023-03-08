package pt.isel.daw.battleship.domain.fleet

data class Position(
    val row: Int,
    val col: Int,
) {
    //this is mandatory
    init {
        require(row in 0..9 && col in 0..9)
    }
}