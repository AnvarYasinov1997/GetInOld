package com.wellcome.main.configuration.security.auth.token.providers

import com.wellcome.main.configuration.security.config.JwtAuthenticationToken
import com.wellcome.main.configuration.security.config.ModerationJwtAuthenticationToken
import com.wellcome.main.configuration.security.model.ProfileContext
import com.wellcome.main.configuration.security.model.ProfileModel
import com.wellcome.main.configuration.security.model.RawAccessJwtToken
import com.wellcome.main.configuration.security.property.AuthenticationJwtProperty
import com.wellcome.main.exception.InvalidPasswordException
import com.wellcome.main.exception.InvalidUsernameException
import com.wellcome.main.service.facade.institution.InstitutionProfileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service(value = "moderation-jwt")
open class ModerationJwtAuthenticationProvider @Autowired constructor(
    private val passwordEncoder: PasswordEncoder,
    private val institutionProfileService: InstitutionProfileService,
    private val authenticationJwtProperty: AuthenticationJwtProperty
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val rawAccessJwtToken = authentication.credentials as RawAccessJwtToken

        if (rawAccessJwtToken.getToken().isEmpty())
            return JwtAuthenticationToken(ProfileContext(emptyList()), rawAccessJwtToken, emptyList())

        val jwsClaims = rawAccessJwtToken.parseClaims(authenticationJwtProperty.tokenSigningKey)

        val profileContext = (jwsClaims.body["profiles"] as List<*>)
            .map { profile ->
                profile as Map<*, *>
                ProfileModel(
                    login = profile["login"] as String,
                    accessKey = profile["accessKey"] as String,
                    institutionProfileId = (profile["institutionProfileId"] as Int).toLong(),
                    institutionId = (profile["institutionId"] as Int).toLong(),
                    authorities = (profile["authorities"] as List<*>).map { it as String }
                )
            }.let(::ProfileContext)

        profileContext.profileModels.forEach(this::validateCredentials)

//        val authorities: List<GrantedAuthority> = scopes.map { SimpleGrantedAuthority(it.toString()) }

        return JwtAuthenticationToken(profileContext, rawAccessJwtToken, emptyList())
    }

    override fun supports(authentication: Class<*>): Boolean {
        return ModerationJwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

    private fun validateCredentials(model: ProfileModel) {
        val institutionProfile = try {
            institutionProfileService.findByLogin(model.login)
        } catch (e: EntityNotFoundException) {
            throw InvalidUsernameException("Authentication Failed. Invalid username or password.")
        }

        if (!passwordEncoder.matches(model.accessKey, institutionProfile.accessKey))
            throw InvalidPasswordException("Authentication Failed. Invalid username or password.")
    }

}