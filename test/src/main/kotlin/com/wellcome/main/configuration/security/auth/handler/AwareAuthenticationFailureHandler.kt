package com.wellcome.main.configuration.security.auth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.wellcome.main.dto.ErrorResponse
import com.wellcome.main.exception.AuthMethodNotSupportedException
import com.wellcome.main.exception.InvalidPasswordException
import com.wellcome.main.exception.JwtExpiredTokenException
import com.wellcome.main.util.functions.getMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
open class AwareAuthenticationFailureHandler @Autowired constructor(
    private val objectMapper: ObjectMapper
) : AuthenticationFailureHandler {

    @Throws(IOException::class)
    override fun onAuthenticationFailure(request: HttpServletRequest,
                                         response: HttpServletResponse,
                                         e: AuthenticationException) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        when (e) {
            is BadCredentialsException -> objectMapper.writeValue(response.writer, ErrorResponse(e.getMessage()))
            is JwtExpiredTokenException -> objectMapper.writeValue(response.writer, ErrorResponse("Token has expired"))
            is AuthMethodNotSupportedException -> objectMapper.writeValue(response.writer, ErrorResponse(e.getMessage()))
            is CredentialsExpiredException -> objectMapper.writeValue(response.writer, ErrorResponse(e.getMessage()))
            is InvalidPasswordException -> objectMapper.writeValue(response.writer, ErrorResponse(e.getMessage()))
            else -> objectMapper.writeValue(response.writer, ErrorResponse("Authentication failed"))
        }
    }

}