package com.wellcome.main.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.EntityManager

interface PersistentContextProvider {
    fun refreshCache()
}

@Component
open class DefaultPersistentContextProvider @Autowired constructor(
    private val entityManager: EntityManager
) : PersistentContextProvider {

    override fun refreshCache() {
        entityManager.clear()
    }
}