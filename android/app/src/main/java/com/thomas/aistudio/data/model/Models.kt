package com.thomas.aistudio.data.model

data class ImageGenerateRequest(
    val prompt: String,
    val aspectRatio: String = "1:1",
    val style: String = "photorealistic",
    val quality: String = "standard",
    val variations: Int = 1
)

data class ImageEditRequest(
    val imageBase64: String,
    val instruction: String
)

data class VideoFromImageRequest(
    val imageBase64: String,
    val motionPrompt: String,
    val durationSeconds: Int = 5,
    val aspectRatio: String = "9:16"
)

data class VideoGenerateRequest(
    val prompt: String,
    val durationSeconds: Int = 5,
    val aspectRatio: String = "9:16",
    val style: String = "cinematic"
)

data class VoiceGenerateRequest(
    val text: String,
    val voice: String = "default"
)

data class AudioGenerateRequest(
    val prompt: String,
    val durationSeconds: Int = 5
)

data class AdCreateRequest(
    val productImageBase64: String?,
    val productName: String,
    val objective: String,
    val audience: String,
    val tone: String,
    val durationSeconds: Int = 15,
    val aspectRatio: String = "9:16",
    val callToAction: String,
    val freeformPrompt: String? = null
)

data class JobResponse(
    val jobId: String,
    val status: String
)

data class JobStatusResponse(
    val jobId: String,
    val status: String,
    val progress: Int = 0,
    val resultUrl: String? = null,
    val storyboard: List<StoryboardScene>? = null,
    val error: String? = null
)

data class StoryboardScene(
    val index: Int,
    val startSeconds: Int,
    val endSeconds: Int,
    val description: String
)

data class ProjectDto(
    val id: String,
    val name: String,
    val createdAt: String,
    val prompt: String? = null,
    val thumbnailUrl: String? = null,
    val type: String
)

data class CreateProjectRequest(
    val name: String,
    val type: String,
    val prompt: String? = null
)

data class ProviderStatus(
    val text: Boolean,
    val image: Boolean,
    val video: Boolean,
    val audio: Boolean,
    val tts: Boolean,
    val demoMode: Boolean
)
