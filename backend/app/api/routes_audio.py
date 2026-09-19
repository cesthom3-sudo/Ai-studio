from fastapi import APIRouter

from app.models.schemas import AudioGenerateRequest, JobResponse, VoiceGenerateRequest
from app.services.generation import run_audio_generate, run_voice_generate
from app.workers.jobs import job_manager

router = APIRouter(prefix="/api", tags=["audio"])


@router.post("/audio/generate", response_model=JobResponse)
async def generate_audio(req: AudioGenerateRequest):
    job = job_manager.schedule(lambda job_id: run_audio_generate(job_id, req))
    return JobResponse(jobId=job.id, status=job.status)


@router.post("/voice/generate", response_model=JobResponse)
async def generate_voice(req: VoiceGenerateRequest):
    job = job_manager.schedule(lambda job_id: run_voice_generate(job_id, req))
    return JobResponse(jobId=job.id, status=job.status)
