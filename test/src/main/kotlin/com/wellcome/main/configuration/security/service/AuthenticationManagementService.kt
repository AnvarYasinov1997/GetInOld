package com.wellcome.main.configuration.security.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.component.InitResult
import com.wellcome.main.configuration.security.dto.AuthorizeInstitutionResponseV1
import com.wellcome.main.configuration.security.dto.PairTokenDto
import com.wellcome.main.configuration.security.model.ProfileModel
import com.wellcome.main.configuration.security.model.RawAccessJwtToken
import com.wellcome.main.configuration.security.model.Scopes
import com.wellcome.main.configuration.security.model.UserContext
import com.wellcome.main.configuration.security.property.AuthenticationJwtProperty
import com.wellcome.main.configuration.security.util.token.TokenGenerator
import com.wellcome.main.dto.InitResponse
import com.wellcome.main.entity.Locality
import com.wellcome.main.entity.user.User
import com.wellcome.main.exception.InvalidJwtToken
import com.wellcome.main.exception.InvalidPasswordException
import com.wellcome.main.exception.MapsRepositoryException
import com.wellcome.main.model.LocalityFound
import com.wellcome.main.repository.remote.maps.MapsRepository
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1
import com.wellcome.main.service.facade.institution.InstitutionProfileService
import com.wellcome.main.service.facade.LocalityService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.functions.generateLocalityTopic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface AuthenticationManagementService {
    fun authorizeInstitution(googleUid: String,
                             login: String,
                             institutionAccessKey: String,
                             oldToken: String?): AuthorizeInstitutionResponseV1

    fun refreshToken(body: String): PairTokenDto
    fun verify(token: String): UserVerified
    fun initUser(token: String): UserVerified
    fun init(lat: Double, lon: Double, token: String): InitResponse
}

