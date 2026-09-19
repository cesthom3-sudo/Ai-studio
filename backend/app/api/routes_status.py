from fastapi import APIRouter

from app.models.schemas import ProviderStatus
from app.providers.factory import provider_flags

router = APIRouter(prefix="/api", tags=["status"])


@router.get("/status", response_model=ProviderStatus)
async def get_status():
    return ProviderStatus(**provider_flags())
