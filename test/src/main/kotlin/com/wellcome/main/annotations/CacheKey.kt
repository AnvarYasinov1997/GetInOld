package com.wellcome.main.annotations

@Target(allowedTargets = [AnnotationTarget.VALUE_PARAMETER])
@Retention(value = AnnotationRetention.RUNTIME)
annotation class CacheKey