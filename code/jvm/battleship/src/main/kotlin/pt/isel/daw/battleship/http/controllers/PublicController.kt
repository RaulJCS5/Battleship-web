package pt.isel.daw.battleship.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.http.Relations
import pt.isel.daw.battleship.http.Uris
import pt.isel.daw.battleship.http.model.PublicOutputModel
import pt.isel.daw.battleship.infra.siren
import pt.isel.daw.battleship.utils.StringHelper

@RestController
class PublicController {

    @GetMapping(Uris.PUBLIC)
    fun getPublic(): ResponseEntity<*> {
        val ls = PublicOutputModel(StringHelper.getPublicVersion(), StringHelper.getPublicAuthors())

        return ResponseEntity.status(200)
            .header("Content-Type", UserController.mediaTypeJsonSiren)
            .body(siren(ls) {
                clazz("public")
                properties
                link(Uris.getPublic(), Relations.SELF)
                link(Uris.getHome(), Relations.HOME)
                link(Uris.getRanking(), Relations.LEADER_BOARD)
            })
    }
}