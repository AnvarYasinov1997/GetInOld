package com.wellcome.main.configuration.security.auth.login

import com.fasterxml.jackson.databind.ObjectMapper
import com.wellcome.main.configuration.security.dto.LoginRequest
import com.wellcome.main.exception.AuthMethodNotSupportedException
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LoginAuthenticationProcessingFilter(
    defaultProcessUrl: String,
    private val objectMapper: ObjectMapper,
    internal val successHandler: AuthenticationSuccessHandler,
    internal val failureHandler: AuthenticationFailureHandler
) : AbstractAuthenticationProcessingFilter(defaultProcessUrl) {

    @Throws(AuthenticationException::class, IOException::class)
    override fun attemptAuthentication(request: HttpServletRequest,
                                       response: HttpServletResponse): Authentication {
        if (HttpMethod.POST.name != request.method)
            throw AuthMethodNotSupportedException("Authentication method not supported")
        val loginRequest = objectMapper.readValue(request.reader, LoginRequest::class.java)

        val token = UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)

        return authenticationManager.authenticate(token)
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(request: HttpServletRequest,
                                          response: HttpServletResponse,
                                          chain: FilterChain?,
                                          authResult: Authentication) {
        successHandler.onAuthenticationSuccess(request, response, authResult)
    }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(request: HttpServletRequest,
                                            response: HttpServletResponse,
                                            failed: AuthenticationException) {
        SecurityContextHolder.clearContext()
        failureHandler.onAuthenticationFailure(request, response, failed)
    }

}