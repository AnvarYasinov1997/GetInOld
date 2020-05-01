package com.wellcome.main.configuration.security.auth.token

import com.wellcome.main.configuration.security.config.ModerationJwtAuthenticationToken
import com.wellcome.main.configuration.security.model.RawAccessJwtToken
import com.wellcome.main.configuration.security.util.SecurityPaths
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.util.matcher.RequestMatcher
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ModerationTokenAuthenticationProcessingFilter(
    matcher: RequestMatcher,
    internal val failureHandler: AuthenticationFailureHandler
) : AbstractAuthenticationProcessingFilter(matcher) {

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest,
                                       response: HttpServletResponse): Authentication {
        val moderationJwtToken = request.getHeader(SecurityPaths.MODERATION_JWT_TOKEN_HEADER_PARAM)
        if(moderationJwtToken != null) {
            val token = RawAccessJwtToken(moderationJwtToken)
            return authenticationManager.authenticate(ModerationJwtAuthenticationToken(token))
        }
        throw AuthenticationServiceException("Authentication header can not be blank")
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(request: HttpServletRequest,
                                          response: HttpServletResponse,
                                          chain: FilterChain?,
                                          authResult: Authentication) {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authResult
        SecurityContextHolder.setContext(context)
        chain!!.doFilter(request, response)
    }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(request: HttpServletRequest,
                                            response: HttpServletResponse,
                                            failed: AuthenticationException) {
        SecurityContextHolder.clearContext()
        failureHandler.onAuthenticationFailure(request, response, failed)
    }

}