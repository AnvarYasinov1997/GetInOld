package com.wellcome.main.annotations

import org.springframework.stereotype.Component

@Component
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class PreProcessor