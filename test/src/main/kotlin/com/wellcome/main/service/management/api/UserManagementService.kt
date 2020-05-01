package com.wellcome.main.service.management.api

import com.wellcome.main.component.FirebaseAuthProvider
import com.wellcome.main.component.TimestampProvider
import com.wellcome.main.dto.api.newDto.response.v1.ActionInstitutionResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.EditUserResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.InitResponseV1
import com.wellcome.main.dto.api.newDto.response.v1.UserProfileResponseV1
import com.wellcome.main.dto.api.newDto.response.v2.UserProfileResponseV2
import com.wellcome.main.dto.api.newDto.response.v3.UserProfileResponseV3
import com.wellcome.main.dto.api.newDto.response.v4.UserProfileResponseV4
import com.wellcome.main.entity.Bookmark
import com.wellcome.main.entity.BookmarkType
import com.wellcome.main.entity.user.Gender
import com.wellcome.main.entity.user.Session
import com.wellcome.main.entity.user.User
import com.wellcome.main.exception.ConcurrentDuplicateUniqueEntityException
import com.wellcome.main.service.extentions.generators.api.common.generateInstitutionDtoV1
import com.wellcome.main.service.extentions.generators.api.common.generateUserDtoV1
import com.wellcome.main.service.extentions.generators.api.v1.generateUserProfileResponseV1
import com.wellcome.main.service.extentions.generators.api.v2.generateUserProfileResponseV2
import com.wellcome.main.service.extentions.generators.api.v3.generateUserProfileResponseV3
import com.wellcome.main.service.extentions.generators.api.v4.generateUserProfileResponseV4
import com.wellcome.main.service.facade.BookmarkService
import com.wellcome.main.service.facade.LocalityService
import com.wellcome.main.service.facade.institution.BirthdayCampaignUserService
import com.wellcome.main.service.facade.institution.InstitutionService
import com.wellcome.main.service.facade.user.SessionService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.interceptor.UserInterceptorService
import com.wellcome.main.util.enumerators.SearchInstitutionDays
import com.wellcome.main.util.functions.getDay
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.persistence.EntityNotFoundException

interface UserManagementService {
    fun getProfileV1(googleUid: String): UserProfileResponseV1
    fun getProfileV2(googleUid: String): UserProfileResponseV2
    fun getProfileV3(googleUid: String): UserProfileResponseV3
    fun getProfileV4(googleUid: String): UserProfileResponseV4
    fun saveInstitution(googleUid: String, institutionId: Long): ActionInstitutionResponseV1
    fun removeInstitution(googleUid: String, institutionId: Long): ActionInstitutionResponseV1
    fun getOrCreateByToken(token: String): User
    fun checkSession(instanceId: String, fcmToken: String)
    fun initUser(instanceId: String, fcmToken: String, token: String): InitResponseV1
    fun editAvatar(googleUid: String, avatarUrl: String): EditUserResponseV1
    fun editName(googleUid: String, name: String): EditUserResponseV1
    fun editDateOfBirth(googleUid: String, dateOfBirth: LocalDate, gender: String): EditUserResponseV1
    fun editPushNotification(googleUid: String, pushAvailable: Boolean): EditUserResponseV1
}

