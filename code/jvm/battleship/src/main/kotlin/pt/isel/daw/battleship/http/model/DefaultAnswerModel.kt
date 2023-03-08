package pt.isel.daw.battleship.http.model

import com.fasterxml.jackson.annotation.JsonInclude

class DefaultAnswerModel {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var error: ProblemOutputModel? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var message: String? = null
}