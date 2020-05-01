package com.wellcome.main.configuration.security.auth.login

import com.wellcome.main.configuration.security.model.UserContext
import com.wellcome.main.entity.user.User
import com.wellcome.main.exception.InvalidPasswordException
import com.wellcome.main.exception.InvalidUsernameException
import com.wellcome.main.service.facade.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@Component(value = "login")
open class LoginAuthenticationProvider @Autowired constructor(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        Assert.notNull(authentication, "No authentication data provided")

        val username = authentication.principal as String

        val password = authentication.credentials as String

        val user = validateAndAuthenticateCredentials(username, password)

        val userContext = UserContext.create(user.id!!, "", user.email ?: "", null, user.authorities)

        return UsernamePasswordAuthenticationToken(userContext, null, userContext.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

    private fun validateAndAuthenticateCredentials(username: String, password: String): User {
        val user = userService.findByEmail(username)
            ?: throw InvalidUsernameException("Authentication Failed. Invalid username or password.")

        if (user.authorities.isEmpty())
            throw AccessDeniedException("User has no permissions assigned")

        if (!passwordEncoder.matches(password, user.password))
            throw InvalidPasswordException("Authentication Failed. Invalid username or password.")

        return user
    }

}