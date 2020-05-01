package com.wellcome.main.service.facade.venue

import com.wellcome.main.entity.venue.Picture
import com.wellcome.main.repository.local.postgre.PictureRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface PictureService : BaseService<Picture>

@Service
open class DefaultPictureService @Autowired constructor(
    private val pictureRepository: PictureRepository
) : DefaultBaseService<Picture>(Picture::class.java.simpleName, pictureRepository),
    PictureService