package com.wellcome.main.dto.admin.response

import com.wellcome.main.dto.admin.common.CategoryDto
import com.wellcome.main.dto.admin.common.TagDto

data class InstitutionAttributesResponse(val tags: List<TagDto>,
                                         val categories: List<CategoryDto>)