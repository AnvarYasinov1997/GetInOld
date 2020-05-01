package com.wellcome.main.service.facade.venue

import com.wellcome.main.entity.venue.ContactPhone
import com.wellcome.main.repository.local.postgre.ContactPhoneRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface ContactPhoneService : BaseService<ContactPhone>

@Service
open class Default @Autowired constructor(
    private val contactPhoneRepository: ContactPhoneRepository
) : DefaultBaseService<ContactPhone>(ContactPhone::class.java.simpleName, contactPhoneRepository),
    ContactPhoneService