package com.wellcome.main.databaseUtils

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.Configurable
import org.hibernate.id.IdentityGenerator
import org.hibernate.service.ServiceRegistry
import org.hibernate.type.Type
import java.io.Serializable
import java.util.*


open class CustomSequenceGenerator : IdentityGenerator(), Configurable {

    private lateinit var sequenceCache: SequenceCache

    override fun generate(s: SharedSessionContractImplementor, obj: Any): Serializable {
        return sequenceCache.getNextId(obj)
    }

    override fun configure(type: Type, params: Properties, serviceRegistry: ServiceRegistry) {
        this.sequenceCache = CustomSequenceGenerator.sequenceCache
    }

    companion object {
        val sequenceCache = DefaultSequenceCache()
    }

}