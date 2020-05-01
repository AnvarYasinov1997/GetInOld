package com.wellcome.main.service.extentions.generators.admin

import com.wellcome.main.dto.admin.common.TagDto
import com.wellcome.main.entity.institution.InstitutionTag


fun List<InstitutionTag>.generateTagDtoList(): List<TagDto> = this.map {
    TagDto(
        id = requireNotNull(it.id),
        tagName = it.name
    )
}