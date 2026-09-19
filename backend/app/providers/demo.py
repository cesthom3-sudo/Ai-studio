"""
Demo providers used when no real API key is configured (DEMO MODE).
They return clearly-labelled placeholder results so the app is demonstrable
without pretending fake output is real AI generation.
"""

import asyncio
from typing import List

from app.providers.base import (
    AIAudioProvider,
    AIImageProvider,
    AITextProvider,
    AITTSProvider,
    AIVideoProvider,
)

DEMO_IMAGE = "https://placehold.co/1024x1024/17171D/F5F5F7?text=DEMO+IMAGE"
DEMO_VIDEO = "https://example.com/demo-assets/demo-video.mp4"
DEMO_AUDIO = "https://example.com/demo-assets/demo-audio.mp3"


class DemoTextProvider(AITextProvider):
    async def generate_text(self, prompt: str) -> str:
        await asyncio.sleep(0.3)
        return f"[DEMO MODE] Generated text response for: {prompt[:80]}"


class DemoImageProvider(AIImageProvider):
    async def generate_image(self, prompt: str, aspect_ratio: str, style: str, variations: int) -> List[str]:
        await asyncio.sleep(1)
        return [DEMO_IMAGE for _ in range(max(1, variations))]

    async def edit_image(self, image_base64: str, instruction: str) -> str:
        await asyncio.sleep(1)
        return DEMO_IMAGE


class DemoVideoProvider(AIVideoProvider):
    async def generate_video(self, prompt: str, duration_seconds: int, aspect_ratio: str, style: str) -> str:
        await asyncio.sleep(1.5)
        return DEMO_VIDEO

    async def video_from_image(self, image_base64: str, motion_prompt: str, duration_seconds: int, aspect_ratio: str) -> str:
        await asyncio.sleep(1.5)
        return DEMO_VIDEO


class DemoTTSProvider(AITTSProvider):
    async def synthesize_speech(self, text: str, voice: str) -> str:
        await asyncio.sleep(0.5)
        return DEMO_AUDIO


class DemoAudioProvider(AIAudioProvider):
    async def generate_audio(self, prompt: str, duration_seconds: int) -> str:
        await asyncio.sleep(0.5)
        return DEMO_AUDIO
