package com.wellcome.main.util.enumerators

import com.wellcome.main.service.cache.DefaultInstitutionCacheService
import com.wellcome.main.service.facade.institution.InstitutionService
import java.lang.reflect.Method

enum class InstitutionCacheMethodNames(private val methodName: String) {
    FIND_BY_ID(InstitutionService::class.java.methods.map(Method::getName).first("findById"::equals)),
    FIND_BY_LOCALITY(InstitutionService::class.java.methods.map(Method::getName).first("findByLocality"::equals));

    fun getKey(key: Any): String = StringBuilder()
        .append(DefaultInstitutionCacheService::class.java.simpleName)
        .append("_")
        .append(this.methodName)
        .append("_")
        .append(key.toString())
        .toString()
}