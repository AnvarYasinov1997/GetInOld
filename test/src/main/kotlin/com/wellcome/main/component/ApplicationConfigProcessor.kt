package com.wellcome.main.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.wellcome.main.configuration.utils.TelegramBotInit
import com.wellcome.main.configuration.utils.ThreadCache
import com.wellcome.main.entity.ApplicationConfig
import com.wellcome.main.entity.ApplicationConfigType
import com.wellcome.main.entity.Locality
import com.wellcome.main.entity.institution.Institution
import com.wellcome.main.entity.institution.InstitutionCategory
import com.wellcome.main.entity.institution.InstitutionCategoryType
import com.wellcome.main.entity.institutionProfile.InstitutionProfile
import com.wellcome.main.entity.user.Permission
import com.wellcome.main.entity.user.User
import com.wellcome.main.exception.PreprocessException
import com.wellcome.main.service.facade.ApplicationConfigService
import com.wellcome.main.service.facade.LocalityService
import com.wellcome.main.service.facade.institution.*
import com.wellcome.main.service.facade.user.PermissionService
import com.wellcome.main.service.facade.user.RolePermissionService
import com.wellcome.main.service.facade.user.RoleService
import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.functions.encryptPassword
import com.wellcome.main.util.variables.Common
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileWriter
import java.lang.StringBuilder
import java.util.*

@Component
open class ApplicationConfigProcessor @Autowired constructor(
    private val institutionService: InstitutionService,
    private val institutionProfileService: InstitutionProfileService,

    private val threadCache: ThreadCache,
    private val userService: UserService,
    private val roleService: RoleService,
    private val loggerService: LoggerService,
    private val localityService: LocalityService,
    private val timestampProvider: TimestampProvider,
    private val permissionService: PermissionService,
    private val rolePermissionService: RolePermissionService,
    private val configurableEnvironment: ConfigurableEnvironment,
    private val applicationConfigService: ApplicationConfigService,
    private val institutionCategoryService: InstitutionCategoryService,
    @Qualifier(value = "firstDefault") private val telegramBotService: TelegramBotInit
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        threadCache
            .getUserZonedDateTimeRequestThreadLocal()
            .set(timestampProvider.getServerZonedDateTime())
        configurableEnvironment.activeProfiles.firstOrNull("prod"::equals)?.let {
            Thread(this.telegramBotService::init).apply { this.isDaemon = true }.start()
        }
        createAppConfigValues()
        createInstitutionCategories()
        createSystemUser()
        updatePermissions()
        threadCache.getUserZonedDateTimeRequestThreadLocal().remove()
        loggerService.sendLogDeveloper(LogMessage("Application started"))
    }

    private fun uploadPasswords() {
        val activeInstitutions =
            institutionService.findAll()
                .filterNot(Institution::blocked)

        val random = Random()

        val profiles = mutableListOf<Pair<Institution, Pair<String, Pair<String, String>>>>()

        activeInstitutions.forEach { institution ->
            val name = StringBuilder()
                .append(institution.locality.name.toLowerCase())
                .append("_")
                .append(institution.name.toLowerCase().replace(" ", "_"))
                .toString()

            val password = StringBuilder()
                .append(random.nextInt(9))
                .append(random.nextInt(9))
                .append(random.nextInt(9))
                .append(random.nextInt(9))
                .toString()

            val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
            profiles.add(Pair(institution, Pair(name, Pair(password, passwordHash))))
        }
        val objectMapper = ObjectMapper()
        val json = objectMapper.writeValueAsString(profiles.map { it.second.first to it.second.second.first }.toMap())

        println("------------------------")
        println()
        println(json)
        println()
        println("------------------------")

        profiles.map {
            InstitutionProfile(
                login = it.second.first,
                accessKey = it.second.second.second,
                institution = it.first
            )
        }.let(institutionProfileService::saveAll)
    }

    private fun createAppConfigValues() {
        ApplicationConfigType.values().forEach {
            applicationConfigService.findByConfigType(it) ?: applicationConfigService.saveOrUpdate(ApplicationConfig(
                configType = it,
                longValue = it.defaultValue.longValue,
                doubleValue = it.defaultValue.doubleValue,
                stringValue = it.defaultValue.stringValue
            ))
        }
    }

    private fun createInstitutionCategories() {
        InstitutionCategoryType.values().forEach {
            institutionCategoryService.findByCategoryTypeOrNull(it)
                ?: InstitutionCategory(it.name, false, it).let(institutionCategoryService::saveOrUpdate)
        }
    }

    private fun createSystemUser() {
        val locality = localityService.findByName(SYSTEM_LOCALITY_NAME) ?: localityService.saveOrUpdate(Locality(
            name = SYSTEM_LOCALITY_NAME,
            timezone = SYSTEM_LOCALITY_TIMEZONE,
            topic = SYSTEM_LOCALITY_TOPIC
        ))
        userService.findByEmail(Common.SYSTEM_USER_EMAIL) ?: userService.saveOrUpdate(User(
            googleUid = "",
            name = SYSTEM_USER_NAME,
            email = Common.SYSTEM_USER_EMAIL,
            locality = locality,
            role = roleService.findByName(ROLE_ADMIN)
                ?: throw PreprocessException("Role with name $ROLE_ADMIN is not found to database"),
            photoUrl = "",
            password = encryptPassword(SYSTEM_USER_PASSWORD)
        ))
        userService.findByEmail(Common.COMMON_MODERATION_USER) ?: userService.saveOrUpdate(User(
            googleUid = "sddfsfdg",
            name = MODERATION_USER_NAME,
            email = Common.COMMON_MODERATION_USER,
            locality = locality,
            role = roleService.findByName(ROLE_MODERATOR)
                ?: throw PreprocessException("Role with name $ROLE_MODERATOR is not found to database"),
            photoUrl = "",
            password = encryptPassword(MODERATION_USER_PASSWORD)
        ))
    }

    private fun updatePermissions() {
        Permissions.values().forEach {
            permissionService.findByName(it.value)
                ?: Permission(it.value).let(permissionService::saveOrUpdate)
        }
        permissions@ for (i in permissionService.findAll()) {
            for (j in Permissions.values()) {
                if (i.name == j.value) {
                    continue@permissions
                }
            }
            i.let(rolePermissionService::delete)
        }
    }

    companion object {
        private const val SYSTEM_USER_NAME = "SystemUser"
        private const val SYSTEM_USER_PASSWORD = "QZecADwxS"
        private const val SYSTEM_LOCALITY_NAME = "SystemLocality"
        private const val SYSTEM_LOCALITY_TIMEZONE = "Future"
        private const val SYSTEM_LOCALITY_TOPIC = "SystemLocality"
        private const val ROLE_ADMIN = "admin"
        private const val MODERATION_USER_NAME = "ModerationUser"
        private const val MODERATION_USER_GENDER = "T-1000"
        private const val MODERATION_USER_PASSWORD = "!QAZxsw2#EDCvfr4"
        private const val ROLE_MODERATOR = "admin"

    }
}