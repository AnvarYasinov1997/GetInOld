package com.wellcome.main.configuration.security.auth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.wellcome.main.configuration.security.model.UserContext
import com.wellcome.main.configuration.security.util.token.JwtTokenFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
open class AwareAuthenticationSuccessHandler @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val tokenFactory: JwtTokenFactory
) : AuthenticationSuccessHandler {

    @Throws(IOException::class)
    override fun onAuthenticationSuccess(request: HttpServletRequest,
                                         response: HttpServletResponse,
                                         authentication: Authentication) {
        val userContext = authentication.principal as UserContext

        val accessToken = tokenFactory.createAccessJwtToken(userContext)

        val refreshToken = tokenFactory.createRefreshToken(userContext)

        val tokenMap = mutableMapOf<String, String>()

        tokenMap["token"] = accessToken.getToken()

        tokenMap["refreshToken"] = refreshToken.getToken()

        response.status = HttpStatus.OK.value()

        response.contentType = MediaType.APPLICATION_JSON_VALUE

        objectMapper.writeValue(response.writer, tokenMap)

        clearAuthenticationAttributes(request)
    }

    private fun clearAuthenticationAttributes(request: HttpServletRequest) {
        val session = request.getSession(false) ?: return

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)
    }

}