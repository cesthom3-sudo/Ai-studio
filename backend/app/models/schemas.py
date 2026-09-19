from typing import List, Optional
from pydantic import BaseModel


class ImageGenerateRequest(BaseModel):
    prompt: str
    aspectRatio: str = "1:1"
    style: str = "photorealistic"
    quality: str = "standard"
    variations: int = 1


class ImageEditRequest(BaseModel):
    imageBase64: str
    instruction: str


class VideoGenerateRequest(BaseModel):
    prompt: str
    durationSeconds: int = 5
    aspectRatio: str = "9:16"
    style: str = "cinematic"


class VideoFromImageRequest(BaseModel):
    imageBase64: str
    motionPrompt: str
    durationSeconds: int = 5
    aspectRatio: str = "9:16"


class VoiceGenerateRequest(BaseModel):
    text: str
    voice: str = "default"


class AudioGenerateRequest(BaseModel):
    prompt: str
    durationSeconds: int = 5


class AdCreateRequest(BaseModel):
    productImageBase64: Optional[str] = None
    productName: str = ""
    objective: str = ""
    audience: str = ""
    tone: str = "Energetic"
    durationSeconds: int = 15
    aspectRatio: str = "9:16"
    callToAction: str = "Shop now"
    freeformPrompt: Optional[str] = None


class JobResponse(BaseModel):
    jobId: str
    status: str


class StoryboardScene(BaseModel):
    index: int
    startSeconds: int
    endSeconds: int
    description: str


class JobStatusResponse(BaseModel):
    jobId: str
    status: str
    progress: int = 0
    resultUrl: Optional[str] = None
    storyboard: Optional[List[StoryboardScene]] = None
    error: Optional[str] = None


class ProjectDto(BaseModel):
    id: str
    name: str
    createdAt: str
    prompt: Optional[str] = None
    thumbnailUrl: Optional[str] = None
    type: str


class CreateProjectRequest(BaseModel):
    name: str
    type: str
    prompt: Optional[str] = None


class ProviderStatus(BaseModel):
    text: bool
    image: bool
    video: bool
    audio: bool
    tts: bool
    demoMode: bool
