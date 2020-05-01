package com.wellcome.main.service.facade.venue

import com.wellcome.main.entity.venue.Category
import com.wellcome.main.repository.local.postgre.CategoryRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface CategoryService : BaseService<Category>

@Service
open class DefaultCategoryService @Autowired constructor(
    private val categoryRepository: CategoryRepository
) : DefaultBaseService<Category>(Category::class.java.simpleName, categoryRepository),
    CategoryService