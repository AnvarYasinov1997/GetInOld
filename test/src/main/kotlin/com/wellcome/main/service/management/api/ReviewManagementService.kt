package com.wellcome.main.service.management.api

import com.wellcome.main.annotations.ReloadCache
import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.api.newDto.response.v1.ReviewActionResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.ReviewResponseV1
import com.wellcome.main.dto.api.newDto.response.v2.ReviewActionResponseV2
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionReview
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1
import com.wellcome.main.service.extentions.generators.api.common.generateReviewDtoListV1
import com.wellcome.main.service.extentions.generators.api.common.generateReviewDtoV1
import com.wellcome.main.service.extentions.generators.api.common.v2.generateReviewDtoV2
import com.wellcome.main.service.facade.ApplicationConfigService
import com.wellcome.main.service.facade.institution.InstitutionReviewService
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.getDay
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface ReviewManagementService {
    fun saveReview(institutionId: Long, starCount: Int, feedback: String, googleUid: String): ReviewActionResponseV1
    fun saveReviewV2(institutionId: Long, starCount: Int, feedback: String, googleUid: String): ReviewActionResponseV2
    fun getReviews(googleUid: String?, institutionId: Long?): ReviewResponseV1
}

@Service
open class DefaultReviewManagementService @Autowired constructor(
    private val userService: UserService,
    private val timestampProvider: TimestampProvider,
    private val institutionService: InstitutionService,
    private val userInterceptorService: UserInterceptorService,
    private val applicationConfigService: ApplicationConfigService,
    private val institutionReviewService: InstitutionReviewService
) : ReviewManagementService {

    @ReloadCache
    @Transactional
    override fun saveReview(institutionId: Long, starCount: Int, feedback: String, googleUid: String): ReviewActionResponseV1 {
        if (starCount < 1 || starCount > 5) throw Exception("Star count range of bound, 1 until 5")

        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val institution = institutionService.findById(institutionId)

        var review = institution.reviews.firstOrNull {
            it.user.id == requireNotNull(user.id)
        }
        when (review) {
            null -> {
                review = InstitutionReview(
                    feedback = feedback,
                    institution = institution,
                    user = user,
                    startCount = starCount.toLong()
                ).let(institutionReviewService::saveOrUpdate)
                institution.apply {
                    this.rating = incrementRating(starCount)
                    this.peopleOfRatedCount = this.peopleOfRatedCount.inc()
                }.let(institutionService::saveOrUpdate)
            }
            else -> {
                institution.apply {
                    this.rating = this.recountRating(starCount, review.startCount.toInt())
                }.let(institutionService::saveOrUpdate)
                review.apply {
                    this.feedback = feedback
                    this.startCount = starCount.toLong()
                }.let(institutionReviewService::saveOrUpdate)
            }
        }

        val institutionWrapper = listOf(institution)
            .map { EntityWrapper(it) }
            .let { userInterceptorService.handleSavedInstitutions(user, it) }
            .first()

        return ReviewActionResponseV1(
            institutionDto = institutionWrapper.generateInstitutionDtoV1(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)),
            reviewDto = review.generateReviewDtoV1()
        )
    }

    @ReloadCache
    @Transactional
    override fun saveReviewV2(institutionId: Long, starCount: Int, feedback: String, googleUid: String): ReviewActionResponseV2 {
        if (starCount < 1 || starCount > 5) throw Exception("Star count range of bound, 1 until 5")

        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val institution = institutionService.findById(institutionId)

        var review = institution.reviews.firstOrNull {
            it.user.id == requireNotNull(user.id)
        }
        when (review) {
            null -> {
                review = InstitutionReview(
                    feedback = feedback,
                    institution = institution,
                    user = user,
                    startCount = starCount.toLong()
                ).let(institutionReviewService::saveOrUpdate)
                institution.apply {
                    this.rating = incrementRating(starCount)
                    this.peopleOfRatedCount = this.peopleOfRatedCount.inc()
                }.let(institutionService::saveOrUpdate)
            }
            else -> {
                institution.apply {
                    this.rating = this.recountRating(starCount, review.startCount.toInt())
                }.let(institutionService::saveOrUpdate)
                review.apply {
                    this.feedback = feedback
                    this.startCount = starCount.toLong()
                }.let(institutionReviewService::saveOrUpdate)
            }
        }

        val institutionWrapper = listOf(institution)
            .map { EntityWrapper(it) }
            .let { userInterceptorService.handleSavedInstitutions(user, it, true) }
            .first()

        return ReviewActionResponseV2(
            institutionDto = institutionWrapper.generateInstitutionDtoV1(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)),
            reviewDto = review.generateReviewDtoV2()
        )
    }

    @Transactional(readOnly = true)
    override fun getReviews(googleUid: String?, institutionId: Long?): ReviewResponseV1 {
        val user = googleUid?.let(userService::findByGoogleUid)

        val institution = institutionId?.let(institutionService::findById)

        val reviews = mutableListOf<InstitutionReview>()

        when {
            institution != null -> institution.reviews.let(reviews::addAll)
            user != null -> user.reviews.let(reviews::addAll)
            else -> throw Exception("Arguments googleUid or institutionId must be not null")
        }

        val title =
            applicationConfigService
                .getConfigValueByConfigType(ApplicationConfigType.USER_PROFILE_REVIEWS_TITLE)
                .getStringValueNotNull()
                .plus(" ${institution?.name ?: ""}")

        return ReviewResponseV1(
            title = title,
            reviews = reviews.generateReviewDtoListV1()
        )
    }

    private fun Institution.incrementRating(starCount: Int): Double =
        ((this.rating * this.peopleOfRatedCount) + starCount) / (this.peopleOfRatedCount + 1)

    private fun Institution.recountRating(starCount: Int, decrementValue: Int): Double {
        val rollbackRating = ((this.rating * this.peopleOfRatedCount) - decrementValue) / (this.peopleOfRatedCount - 1)
        return ((rollbackRating * (this.peopleOfRatedCount - 1)) + starCount) / this.peopleOfRatedCount
    }

}