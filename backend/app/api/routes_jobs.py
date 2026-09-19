from fastapi import APIRouter, HTTPException

from app.models.schemas import JobStatusResponse
from app.workers.jobs import job_manager

router = APIRouter(prefix="/api/jobs", tags=["jobs"])


@router.get("/{job_id}", response_model=JobStatusResponse)
async def get_job(job_id: str):
    job = job_manager.get(job_id)
    if job is None:
        raise HTTPException(status_code=404, detail="Job not found")
    return JobStatusResponse(
        jobId=job.id,
        status=job.status,
        progress=job.progress,
        resultUrl=job.result_url,
        storyboard=job.storyboard,
        error=job.error,
    )
