package com.thomas.aistudio.data.api

import com.thomas.aistudio.data.model.*
import retrofit2.http.*

interface StudioApi {

    @POST("api/image/generate")
    suspend fun generateImage(@Body request: ImageGenerateRequest): JobResponse

    @POST("api/image/edit")
    suspend fun editImage(@Body request: ImageEditRequest): JobResponse

    @POST("api/video/generate")
    suspend fun generateVideo(@Body request: VideoGenerateRequest): JobResponse

    @POST("api/video/from-image")
    suspend fun videoFromImage(@Body request: VideoFromImageRequest): JobResponse

    @POST("api/audio/generate")
    suspend fun generateAudio(@Body request: AudioGenerateRequest): JobResponse

    @POST("api/voice/generate")
    suspend fun generateVoice(@Body request: VoiceGenerateRequest): JobResponse

    @POST("api/ad/create")
    suspend fun createAd(@Body request: AdCreateRequest): JobResponse

    @GET("api/jobs/{jobId}")
    suspend fun getJob(@Path("jobId") jobId: String): JobStatusResponse

    @GET("api/projects")
    suspend fun listProjects(): List<ProjectDto>

    @POST("api/projects")
    suspend fun createProject(@Body request: CreateProjectRequest): ProjectDto

    @DELETE("api/projects/{id}")
    suspend fun deleteProject(@Path("id") id: String)

    @GET("api/status")
    suspend fun getProviderStatus(): ProviderStatus
}
