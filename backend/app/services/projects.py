"""
Practical V1 project storage: JSON file on disk under STORAGE_DIR.
Swap for a real database later without changing the API surface.
"""

import json
import os
import uuid
from datetime import datetime, timezone
from typing import List

from app.config import settings
from app.models.schemas import CreateProjectRequest, ProjectDto

_STORE_PATH = os.path.join(settings.storage_dir, "projects.json")


def _load() -> List[dict]:
    if not os.path.exists(_STORE_PATH):
        return []
    with open(_STORE_PATH, "r") as f:
        return json.load(f)


def _save(projects: List[dict]) -> None:
    with open(_STORE_PATH, "w") as f:
        json.dump(projects, f, indent=2)


def list_projects() -> List[ProjectDto]:
    return [ProjectDto(**p) for p in _load()]


def create_project(req: CreateProjectRequest) -> ProjectDto:
    projects = _load()
    project = ProjectDto(
        id=str(uuid.uuid4()),
        name=req.name,
        createdAt=datetime.now(timezone.utc).isoformat(),
        prompt=req.prompt,
        type=req.type,
    )
    projects.append(project.model_dump())
    _save(projects)
    return project


def delete_project(project_id: str) -> None:
    projects = [p for p in _load() if p["id"] != project_id]
    _save(projects)
