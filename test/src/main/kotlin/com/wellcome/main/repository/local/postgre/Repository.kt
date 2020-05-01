package com.wellcome.main.repository.local.postgre

import com.wellcome.main.entity.*
import com.wellcome.main.entity.institution.*
import com.wellcome.main.entity.institutionProfile.*
import com.wellcome.main.entity.selection.Selection
import com.wellcome.main.entity.selection.SelectionOffer
import com.wellcome.main.entity.story.Story
import com.wellcome.main.entity.story.StoryFeedback
import com.wellcome.main.entity.story.UserStory
import com.wellcome.main.entity.user.Permission
import com.wellcome.main.entity.user.Role
import com.wellcome.main.entity.user.Session
import com.wellcome.main.entity.user.User
import com.wellcome.main.entity.venue.*
import com.wellcome.main.entity.venue.event.Event
import com.wellcome.main.entity.venue.event.EventContent
import com.wellcome.main.entity.venue.offer.Offer
import com.wellcome.main.entity.venue.offer.OfferContent
import com.wellcome.main.entity.venue.offer.OfferWorkTime
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun findByName(name: String): Optional<User>
    fun findByLocalityId(localityId: Long): List<User>
    fun findByGoogleUid(googleUid: String): Optional<User>
}

interface LocalityRepository : JpaRepository<Locality, Long> {
    fun findByName(name: String): Optional<Locality>
}

interface ApplicationConfigRepository : JpaRepository<ApplicationConfig, Long> {
    fun findByConfigType(configType: ApplicationConfigType): ApplicationConfig?
}

interface InstitutionRepository : JpaRepository<Institution, Long> {
    fun findByLocalityIdAndBlockedFalse(localityId: Long): List<Institution>
    fun findByProcessingTrue(): List<Institution>
    fun findByInstagramAccount(instagramAccount: String): Optional<Institution>
    fun findByLocalityIdAndBlockedFalseAndRangingTrue(localityId: Long): List<Institution>
    fun findByLocalityIdAndBlockedFalseAndProcessingFalse(localityId: Long): List<Institution>
    fun findByLocalityId(localityId: Long): List<Institution>
}

interface InstitutionPictureRepository : JpaRepository<InstitutionPicture, Long> {
    fun findByInstitutionId(institutionId: Long): List<InstitutionPicture>
}

interface InstitutionReviewRepository : JpaRepository<InstitutionReview, Long>

interface InstitutionContactPhoneRepository : JpaRepository<InstitutionContactPhone, Long>

interface InstitutionWorkTimeRepository : JpaRepository<InstitutionWorkTime, Long>

interface InstitutionTagRepository : JpaRepository<InstitutionTag, Long>

interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: String): Optional<Role>
}

interface MapsInstitutionRepository : JpaRepository<MapsInstitution, Long> {
    fun findByCreatedFalse(): List<MapsInstitution>
}

interface PermissionRepository : JpaRepository<Permission, Long> {
    fun findByName(name: String): Optional<Permission>
}

interface InstitutionOfferRepository : JpaRepository<InstitutionOffer, Long> {
    fun findByInstitutionIdAndActiveTrue(institutionId: Long): List<InstitutionOffer>
    fun findByCompletedFalse(): List<InstitutionOffer>
}

interface InstitutionOfferWorkTimeRepository : JpaRepository<InstitutionOfferWorkTime, Long>

interface InstitutionCategoryRepository : JpaRepository<InstitutionCategory, Long> {
    fun findByRangingTrue(): List<InstitutionCategory>
    fun findByCategoryType(institutionCategoryType: InstitutionCategoryType): Optional<InstitutionCategory>
}

interface PromotedInstitutionRepository : JpaRepository<PromotedInstitution, Long> {
    fun findByInstitutionCategoryId(institutionCategoryId: Long): List<PromotedInstitution>
}

interface PriceRepository : JpaRepository<Price, Long>

