from fastapi import APIRouter

from app.models.schemas import ImageEditRequest, ImageGenerateRequest, JobResponse
from app.services.generation import run_image_edit, run_image_generate
from app.workers.jobs import job_manager

router = APIRouter(prefix="/api/image", tags=["image"])


@router.post("/generate", response_model=JobResponse)
async def generate_image(req: ImageGenerateRequest):
    job = job_manager.schedule(lambda job_id: run_image_generate(job_id, req))
    return JobResponse(jobId=job.id, status=job.status)


@router.post("/edit", response_model=JobResponse)
async def edit_image(req: ImageEditRequest):
    job = job_manager.schedule(lambda job_id: run_image_edit(job_id, req))
    return JobResponse(jobId=job.id, status=job.status)
