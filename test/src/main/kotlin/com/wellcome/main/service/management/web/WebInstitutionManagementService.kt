package com.wellcome.main.service.management.web

import com.wellcome.main.component.PersistentContextProvider
import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.web.response.InstitutionProfileResponse
import com.wellcome.main.service.cache.InstitutionCacheService
import com.wellcome.main.service.extentions.generators.web.generateInstitutionProfileResponse
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.service.interceptor.WorkTimeInterceptorService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.getDay
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface WebInstitutionManagementService {
    fun getProfile(institutionId: Long, googleUid: String?): InstitutionProfileResponse
}

@Service
open class DefaultWebInstitutionManagementService @Autowired constructor(
    private val userService: UserService,
    private val timestampProvider: TimestampProvider,
    private val userInterceptorService: UserInterceptorService,
    private val institutionCacheService: InstitutionCacheService,
    private val persistentContextProvider: PersistentContextProvider,
    private val workTimeInterceptorService: WorkTimeInterceptorService
) : WebInstitutionManagementService {

    @Transactional(readOnly = true)
    override fun getProfile(institutionId: Long, googleUid: String?): InstitutionProfileResponse {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        persistentContextProvider.refreshCache()

        val institution = institutionCacheService.findById(institutionId)

        val institutionWrapper = mutableListOf(institution)
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutions(user, it) else it }
            .let { workTimeInterceptorService.handleWorkTimeInstitutions(SearchInstitutionDays.NOW.name, userZonedDateTime, it) }
            .map { wrapper ->
                wrapper.entity.reviews = institution.reviews.sortedByDescending { it.feedback.length }.toMutableList()
                return@map wrapper
            }.first()

        return institutionWrapper.generateInstitutionProfileResponse(
            dayOfWeek = getDay(SearchInstitutionDays.NOW.name, userZonedDateTime),
            userId = user?.id
        )
    }

}