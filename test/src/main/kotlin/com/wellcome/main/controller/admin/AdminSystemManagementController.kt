package com.wellcome.main.controller.admin

import com.wellcome.main.Main
import com.wellcome.main.component.CacheReloader
import com.wellcome.main.configuration.utils.CacheState
import com.wellcome.main.configuration.utils.CustomCacheManager
import com.wellcome.main.util.enumerators.Permissions
import com.wellcome.main.util.enumerators.SwitchState
import com.wellcome.main.util.variables.Paths
import com.wellcome.main.util.variables.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = [Paths.BASE_SYSTEM_MANAGEMENT])
open class AdminSystemManagementController @Autowired constructor(
    private val cacheState: CacheState,
    private val cacheReloader: CacheReloader,
    private val customCacheManager: CustomCacheManager
) {

    @Secured(value = [Permissions.PermissionValues.CLEAR_ALL_CACHES])
    @GetMapping(value = [Paths.SystemManagement.CLEAR_ALL_CACHES])
    open fun clearAllCaches() {
        customCacheManager.clearAllCaches()
    }

    @Secured(value = [Permissions.PermissionValues.RESTART_APPLICATION])
    @GetMapping(value = [Paths.SystemManagement.RESTART_APPLICATION])
    open fun restartApplication() {
        Main.restartApplication()
    }

    @Secured(value = [Permissions.PermissionValues.SWITCH_CACHE])
    @GetMapping(value = [Paths.SystemManagement.SWITCH_CACHE])
    open fun switchCache(@RequestParam(value = Query.SWITCH_STATE) switchState: SwitchState) {
        when (switchState) {
            SwitchState.ENABLE -> {
                if (!cacheState.isCacheEnabled()) {
                    cacheReloader.reloadAllCaches()
                    cacheState.enableCache()
                } else throw RuntimeException("Cache already enabled")
            }
            SwitchState.DISABLE -> {
                if (cacheState.isCacheEnabled()) {
                    cacheState.disableCache()
                } else throw RuntimeException("Cache already disabled")
            }
        }
    }

}