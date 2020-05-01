package com.wellcome.main.service.facade.user

import com.wellcome.main.entity.user.Role
import com.wellcome.main.repository.local.postgre.RoleRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface RoleService : BaseService<Role> {
    fun findByName(roleName: String): Role?
}

@Service
open class DefaultRoleService @Autowired constructor(
    private val roleRepository: RoleRepository
) : RoleService, DefaultBaseService<Role>(Role::class.java.simpleName, roleRepository) {

    @Transactional
    override fun findByName(roleName: String): Role? =
        roleRepository.findByName(roleName).orElseGet { null }

}