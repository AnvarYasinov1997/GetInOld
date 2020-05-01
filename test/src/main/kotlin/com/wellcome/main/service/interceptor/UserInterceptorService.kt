package com.wellcome.main.service.interceptor

import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionEvent
import com.wellcome.main.entity.institution.InstitutionOffer
import com.wellcome.main.entity.selection.SelectionOffer
import com.wellcome.main.entity.story.Story
import com.wellcome.main.entity.user.User
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.stereotype.Service

interface UserInterceptorService {
    fun handleSavedInstitutions(user: User, wrappers: List<EntityWrapper<Institution>>, isRated: Boolean = false): List<EntityWrapper<Institution>>
    fun handleSavedInstitutionsInsideOffers(user: User, wrappers: List<EntityWrapper<InstitutionOffer>>): List<EntityWrapper<InstitutionOffer>>
    fun handleSavedInstitutionsInsideEvent(user: User, wrappers: List<EntityWrapper<InstitutionEvent>>): List<EntityWrapper<InstitutionEvent>>
    fun handleSavedInstitutionInsideOfferInsideSelection(user: User, wrappers: List<EntityWrapper<SelectionOffer>>): List<EntityWrapper<SelectionOffer>>
    fun handleLikedStory(user: User, wrappers: List<EntityWrapper<Story>>): List<EntityWrapper<Story>>
}

@Service
open class DefaultUserInterceptorService : UserInterceptorService {

    override fun handleSavedInstitutions(user: User, wrappers: List<EntityWrapper<Institution>>, isRated: Boolean): List<EntityWrapper<Institution>> {
        val bookmarks = user.getInstitutionBookmarks().mapNotNull(Institution::id)
        val reviews = user.getInstitutionReviews().mapNotNull(Institution::id)
        return wrappers.map { wrapper ->
            val saved = bookmarks.firstOrNull { it == requireNotNull(wrapper.entity.id) } != null
            val rated = if (isRated) true
            else reviews.firstOrNull { it == requireNotNull(wrapper.entity.id) } != null

            wrapper.delegates.add(Delegate.UserDelegate(saved, rated))
            return@map wrapper
        }
    }

    override fun handleSavedInstitutionsInsideOffers(user: User, wrappers: List<EntityWrapper<InstitutionOffer>>): List<EntityWrapper<InstitutionOffer>> {
        val bookmarks = user.getInstitutionBookmarks().mapNotNull(Institution::id)
        val reviews = user.getInstitutionReviews().mapNotNull(Institution::id)
        return wrappers.map { wrapper ->
            val saved = bookmarks.firstOrNull { it == requireNotNull(wrapper.entity.getInstitutionNotNull().id) } != null
            val rated = reviews.firstOrNull { it == requireNotNull(wrapper.entity.getInstitutionNotNull().id) } != null
            wrapper.delegates.add(Delegate.UserDelegate(saved, rated))
            return@map wrapper
        }
    }

    override fun handleSavedInstitutionsInsideEvent(user: User, wrappers: List<EntityWrapper<InstitutionEvent>>): List<EntityWrapper<InstitutionEvent>> {
        val bookmarks = user.getInstitutionBookmarks().mapNotNull(Institution::id)
        val reviews = user.getInstitutionReviews().mapNotNull(Institution::id)
        return wrappers.map { wrapper ->
            val saved = bookmarks.firstOrNull { it == requireNotNull(wrapper.entity.institution.id) } != null
            val rated = reviews.firstOrNull { it == requireNotNull(wrapper.entity.institution.id) } != null
            wrapper.delegates.add(Delegate.UserDelegate(saved, rated))
            return@map wrapper
        }
    }

    override fun handleSavedInstitutionInsideOfferInsideSelection(user: User, wrappers: List<EntityWrapper<SelectionOffer>>): List<EntityWrapper<SelectionOffer>> {
        val bookmarks = user.getInstitutionBookmarks().mapNotNull(Institution::id)
        val reviews = user.getInstitutionReviews().mapNotNull(Institution::id)
        return wrappers.map { wrapper ->
            val saved = bookmarks.firstOrNull { it == wrapper.entity.offer.institution!!.id!! } != null
            val rated = reviews.firstOrNull { it == wrapper.entity.offer.institution!!.id!! } != null
            wrapper.delegates.add(Delegate.UserDelegate(saved, rated))
            return@map wrapper
        }
    }

    override fun handleLikedStory(user: User, wrappers: List<EntityWrapper<Story>>): List<EntityWrapper<Story>> {
        return wrappers.map { wrapper ->
            val feedback = wrapper.entity.storyFeedback.firstOrNull { it.user == user }
            val liked = feedback?.type?.isLiked() ?: false
            wrapper.delegates.add(Delegate.UserStoryDelegate(liked))
            return@map wrapper
        }
    }
}