package com.thomas.aistudio.data.repo

import com.thomas.aistudio.data.api.StudioApi
import com.thomas.aistudio.data.model.JobStatusResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StudioRepository(private val api: StudioApi) {

    /** Polls a job until it completes, fails, or emits latest status each tick. */
    fun pollJob(jobId: String, intervalMs: Long = 2000): Flow<JobStatusResponse> = flow {
        while (true) {
            val status = api.getJob(jobId)
            emit(status)
            if (status.status == "completed" || status.status == "failed") break
            delay(intervalMs)
        }
    }

    suspend fun providerStatus() = api.getProviderStatus()

    suspend fun projects() = api.listProjects()
}