@Service
open class DefaultUserManagementService @Autowired constructor(
    private val userService: UserService,
    private val sessionService: SessionService,
    private val localityService: LocalityService,
    private val bookmarkService: BookmarkService,
    private val timestampProvider: TimestampProvider,
    private val institutionService: InstitutionService,
    private val firebaseAuthProvider: FirebaseAuthProvider,
    private val userInterceptorService: UserInterceptorService,
    private val birthdayCampaignUserService: BirthdayCampaignUserService
) : UserManagementService {

    @Transactional
    override fun saveInstitution(googleUid: String, institutionId: Long): ActionInstitutionResponseV1 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()
        val institution = institutionService.findById(institutionId)
        if (user.bookmarks.firstOrNull { it.institution?.id == institutionId } != null)
            throw Exception("Bookmark with institution id: $institutionId at user with google uid: $googleUid already checked")
        Bookmark(user = user, institution = institution, type = BookmarkType.INSTITUTION)
            .let(bookmarkService::saveOrUpdate)
            .let { user.bookmarks.add(it) }
        val institutionWrapper = listOf(institution)
            .map { EntityWrapper(it, mutableListOf()) }
            .let { userInterceptorService.handleSavedInstitutions(user, it) }
            .first()
        return ActionInstitutionResponseV1(institutionWrapper.generateInstitutionDtoV1(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)))
    }

    @Transactional
    override fun removeInstitution(googleUid: String, institutionId: Long): ActionInstitutionResponseV1 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()
        val institution = institutionService.findById(institutionId)
        val bookmark = user.bookmarks.filter { it.institution != null }.firstOrNull { requireNotNull(it.institution).id == institutionId }
            ?: throw Exception("Bookmark with institution id: $institutionId at user with google uid: $googleUid already deleted")
        bookmarkService.deleteById(bookmark.id!!)
        user.bookmarks.remove(bookmark)
        val institutionWrapper = listOf(institution)
            .map { EntityWrapper(it, mutableListOf()) }
            .let { userInterceptorService.handleSavedInstitutions(user, it) }
            .first()
        return ActionInstitutionResponseV1(institutionWrapper.generateInstitutionDtoV1(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime)))
    }

    @Transactional(readOnly = true)
    override fun getProfileV1(googleUid: String): UserProfileResponseV1 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val bookmarks = user.getInstitutionBookmarks()
            .map { EntityWrapper(it, mutableListOf()) }
            .let { userInterceptorService.handleSavedInstitutions(user, it) }

        return user.generateUserProfileResponseV1(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime), bookmarks)
    }

    @Transactional(readOnly = true)
    override fun getProfileV2(googleUid: String): UserProfileResponseV2 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User with googleUid: $googleUid is not found to database")

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val bookmarks = user.getInstitutionBookmarks()
            .map { EntityWrapper(it, mutableListOf()) }
            .let { userInterceptorService.handleSavedInstitutions(user, it) }

        return user.generateUserProfileResponseV2(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime), bookmarks)
    }

    @Transactional(readOnly = true)
    override fun getProfileV3(googleUid: String): UserProfileResponseV3 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is npt found to database")

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val bookmarks = user.getInstitutionBookmarks()
            .map { EntityWrapper(it, mutableListOf()) }
            .let { userInterceptorService.handleSavedInstitutions(user, it) }

        return user.generateUserProfileResponseV3(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime), bookmarks)
    }

    @Transactional(readOnly = true)
    override fun getProfileV4(googleUid: String): UserProfileResponseV4 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is npt found to database")

        val userZonedDateTime = timestampProvider.getUserZonedDateTime()

        val bookmarks = user.getInstitutionBookmarks()
            .map { EntityWrapper(it, mutableListOf()) }
            .let { userInterceptorService.handleSavedInstitutions(user, it) }

        val birthdayCampaignUser = user.let(User::getIdNotNull)
            .let(birthdayCampaignUserService::findByUser)
            ?.let { if(!it.expired) it else it }

        return user.generateUserProfileResponseV4(getDay(SearchInstitutionDays.NOW.name, userZonedDateTime), birthdayCampaignUser, bookmarks)
    }

    @Transactional
    override fun initUser(instanceId: String, fcmToken: String, token: String): InitResponseV1 {
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        var session = sessionService.findByInstanceId(instanceId)
        var user = userService.findByGoogleUid(initResult.googleUid)
        if (session == null) {
            session = Session(
                instanceId = instanceId,
                fcmToken = fcmToken,
                latestSession = timestampProvider.getUserZonedDateTime().toString()
            ).let(sessionService::saveOrUpdate)
        }
        if (user == null) {
            user = User(
                googleUid = initResult.googleUid,
                name = initResult.username,
                email = initResult.email,
                photoUrl = initResult.photoUrl,
                session = session,
                locality = localityService.findById(5)
            ).let(userService::saveOrUpdate)
        } else {
            user.apply {
                this.session = session
            }.let(userService::saveOrUpdate)
        }
        return InitResponseV1(userId = requireNotNull(user.id), name = user.name, avatarUrl = user.photoUrl)
    }

    @Transactional
    override fun getOrCreateByToken(token: String): User {
        val initResult = firebaseAuthProvider.getUserCredentialsByFirebaseToken(token)
        return userService.findByGoogleUid(initResult.googleUid) ?: User(
            googleUid = initResult.googleUid,
            name = initResult.username,
            email = initResult.email,
            photoUrl = initResult.photoUrl,
            locality = localityService.findById(5)
        ).let(userService::saveOrUpdate)
    }

    @Transactional
    override fun checkSession(instanceId: String, fcmToken: String) {
        val userZonedDateTime = timestampProvider.getUserZonedDateTime()
        val session = sessionService.findByInstanceId(instanceId)
        if (session != null) {
            session.apply {
                this.fcmToken = fcmToken
                this.latestSession = userZonedDateTime.toString()
            }.let(sessionService::saveOrUpdate)
        } else {
            try {
                Session(
                    instanceId = instanceId,
                    fcmToken = fcmToken,
                    latestSession = userZonedDateTime.toString()
                ).let(sessionService::saveOrUpdate)
            } catch (e: Exception) {
                throw ConcurrentDuplicateUniqueEntityException(
                    "Session with instanceId: $instanceId already created by another thread")
            }
        }
    }

    @Transactional
    override fun editAvatar(googleUid: String, avatarUrl: String): EditUserResponseV1 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is not found to database")
        user.apply {
            this.photoUrl = avatarUrl
        }.let(userService::saveOrUpdate)
        return EditUserResponseV1(user.generateUserDtoV1())
    }

    @Transactional
    override fun editName(googleUid: String, name: String): EditUserResponseV1 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is not found to database")
        user.apply {
            this.name = name
        }.let(userService::saveOrUpdate)
        return EditUserResponseV1(user.generateUserDtoV1())
    }

    @Transactional
    override fun editDateOfBirth(googleUid: String, dateOfBirth: LocalDate, gender: String): EditUserResponseV1 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is not found to database")
        user.apply {
            this.dateOfBirth = dateOfBirth
            this.gender = Gender.valueOf(gender)
        }.let(userService::saveOrUpdate)
        return EditUserResponseV1(user.generateUserDtoV1())
    }

    @Transactional
    override fun editPushNotification(googleUid: String, pushAvailable: Boolean): EditUserResponseV1 {
        val user = userService.findByGoogleUid(googleUid)
            ?: throw EntityNotFoundException("User width googleUid: $googleUid is not found to database")
        user.apply {
            this.pushAvailable = pushAvailable
        }.let(userService::saveOrUpdate)
        return EditUserResponseV1(user.generateUserDtoV1())
    }
}