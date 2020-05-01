package com.wellcome.main.aop

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.configuration.security.model.ProfileModel
import com.wellcome.main.configuration.utils.ThreadCache
import com.wellcome.main.dto.api.paths.QueryString
import com.wellcome.main.exception.UnauthorizedException
import com.wellcome.main.service.facade.LocalityService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.functions.getMessage
import com.wellcome.main.util.functions.getProfileContext
import com.wellcome.main.util.functions.getQueryString
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
open class RestControllerProxyProcessor @Autowired constructor(
    private val threadCache: ThreadCache,
    private val loggerService: LoggerService,
    private val localityService: LocalityService,
    private val timestampProvider: TimestampProvider
) {

    @Before(value = "@within(com.wellcome.main.annotations.PreProcessor) || @annotation(com.wellcome.main.annotations.PreProcessor)")
    open fun profileModelValidationPreProcessor(joinPoint: JoinPoint) {
        val context = getProfileContext()
        if (context == null || context.profileModels.isEmpty())
            throw UnauthorizedException("Profile context is empty")
        val institutionId = getQueryString(QueryString.INSTITUTION_ID)?.toLong()
        if (institutionId != null)
            if (!context.profileModels.map(ProfileModel::institutionId).contains(institutionId))
                throw UnauthorizedException("User has not permission for access for institution width id: $institutionId").also {
                    loggerService.sendLogDeveloper(LogMessage(it.getMessage()))
                }
    }

    @Before(value = "@within(org.springframework.web.bind.annotation.RestController) || @annotation(org.springframework.web.bind.annotation.RestController)")
    open fun threadCachingLocalityPreProcessor(joinPoint: JoinPoint) {
        val locality = localityService.findById(5) // id mock for bishkek TODO()
        val userZonedDateTime =
            timestampProvider.getUserZonedDateTimeByTimeZoneId(locality.timezone)
        threadCache.getUserZonedDateTimeRequestThreadLocal().set(userZonedDateTime)
        threadCache.getLocalityIdRequestThreadLocal().set(locality.id)
    }

    @After(value = "@within(org.springframework.web.bind.annotation.RestController) || @annotation(org.springframework.web.bind.annotation.RestController)")
    open fun threadCachingLocalityPostProcessor(joinPoint: JoinPoint) {
        threadCache.getUserZonedDateTimeRequestThreadLocal().remove()
        threadCache.getLocalityIdRequestThreadLocal().remove()
    }

}