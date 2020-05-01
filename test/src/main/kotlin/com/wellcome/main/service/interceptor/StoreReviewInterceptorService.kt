package com.wellcome.main.service.interceptor

import com.wellcome.main.entity.institution.*
import com.wellcome.main.wrapper.Delegate
import com.wellcome.main.wrapper.EntityWrapper
import org.springframework.stereotype.Service

interface StoreReviewInterceptorService {
    fun handleForbiddenInstitutions(institutions: List<EntityWrapper<Institution>>): List<EntityWrapper<Institution>>
    fun handleForbiddenOffers(offers: List<EntityWrapper<InstitutionOffer>>): List<EntityWrapper<InstitutionOffer>>
    fun handleForbiddenEvents(events: List<EntityWrapper<InstitutionEvent>>): List<EntityWrapper<InstitutionEvent>>
}

@Service
open class DefaultStoreReviewInterceptorService : StoreReviewInterceptorService {

    override fun handleForbiddenInstitutions(institutions: List<EntityWrapper<Institution>>): List<EntityWrapper<Institution>> {
        return institutions.map {
            for (i in it.entity.categories) {
                if (i.categoryType == InstitutionCategoryType.STRIP_BAR
                    || i.categoryType == InstitutionCategoryType.HOOKAH
                    || i.categoryType == InstitutionCategoryType.VAPE_BAR) {
                    it.delegates.add(Delegate.StoreReviewDelegate(false))
                    return@map it
                }
            }
            it.delegates.add(Delegate.StoreReviewDelegate(true))
            return@map it
        }
    }

    override fun handleForbiddenOffers(offers: List<EntityWrapper<InstitutionOffer>>): List<EntityWrapper<InstitutionOffer>> {
        return offers.map {
            for (i in it.entity.getInstitutionNotNull().categories) {
                if (i.categoryType == InstitutionCategoryType.STRIP_BAR
                    || i.categoryType == InstitutionCategoryType.HOOKAH
                    || i.categoryType == InstitutionCategoryType.VAPE_BAR) {
                    it.delegates.add(Delegate.StoreReviewDelegate(false))
                    return@map it
                }
            }
            if (it.entity.offerType == OfferType.HOOKAH) {
                it.delegates.add(Delegate.StoreReviewDelegate(false))
                return@map it
            }
            it.delegates.add(Delegate.StoreReviewDelegate(true))
            return@map it
        }
    }

    override fun handleForbiddenEvents(events: List<EntityWrapper<InstitutionEvent>>): List<EntityWrapper<InstitutionEvent>> {
        return events.map {
            for (i in it.entity.institution.categories) {
                if (i.categoryType == InstitutionCategoryType.STRIP_BAR
                    || i.categoryType == InstitutionCategoryType.HOOKAH
                    || i.categoryType == InstitutionCategoryType.VAPE_BAR) {
                    it.delegates.add(Delegate.StoreReviewDelegate(false))
                    return@map it
                }
            }
            it.delegates.add(Delegate.StoreReviewDelegate(true))
            return@map it
        }
    }
}