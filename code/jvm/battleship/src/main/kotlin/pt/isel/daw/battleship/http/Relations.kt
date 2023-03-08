package pt.isel.daw.battleship.http

import pt.isel.daw.battleship.infra.LinkRelation

object Relations {

    val SELF = LinkRelation("self")

    val HOME = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/home"
    )
    val LOGIN = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/login"
    )
    val REGISTER = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/register"
    )

    val LEADER_BOARD = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/leaderBoard"
    )

    val ME = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/me"
    )

    val PUBLIC = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/public"
    )

    val CREATEGAME = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/creategame"
    )

    val SETINLOBBY = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/setinlobby"
    )

    val SETLAYOUT = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/setlayout"
    )

    val SETSHOTS = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/setshots"
    )

    val FLEETSTATE = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/fleetstate"
    )

    val OPPONENTFLEET = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/opponentfleet"
    )

    val OVERALLSTATE = LinkRelation(
        "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                "docs/rels/overallstate"
    )
}