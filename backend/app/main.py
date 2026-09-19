from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api import (
    routes_ad,
    routes_audio,
    routes_image,
    routes_jobs,
    routes_projects,
    routes_status,
    routes_video,
)

app = FastAPI(title="Thomas AI Studio Backend", version="1.0.0")

# Allow the Android app (and local development) to call this API.
# Tighten allow_origins for production deployments.
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(routes_image.router)
app.include_router(routes_video.router)
app.include_router(routes_audio.router)
app.include_router(routes_ad.router)
app.include_router(routes_jobs.router)
app.include_router(routes_projects.router)
app.include_router(routes_status.router)


@app.get("/")
async def root():
    return {"name": "Thomas AI Studio Backend", "status": "running"}


@app.get("/health")
async def health():
    return {"status": "ok"}
