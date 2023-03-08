package pt.isel.daw.battleship.http.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Min

data class HomeMeInputModel(
    @get:Min(1)
    val id: Int
)

@RestController
@RequestMapping("/tests")
class TestController {

    @GetMapping("/", produces = ["application/json"])
    fun getHome() = "Home Test path!"

    //Example 1
    @GetMapping("/me/{id}", produces = ["application/json"])
    fun getHomeMe(@PathVariable id: Int) = "Home of user $id!"

    //Example 2
    @GetMapping("/me2/", produces = ["application/json"])
    fun getHomeMe2(@Valid @RequestBody input: HomeMeInputModel) = "Home of user " + input.id + "!"

    //Example 3
    @GetMapping("/me3/", produces = ["application/json"])
    fun getHomeMe3(@Valid @RequestBody input: HomeMeInputModel) = ResponseEntity
        .status(200)
        .contentType(MediaType.parseMediaType("application/json"))
        .body(HomeMeInputModel(input.id))
}