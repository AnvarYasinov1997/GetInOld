package com.wellcome.main.service.facade.user

import com.wellcome.main.entity.user.Session
import com.wellcome.main.repository.local.postgre.SessionRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface SessionService : BaseService<Session> {
    fun findByInstanceId(instanceId: String): Session?
}

@Service
open class DefaultSessionService @Autowired constructor(
    private val sessionRepository: SessionRepository
) : SessionService, DefaultBaseService<Session>(Session::class.java.simpleName, sessionRepository) {

    @Transactional(readOnly = true)
    override fun findByInstanceId(instanceId: String): Session? =
        sessionRepository.findByInstanceId(instanceId).orElseGet { null }

}