package com.wellcome.main.service.facade.venue

import com.wellcome.main.entity.venue.Tag
import com.wellcome.main.repository.local.postgre.TagRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface TagService : BaseService<Tag>

@Service
open class DefaultTagService @Autowired constructor(
    private val tagRepository: TagRepository
) : DefaultBaseService<Tag>(Tag::class.java.simpleName, tagRepository),
    TagService