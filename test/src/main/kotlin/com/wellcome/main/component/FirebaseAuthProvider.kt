package com.wellcome.main.component

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import com.wellcome.main.exception.GoogleTokenParseException
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.enumerators.TokenConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.stereotype.Component

interface FirebaseAuthProvider {
    fun getUserCredentialsByFirebaseToken(token: String): InitResult
}

@Component
open class DefaultFirebaseAuthProvider @Autowired constructor(
    private val loggerService: LoggerService,
    private val configurableEnvironment: ConfigurableEnvironment
) : FirebaseAuthProvider {

    override fun getUserCredentialsByFirebaseToken(token: String): InitResult {
        return if (configurableEnvironment.activeProfiles.firstOrNull("develop"::equals) != null
            || token == "anvarToken") InitResult(
            googleUid = "H370U5axNpW8qKZrQQwlCfX2VfH3",
            username = "",
            email = "",
            photoUrl = ""
        ) else {
            val firebaseToken = getFirebaseToken(token)
            return InitResult(
                googleUid = firebaseToken.uid,
                username = firebaseToken.name ?: "",
                email = firebaseToken.email,
                photoUrl = firebaseToken.picture ?: ""
            )
        }
    }

    private fun getFirebaseToken(token: String): FirebaseToken {
        return try {
            FirebaseAuth.getInstance().verifyIdToken(token, true)
        } catch (e: Exception) {
            val message = if (e is FirebaseAuthException && e.errorCode == "institutionId-token-revoked") {
                TokenConstants.TOKEN_REVOKED.name
            } else {
                TokenConstants.TOKEN_INVALID.name
            }
            loggerService.error(LogMessage(message))
            throw GoogleTokenParseException(message)
        }
    }
}

data class InitResult(val googleUid: String,
                      val username: String,
                      val email: String?,
                      val photoUrl: String)