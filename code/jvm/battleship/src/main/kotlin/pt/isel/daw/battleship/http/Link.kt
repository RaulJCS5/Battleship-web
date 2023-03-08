package pt.isel.daw.battleship.http

import pt.isel.daw.battleship.infra.LinkRelation
import java.net.URI

class Link(uri: URI, linkRelation: LinkRelation) {

    var href: URI
    var rel: LinkRelation

    init {
        href = uri
        rel = linkRelation
    }
}