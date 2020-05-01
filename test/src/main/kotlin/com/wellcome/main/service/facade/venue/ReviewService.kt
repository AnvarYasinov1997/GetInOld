package com.wellcome.main.service.facade.venue

import com.wellcome.main.entity.venue.Review
import com.wellcome.main.repository.local.postgre.ReviewRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface ReviewService : BaseService<Review>

@Service
open class DefaultReviewService @Autowired constructor(
    private val reviewRepository: ReviewRepository
) : DefaultBaseService<Review>(Review::class.java.simpleName, reviewRepository),
    ReviewService