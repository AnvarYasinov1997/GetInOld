package com.wellcome.main.service.facade.user

import com.wellcome.main.entity.user.Permission
import com.wellcome.main.repository.local.postgre.PermissionRepository
import com.wellcome.main.repository.local.postgre.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// not implement base service
interface RolePermissionService {
    fun delete(permission: Permission)
}

@Service
open class DefaultRolePermissionService @Autowired constructor(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository
) : RolePermissionService {

    @Transactional
    override fun delete(permission: Permission) {
        roleRepository.findAll().forEach { role ->
            role.permissions.removeIf(permission::equals)
            role.let(roleRepository::save)
        }.also {
            permission.let(permissionRepository::delete)
        }
    }
}