interface InstitutionEventRepository : JpaRepository<InstitutionEvent, Long> {
    fun findByCompletedFalse(): List<InstitutionEvent>
}

interface SessionRepository : JpaRepository<Session, Long> {
    fun findByInstanceId(instanceId: String): Optional<Session>
}

interface BookmarkRepository : JpaRepository<Bookmark, Long> {
    fun findByInstitutionId(institutionId: Long): List<Bookmark>
}

interface InstitutionProfileRepository : JpaRepository<InstitutionProfile, Long> {
    fun findByLogin(login: String): Optional<InstitutionProfile>
    fun findByInstitutionId(institutionId: Long): Optional<InstitutionProfile>
}

interface InstitutionEditRequestRepository : JpaRepository<InstitutionEditRequest, Long> {
    fun findByInstitutionProfileIdAndApprovedFalse(institutionProfileId: Long): List<InstitutionEditRequest>
}

interface InstitutionEditRequestOfferRepository : JpaRepository<InstitutionEditRequestOffer, Long> {
    fun findByOfferId(offerId: Long): List<InstitutionEditRequestOffer>
}

interface InstitutionEditRequestEventRepository : JpaRepository<InstitutionEditRequestEvent, Long> {
    fun findByEventId(eventId: Long): List<InstitutionEditRequestEvent>
}

interface InstitutionEditRequestPictureRepository : JpaRepository<InstitutionEditRequestPicture, Long> {
    fun findByPictureId(pictureId: Long): List<InstitutionEditRequestPicture>
}

interface InstitutionEditRequestStatusRepository : JpaRepository<InstitutionEditRequestStatus, Long>

interface StoryRepository : JpaRepository<Story, Long>

interface StoryFeedbackRepository : JpaRepository<StoryFeedback, Long> {
    fun findByUserIdAndStoryId(userId: Long, storyId: Long): Optional<StoryFeedback>
}

interface SelectionRepository : JpaRepository<Selection, Long> {
    fun findByName(name: String): Optional<Selection>
}

interface SelectionOfferRepository : JpaRepository<SelectionOffer, Long> {
    fun findByOfferId(offerId: Long): Optional<SelectionOffer>
    fun findBySelectionId(selectionId: Long): List<SelectionOffer>
}

interface UserStoryRepository : JpaRepository<UserStory, Long>

interface BirthdayCampaignRepository : JpaRepository<BirthdayCampaign, Long> {
    fun findByInstitutionId(institutionId: Long): List<BirthdayCampaign>
}

interface BirthdayCampaignUserRepository : JpaRepository<BirthdayCampaignUser, Long> {
    fun findByUserIdAndExpiredFalse(userId: Long): List<BirthdayCampaignUser>
    fun findByExpiredFalse(): List<BirthdayCampaignUser>
}

interface InstructionRepository : JpaRepository<Instruction, Long>

interface MarketingRepository : JpaRepository<Marketing, Long>

//new
interface LanguageRepository : JpaRepository<Language, Long>

interface VenueRepository : JpaRepository<Venue, Long>

interface VenueContentRepository : JpaRepository<VenueContent, Long>

interface VenueWorkTimeRepository : JpaRepository<VenueWorkTime, Long>

interface OfferRepository : JpaRepository<Offer, Long>

interface OfferContentRepository : JpaRepository<OfferContent, Long>

interface OfferWorkTimeRepository : JpaRepository<OfferWorkTime, Long>

interface EventRepository : JpaRepository<Event, Long>

interface EventContentRepository : JpaRepository<EventContent, Long>

interface TagRepository : JpaRepository<Tag, Long>

interface CategoryRepository : JpaRepository<Category, Long>

interface ContactPhoneRepository : JpaRepository<ContactPhone, Long>

interface ReviewRepository : JpaRepository<Review, Long>

interface PictureRepository : JpaRepository<Picture, Long>