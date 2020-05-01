package com.wellcome.main.service.facade

import com.wellcome.main.entity.Bookmark
import com.wellcome.main.repository.local.postgre.BookmarkRepository
import com.wellcome.main.service.facade.baseService.BaseService
import com.wellcome.main.service.facade.baseService.DefaultBaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface BookmarkService : BaseService<Bookmark> {
    fun findByInstitution(institutionId: Long): List<Bookmark>
}

@Service
open class DefaultBookmarkService @Autowired constructor(
    private val bookmarkRepository: BookmarkRepository
) : BookmarkService, DefaultBaseService<Bookmark>(Bookmark::class.java.simpleName, bookmarkRepository) {

    @Transactional(readOnly = true)
    override fun findByInstitution(institutionId: Long): List<Bookmark> =
        bookmarkRepository.findByInstitutionId(institutionId)

}