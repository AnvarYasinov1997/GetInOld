package com.wellcome.main.exception

import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.AuthenticationException

class FirebaseAccessTokenException(message: String) : RuntimeException(message)
class FirebaseRepositoryException(message: String) : RuntimeException(message)
class FoursquareApiException(message: String) : RuntimeException(message)
class InstitutionExistException(message: String) : RuntimeException(message)
class LanguageUnsupportedException(message: String) : RuntimeException(message)
class MapsRepositoryException(message: String) : RuntimeException(message)
class PreprocessException(message: String) : RuntimeException(message)
class InvalidPasswordException(message: String) : AuthenticationException(message)
class InvalidUsernameException(message: String) : AuthenticationException(message)
class UnauthorizedException(message: String) : RuntimeException(message)
class GoogleTokenParseException(message: String) : RuntimeException(message)
class AuthMethodNotSupportedException(message: String) : AuthenticationServiceException(message)
class InvalidJwtToken : RuntimeException()
class InstitutionOfferCreationException(message: String) : RuntimeException(message)
class ConcurrentDuplicateUniqueEntityException(message: String) : RuntimeException(message)
class BirthdayCampaignPatternExistException(message: String) : RuntimeException(message)
class JwtExpiredTokenException : AuthenticationException {

    constructor(message: String) : super(message)

    constructor(message: String, t: Throwable) : super(message, t)

}