package com.wellcome.main.component

import org.springframework.stereotype.Component

interface SynchronizeObjectProvider {
    fun getSelectionAndSelectionOfferSharedSynchronizedObject(): Any
}

@Component
open class DefaultSynchronizeObjectProvider : SynchronizeObjectProvider {

    private val selectionAndSelectionOffer = Any()

    override fun getSelectionAndSelectionOfferSharedSynchronizedObject(): Any =
        selectionAndSelectionOffer

}