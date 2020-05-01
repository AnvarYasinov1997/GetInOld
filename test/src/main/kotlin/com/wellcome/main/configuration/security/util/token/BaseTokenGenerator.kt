package com.wellcome.main.configuration.security.util.token

import com.wellcome.main.configuration.security.dto.PairTokenDto
import com.wellcome.main.configuration.security.dto.TokenDto
import com.wellcome.main.configuration.security.model.ProfileModel
import com.wellcome.main.configuration.security.model.UserContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

interface TokenGenerator {
    fun generatePairToken(userContext: UserContext): PairTokenDto
    fun generatePairToken(userName: String, institutionProfiles: List<ProfileModel>): TokenDto
}

@Component
open class BaseTokenGenerator @Autowired constructor(
    private val tokenFactory: TokenFactory
) : TokenGenerator {

    override fun generatePairToken(userContext: UserContext): PairTokenDto {
        return PairTokenDto(
            tokenFactory.createAccessJwtToken(userContext).getToken(),
            tokenFactory.createRefreshToken(userContext).getToken()
        )
    }

    override fun generatePairToken(userName: String, institutionProfiles: List<ProfileModel>): TokenDto {
        return TokenDto(tokenFactory.createCustomerAccessToken(userName, institutionProfiles).getToken())
    }
}
