from fastapi import APIRouter

from app.models.schemas import AdCreateRequest, JobResponse
from app.services.ad_pipeline import run_ad_create
from app.workers.jobs import job_manager

router = APIRouter(prefix="/api/ad", tags=["ad"])


@router.post("/create", response_model=JobResponse)
async def create_ad(req: AdCreateRequest):
    job = job_manager.schedule(lambda job_id: run_ad_create(job_id, req))
    return JobResponse(jobId=job.id, status=job.status)