@Service
open class DefaultAuthenticationManagementService @Autowired constructor(
    private val userService: UserService,
    private val objectMapper: ObjectMapper,
    private val loggerService: LoggerService,
    private val mapsRepository: MapsRepository,
    private val tokenGenerator: TokenGenerator,
    private val localityService: LocalityService,
    private val passwordEncoder: PasswordEncoder,
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val authenticationJwtProperty: AuthenticationJwtProperty,
    private val institutionProfileService: InstitutionProfileService
) : AuthenticationManagementService {

    @Transactional(readOnly = true)
    override fun verify(token: String): UserVerified {
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)

        val user = userService.findByGoogleUid(initResult.googleUid)
            ?: throw EntityNotFoundException("User with google uid: ${initResult.googleUid} is not found to database")

        return UserVerified(
            userId = user.id!!,
            localityId = user.locality.id!!,
            email = user.email ?: "",
            username = user.username,
            isBlocked = user.blocked,
            authority = user.authorities.toList()
        )
    }

    @Transactional
    override fun authorizeInstitution(googleUid: String,
                                      login: String,
                                      institutionAccessKey: String,
                                      oldToken: String?): AuthorizeInstitutionResponseV1 {

        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is not found to database")

        val profileModels = oldToken
            ?.let(::RawAccessJwtToken)
            ?.parseClaims(authenticationJwtProperty.tokenSigningKey)
            ?.let { it.body["profiles"] as List<*> }
            ?.mapNotNull { profile ->
                profile as Map<*, *>
                if ((profile["login"] as String) == login) return@mapNotNull null
                else ProfileModel(
                    login = profile["login"] as String,
                    accessKey = profile["accessKey"] as String,
                    institutionId = (profile["institutionId"] as Int).toLong(),
                    institutionProfileId = (profile["institutionProfileId"] as Int).toLong(),
                    authorities = (profile["authorities"] as List<*>).map { it as String }
                )
            }?.toMutableList() ?: mutableListOf()

        val institutionProfile = institutionProfileService.findByLogin(login)

        if (!passwordEncoder.matches(institutionAccessKey, institutionProfile.accessKey))
            throw InvalidPasswordException("Authentication Failed. Invalid username or password.")

        ProfileModel(
            login = institutionProfile.login,
            accessKey = institutionAccessKey,
            authorities = emptyList(),
            institutionId = institutionProfile.institution.id.let(::requireNotNull),
            institutionProfileId = institutionProfile.id.let(::requireNotNull)
        ).let(profileModels::add)

        val tokenDto = tokenGenerator.generatePairToken(user.name, profileModels.toList())

        return AuthorizeInstitutionResponseV1(
            token = tokenDto.token,
            institutionDto = institutionProfile.institution.generateInstitutionDtoV1()
        )
    }


    @Transactional
    override fun refreshToken(body: String): PairTokenDto {
        val data = objectMapper.readValue(body, Map::class.java)

        val refreshTokenPayload = data["refreshToken"].toString()

        val rawRefreshToken = RawAccessJwtToken(refreshTokenPayload)

        val jwsClaims = rawRefreshToken.parseClaims(authenticationJwtProperty.tokenSigningKey)

        if (jwsClaims.body["claims"] != Scopes.REFRESH_TOKEN.authority())
            throw InvalidJwtToken()

        val username = jwsClaims.body.subject

        val user = userService.findByEmail(username)
            ?: throw EntityNotFoundException("User with email $username is not found to database")

        val userContext = UserContext.create(user.id!!, "", user.email ?: "", null, user.authorities)

        return tokenGenerator.generatePairToken(userContext)
    }

    @Transactional
    override fun initUser(token: String): UserVerified {
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        return User(
            googleUid = initResult.googleUid,
            name = initResult.username,
            email = initResult.email,
            photoUrl = initResult.photoUrl,
            locality = localityService.findById(5)
        ).let(userService::saveOrUpdate).let {
            UserVerified(
                userId = it.id!!,
                localityId = it.locality.id!!,
                email = it.email ?: "",
                username = it.username,
                isBlocked = it.blocked,
                authority = it.authorities.toList()
            )
        }
    }

    @Transactional
    override fun init(lat: Double, lon: Double, token: String): InitResponse {
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)

        val localityMessage = mapsRepository.findLocality(lat, lon)
            as? LocalityFound ?: throw MapsRepositoryException("Locality with lat: $lat lon: $lon is not found")

        val user = userService.findByGoogleUid(initResult.googleUid)

        val locality = localityService.findByName(localityMessage.locality)

        val topic: String = generateLocalityTopic(localityMessage.locality)

        return when {
            user == null && locality == null -> {
                loggerService.info(LogMessage("Locality and user empty $initResult"))
                createUserCreateLocality(localityMessage, initResult, topic)
            }
            user != null && locality != null -> {
                loggerService.info(LogMessage("Locality and user exists $initResult"))
                updateUser(localityMessage, locality, user)
            }
            user != null && locality == null -> {
                loggerService.info(LogMessage("Empty locality, user exists $initResult"))
                createLocalityUpdateUser(localityMessage, user, topic)
            }
            user == null && locality != null -> {
                loggerService.info(LogMessage("Empty user, locality exists $initResult"))
                createUser(initResult, locality)
            }
            else -> {
                loggerService.error(LogMessage("Zero case $initResult"))
                throw Exception("User is not initialized")
            }
        }
    }

    private fun createUserCreateLocality(localityMessage: LocalityFound, initResult: InitResult, topic: String): InitResponse {
        val savedLocality = Locality(
            name = localityMessage.locality,
            timezone = localityMessage.timezoneId,
            topic = topic
        ).let(localityService::saveOrUpdate)

        val savedUser = User(
            googleUid = initResult.googleUid,
            name = initResult.username,
            email = initResult.email,
            photoUrl = initResult.photoUrl,
            locality = savedLocality
        ).let(userService::saveOrUpdate)

        return InitResponse(
            userId = savedUser.id!!,
            googleUid = savedUser.googleUid,
            localityName = savedUser.locality.name,
            topic = topic,
            photoUrl = savedUser.photoUrl,
            name = savedUser.name)
    }

    private fun updateUser(localityMessage: LocalityFound, locality: Locality, user: User): InitResponse {
        if (user.locality.name != localityMessage.locality) {
            userService.findById(user.id!!).apply {
                this.locality = locality
            }.let(userService::saveOrUpdate)
        }

        return InitResponse(
            userId = user.id!!,
            googleUid = user.googleUid,
            localityName = locality.name,
            topic = locality.topic,
            name = user.name,
            photoUrl = user.photoUrl)
    }

    private fun createLocalityUpdateUser(localityMessage: LocalityFound, user: User, topic: String): InitResponse {
        val savedLocality = Locality(
            name = localityMessage.locality,
            timezone = localityMessage.timezoneId,
            topic = topic
        ).let(localityService::saveOrUpdate)

        val updatedUser = userService.findById(user.id!!).apply {
            this.locality = savedLocality
        }.let(userService::saveOrUpdate)

        return InitResponse(
            userId = updatedUser.id!!,
            googleUid = updatedUser.googleUid,
            localityName = updatedUser.locality.name,
            topic = topic,
            photoUrl = user.photoUrl,
            name = updatedUser.name)
    }

    private fun createUser(initResult: InitResult, locality: Locality): InitResponse {
        val savedUser = User(
            googleUid = initResult.googleUid,
            name = initResult.username,
            email = initResult.email,
            photoUrl = initResult.photoUrl,
            locality = locality
        ).let(userService::saveOrUpdate)

        return InitResponse(
            userId = savedUser.id!!,
            googleUid = savedUser.googleUid,
            localityName = savedUser.locality.name,
            topic = locality.topic,
            name = savedUser.name,
            photoUrl = savedUser.photoUrl)
    }

}

data class UserVerified(val userId: Long,
                        val localityId: Long,
                        val username: String,
                        val email: String,
                        val isBlocked: Boolean,
                        val authority: List<GrantedAuthority>)