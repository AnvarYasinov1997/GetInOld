package com.wellcome.main.service.facade.user

import com.wellcome.main.entity.user.Permission
import com.wellcome.main.repository.local.postgre.PermissionRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface PermissionService : BaseService<Permission> {
    fun findByName(name: String): Permission?
}

@Service
open class DefaultPermissionService @Autowired constructor(
    private val permissionRepository: PermissionRepository
) : PermissionService, DefaultBaseService<Permission>(Permission::class.java.simpleName, permissionRepository) {

    @Transactional(readOnly = true)
    override fun findByName(name: String): Permission? =
        permissionRepository.findByName(name).orElseGet { null }

}