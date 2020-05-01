package com.wellcome.main.contextListener

import com.wellcome.main.databaseUtils.SequenceCacheUploader
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
open class SequenceCacheUploaderListener @Autowired constructor(
    private val loggerService: LoggerService,
    private val sequenceCacheUploader: SequenceCacheUploader
) {

    @EventListener(value = [ContextRefreshedEvent::class])
    open fun uploadSequenceCache() {
        sequenceCacheUploader.uploadSequences()
        loggerService.info(LogMessage("Sequence cache warming success"))
    }

}