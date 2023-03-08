package pt.isel.daw.battleship.http.pipeline

import com.google.gson.Gson
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.daw.battleship.domain.User
import pt.isel.daw.battleship.http.model.ProblemOutputModel
import pt.isel.daw.battleship.utils.StringHelper
import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationInterceptor(
    private val authorizationHeaderProcessor: AuthorizationHeaderProcessor,
    private val cookieProcessor: CookieProcessor
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.methodParameters.any { it.parameterType == User::class.java }) {
            // enforce authentication
            var user = authorizationHeaderProcessor.process(request.getHeader(NAME_AUTHORIZATION_HEADER))

            //if no exists authorization, try Cookie header
            //to accept the two types of auth
            if (user == null) {
                user = cookieProcessor.process(readCookieFromHeader(request))
            }

            return if (user == null) {
                val invalidTokenProblem = getInvalidTokenProblem()
                val gson = Gson()
                val invalidToken: String = gson.toJson(invalidTokenProblem)
                response.status = 401
                response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, AuthorizationHeaderProcessor.SCHEME)
                response.contentType = MEDIA_TYPE;
                response.writer.write(invalidToken)
                false
            } else {
                UserArgumentResolver.addUserTo(user, request)
                true
            }
        }
        return true
    }

    companion object {
        private const val NAME_AUTHORIZATION_HEADER = "Authorization"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
        const val MEDIA_TYPE = "application/problem+json"
    }

    //Aux to get invalid token problem
    private fun getInvalidTokenProblem(): ProblemOutputModel {
        return ProblemOutputModel(
            URI(
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/" +
                        "docs/problems/invalid-token-auth"
            ),
            "Invalid user token!",
            "Invalid user token! Current path needs valid auth token!"
        )
    }

    //get cookie header with login info
    fun readCookieFromHeader(request: HttpServletRequest): String? {
        request.cookies?.forEach {
            if (it.name.equals(StringHelper.cookieUserKey())) {
                return it.value
            }
        }
        return null
    }
}