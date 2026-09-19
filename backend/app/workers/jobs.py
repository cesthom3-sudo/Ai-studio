"""
Minimal in-memory async job system.

POST endpoints create a job and return a job_id immediately.
A background asyncio task performs the work.
GET /api/jobs/{job_id} reports progress until completion.

For V1 this is intentionally simple (in-process, not persisted across
restarts). It can be swapped for Celery/Redis later without changing
the API surface.
"""

import asyncio
import uuid
from dataclasses import dataclass, field
from typing import Any, Callable, Coroutine, Dict, List, Optional

from app.models.schemas import StoryboardScene


@dataclass
class Job:
    id: str
    status: str = "queued"  # queued | processing | completed | failed
    progress: int = 0
    result_url: Optional[str] = None
    storyboard: Optional[List[StoryboardScene]] = None
    error: Optional[str] = None


class JobManager:
    def __init__(self):
        self._jobs: Dict[str, Job] = {}

    def create(self) -> Job:
        job = Job(id=str(uuid.uuid4()))
        self._jobs[job.id] = job
        return job

    def get(self, job_id: str) -> Optional[Job]:
        return self._jobs.get(job_id)

    def set_progress(self, job_id: str, progress: int, storyboard: Optional[List[StoryboardScene]] = None):
        job = self._jobs.get(job_id)
        if job:
            job.status = "processing"
            job.progress = progress
            if storyboard is not None:
                job.storyboard = storyboard

    def complete(self, job_id: str, result_url: str, storyboard: Optional[List[StoryboardScene]] = None):
        job = self._jobs.get(job_id)
        if job:
            job.status = "completed"
            job.progress = 100
            job.result_url = result_url
            if storyboard is not None:
                job.storyboard = storyboard

    def fail(self, job_id: str, error: str):
        job = self._jobs.get(job_id)
        if job:
            job.status = "failed"
            job.error = error

    def schedule(self, coro_factory: Callable[[str], Coroutine[Any, Any, None]]) -> Job:
        """Creates a job and schedules coro_factory(job_id) to run it in the background."""
        job = self.create()

        async def _runner():
            try:
                await coro_factory(job.id)
            except Exception as exc:  # noqa: BLE001 - surface any provider/pipeline error to the client
                self.fail(job.id, str(exc))

        asyncio.create_task(_runner())
        return job


job_manager = JobManager()
