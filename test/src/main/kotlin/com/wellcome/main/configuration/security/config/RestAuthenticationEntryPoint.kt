package com.wellcome.main.configuration.security.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration(value = "rest")
open class RestAuthenticationEntryPoint : AuthenticationEntryPoint {

    @Throws(IOException::class)
    override fun commence(request: HttpServletRequest,
                          response: HttpServletResponse,
                          authException: AuthenticationException) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized")
    }

}