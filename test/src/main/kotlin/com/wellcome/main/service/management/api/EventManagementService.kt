package com.wellcome.main.service.management.api

import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.api.newDto.response.v1.EventResponseV1
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.service.extentions.generators.api.common.generateBlockEventDtoV1List
import com.wellcome.main.service.extentions.generators.api.common.generateEventDtoV1List
import com.wellcome.main.service.extentions.management.filterFutureWeek
import com.wellcome.main.service.extentions.management.filterNotFutureWeek
import com.wellcome.main.service.extentions.management.sortByDayOfWeeks
import com.wellcome.main.service.interceptor.StoreReviewInterceptorService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.getDay
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.persistence.EntityNotFoundException

interface EventManagementService {
    fun getEvents(reviewing: Boolean, localityId: Long, googleUid: String?): EventResponseV1
}

@Service
open class DefaultEventManagementService @Autowired constructor(
    private val userService: UserService,
    private val timestampProvider: TimestampProvider,
    private val institutionService: InstitutionService,
    private val userInterceptorService: UserInterceptorService,
    private val storeReviewInterceptorService: StoreReviewInterceptorService
) : EventManagementService {

    @Transactional
    override fun getEvents(reviewing: Boolean, localityId: Long, googleUid: String?): EventResponseV1 {
        val user = googleUid?.let {
            userService.findByGoogleUid(googleUid)
                ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        }

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val events = institutionService.findByLocalityForEvents(5)
            .filter { it.events.isNotEmpty() }
            .map(Institution::getWorkingEvents)
            .flatMap(List<InstitutionEvent>::toList)

        val promotedEvents = events
            .filter(InstitutionEvent::promoted)
            .sortedBy { LocalDate.parse(it.date) }

        val currentEvents = events
            .filterFutureWeek(userZonedDateTime)
            .sortByDayOfWeeks()

        val futureEvents = events
            .filterNotFutureWeek(userZonedDateTime)
            .sortedBy { LocalDate.parse(it.date) }

        val promotedEventWrapper = promotedEvents
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideEvent(user, it) else it }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenEvents(it) else it }
            .let { wrappers ->
                if (reviewing) {
                    return@let if (reviewing) wrappers.filter { wrapper ->
                        (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                    } else wrappers
                } else wrappers
            }

        val currentEventWrappers = currentEvents
            .map { entry -> entry.key to entry.value.map { EntityWrapper(it, mutableListOf()) } }.toMap()
            .map { entry -> entry.key to entry.value.let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideEvent(user, it) else it } }.toMap()
            .map { entry -> entry.key to entry.value.let { if (reviewing) storeReviewInterceptorService.handleForbiddenEvents(it) else it } }.toMap()
            .let { eventsMap ->
                if (reviewing) {
                    return@let eventsMap.map { entry ->
                        entry.key to entry.value.filter { wrapper ->
                            (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                        }
                    }.toMap()
                } else eventsMap
            }


        val futureEventWrapper = futureEvents
            .map { EntityWrapper(it, mutableListOf()) }
            .let { if (user != null) userInterceptorService.handleSavedInstitutionsInsideEvent(user, it) else it }
            .let { if (reviewing) storeReviewInterceptorService.handleForbiddenEvents(it) else it }
            .let { wrappers ->
                if (reviewing) {
                    return@let if (reviewing) wrappers.filter { wrapper ->
                        (wrapper.delegates.first { it is Delegate.StoreReviewDelegate } as Delegate.StoreReviewDelegate).allowed
                    } else wrappers
                } else wrappers
            }

        val dayOfWeek = getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)

        return EventResponseV1(
            promotedEvents = promotedEventWrapper.generateEventDtoV1List(dayOfWeek),
            currentEvents = currentEventWrappers.generateBlockEventDtoV1List(dayOfWeek),
            futureEvents = futureEventWrapper.generateEventDtoV1List(dayOfWeek)
        )
    }

}