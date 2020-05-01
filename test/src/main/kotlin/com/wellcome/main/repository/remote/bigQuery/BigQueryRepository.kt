package com.wellcome.main.repository.remote.bigQuery

import com.google.cloud.bigquery.*
import com.wellcome.main.service.utils.LogMessage
import com.wellcome.main.service.utils.LoggerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*


interface BigQueryRepository {
    fun getData(query: String): TableResult
}

@Repository
open class DefaultBigQueryRepository @Autowired constructor(
    private val bigQuery: BigQuery,
    private val loggerService: LoggerService
) : BigQueryRepository {

    override fun getData(query: String): TableResult {
        val queryConfig = QueryJobConfiguration.newBuilder(query)
            .setUseLegacySql(false)
            .build()

        val jobId = JobId.of(UUID.randomUUID().toString())
        var queryJob = bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build())

        queryJob = queryJob!!.waitFor()

        if (queryJob == null) {
            val message = "Job no longer exists"
            loggerService.error(LogMessage(message))
            throw RuntimeException(message)
        } else if (queryJob.status.error != null) {
            val message = queryJob.status.error.toString()
            loggerService.error(LogMessage(message))
            throw RuntimeException(message)
        }
        return queryJob.getQueryResults()
    }

}