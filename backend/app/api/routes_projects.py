from fastapi import APIRouter

from app.models.schemas import CreateProjectRequest, ProjectDto
from app.services import projects as projects_service

router = APIRouter(prefix="/api/projects", tags=["projects"])


@router.get("", response_model=list[ProjectDto])
async def list_projects():
    return projects_service.list_projects()


@router.post("", response_model=ProjectDto)
async def create_project(req: CreateProjectRequest):
    return projects_service.create_project(req)


@router.delete("/{project_id}")
async def delete_project(project_id: str):
    projects_service.delete_project(project_id)
    return {"deleted": project_id}
