package com.wellcome.main.service.facade

import com.wellcome.main.entity.ApplicationConfig
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.entity.ConfigValue
import com.wellcome.main.repository.local.postgre.ApplicationConfigRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ApplicationConfigService : BaseService<ApplicationConfig> {
    fun findByConfigType(configType: ApplicationConfigType): ApplicationConfig?
    fun getConfigValueByConfigType(configType: ApplicationConfigType): ConfigValue
}

@Service
open class DefaultApplicationConfigService @Autowired constructor(
    private val applicationConfigRepository: ApplicationConfigRepository
) : ApplicationConfigService,
    DefaultBaseService<ApplicationConfig>(ApplicationConfig::class.java.simpleName, applicationConfigRepository) {

    @Transactional(readOnly = true)
    override fun findByConfigType(configType: ApplicationConfigType): ApplicationConfig? =
        applicationConfigRepository.findByConfigType(configType)

    @Transactional(readOnly = true)
    override fun getConfigValueByConfigType(configType: ApplicationConfigType): ConfigValue =
        applicationConfigRepository.findByConfigType(configType).let {
            return@let it?.toConfigValue() ?: configType.defaultValue
        }

}