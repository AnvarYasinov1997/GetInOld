package com.wellcome.main.service.facade

import com.wellcome.main.entity.Language
import com.wellcome.main.repository.local.postgre.LanguageRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface LanguageService : BaseService<Language>

@Service
open class DefaultLanguageService @Autowired constructor(
    private val languageRepository: LanguageRepository
) : DefaultBaseService<Language>(Language::class.java.simpleName, languageRepository),
    LanguageService