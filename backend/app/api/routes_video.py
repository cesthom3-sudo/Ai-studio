from fastapi import APIRouter

from app.models.schemas import JobResponse, VideoFromImageRequest, VideoGenerateRequest
from app.services.generation import run_video_from_image, run_video_generate
from app.workers.jobs import job_manager

router = APIRouter(prefix="/api/video", tags=["video"])


@router.post("/generate", response_model=JobResponse)
async def generate_video(req: VideoGenerateRequest):
    job = job_manager.schedule(lambda job_id: run_video_generate(job_id, req))
    return JobResponse(jobId=job.id, status=job.status)


@router.post("/from-image", response_model=JobResponse)
async def video_from_image(req: VideoFromImageRequest):
    job = job_manager.schedule(lambda job_id: run_video_from_image(job_id, req))
    return JobResponse(jobId=job.id, status=job.status)
