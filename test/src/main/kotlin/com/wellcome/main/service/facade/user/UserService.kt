package com.wellcome.main.service.facade.user

import com.wellcome.main.entity.user.User
import com.wellcome.main.repository.local.postgre.UserRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface UserService : BaseService<User> {
    fun findByEmail(email: String): User?
    fun findByName(name: String): User?
    fun findByLocality(localityId: Long): List<User>
    fun findByGoogleUid(googleUid: String): User?
}

@Service
open class DefaultUserService @Autowired constructor(
    private val userRepository: UserRepository
) : UserService, DefaultBaseService<User>(User::class.java.simpleName, userRepository) {

    @Transactional(readOnly = true)
    override fun findByEmail(email: String): User? =
        userRepository.findByEmail(email).orElseGet { null }

    @Transactional(readOnly = true)
    override fun findByLocality(localityId: Long): List<User> =
        userRepository.findByLocalityId(localityId)

    @Transactional(readOnly = true)
    override fun findByName(name: String): User? =
        userRepository.findByName(name).orElseGet { null }

    @Transactional(readOnly = true)
    override fun findByGoogleUid(googleUid: String): User? =
        userRepository.findByGoogleUid(googleUid).orElseGet { null }

}