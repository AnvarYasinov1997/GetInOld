package com.wellcome.main.service.management.admin

import com.wellcome.main.service.facade.user.UserService
import com.wellcome.main.util.functions.encryptPassword
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

interface AdminUserManagementService {
    fun changePassword(email: String, newPassword: String)
}

@Service
open class DefaultAdminUserManagementService @Autowired constructor(
    private val userService: UserService
) : AdminUserManagementService {

    @Transactional
    override fun changePassword(email: String, newPassword: String) {
        userService.findByEmail(email)?.apply {
            this.setNewPassword(encryptPassword(newPassword))
        }?.let(userService::saveOrUpdate)
            ?: throw EntityNotFoundException("User with email: $email is not found to database")
    }

}