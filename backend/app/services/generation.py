from app.models.schemas import (
    AudioGenerateRequest,
    ImageEditRequest,
    ImageGenerateRequest,
    VideoFromImageRequest,
    VideoGenerateRequest,
    VoiceGenerateRequest,
)
from app.providers.factory import (
    get_audio_provider,
    get_image_provider,
    get_tts_provider,
    get_video_provider,
)
from app.workers.jobs import job_manager


async def run_image_generate(job_id: str, req: ImageGenerateRequest):
    provider = get_image_provider()
    job_manager.set_progress(job_id, 20)
    urls = await provider.generate_image(req.prompt, req.aspectRatio, req.style, req.variations)
    job_manager.complete(job_id, urls[0])


async def run_image_edit(job_id: str, req: ImageEditRequest):
    provider = get_image_provider()
    job_manager.set_progress(job_id, 20)
    url = await provider.edit_image(req.imageBase64, req.instruction)
    job_manager.complete(job_id, url)


async def run_video_generate(job_id: str, req: VideoGenerateRequest):
    provider = get_video_provider()
    job_manager.set_progress(job_id, 20)
    url = await provider.generate_video(req.prompt, req.durationSeconds, req.aspectRatio, req.style)
    job_manager.complete(job_id, url)


async def run_video_from_image(job_id: str, req: VideoFromImageRequest):
    provider = get_video_provider()
    job_manager.set_progress(job_id, 20)
    url = await provider.video_from_image(req.imageBase64, req.motionPrompt, req.durationSeconds, req.aspectRatio)
    job_manager.complete(job_id, url)


async def run_voice_generate(job_id: str, req: VoiceGenerateRequest):
    provider = get_tts_provider()
    job_manager.set_progress(job_id, 30)
    url = await provider.synthesize_speech(req.text, req.voice)
    job_manager.complete(job_id, url)


async def run_audio_generate(job_id: str, req: AudioGenerateRequest):
    provider = get_audio_provider()
    job_manager.set_progress(job_id, 30)
    url = await provider.generate_audio(req.prompt, req.durationSeconds)
    job_manager.complete(job_id, url)
