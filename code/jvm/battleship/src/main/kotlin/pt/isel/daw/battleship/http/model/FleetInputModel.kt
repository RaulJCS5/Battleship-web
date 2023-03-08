package pt.isel.daw.battleship.http.model

import pt.isel.daw.battleship.domain.fleet.Position

data class SetFleetInputModel(
    val shipType: String,
    val shipLayout: String,
    val referencePoint: Position
)

data class MyBoardModel(
    val myBoard: Boolean
)

