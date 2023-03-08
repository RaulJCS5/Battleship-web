package pt.isel.daw.battleship.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.http.Relations
import pt.isel.daw.battleship.http.Uris
import pt.isel.daw.battleship.http.model.HomeOutputModel
import pt.isel.daw.battleship.infra.siren
import pt.isel.daw.battleship.utils.StringHelper

@RestController
class HomeController {

    //Get API Home
    @GetMapping(Uris.HOME)
    fun getHome(): ResponseEntity<*> {
        val ls = HomeOutputModel(StringHelper.getHomeCredits())

        return ResponseEntity.status(200)
            .header("Content-Type", UserController.mediaTypeJsonSiren)
            .body(siren(ls) {
                clazz("home")
                properties
                link(Uris.getHome(), Relations.SELF)
                link(Uris.Users.getRegisterUser(), Relations.REGISTER)
                link(Uris.Users.getLogin(), Relations.LOGIN)
                link(Uris.getPublic(), Relations.PUBLIC)
            })
    }
}