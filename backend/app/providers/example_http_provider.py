"""
TEMPLATE ONLY — not wired in by default.

Shows how to implement a real provider against an HTTP-based AI API.
Copy this pattern for your chosen image/video/audio/TTS/text vendor,
then register it in providers/factory.py and set the matching
*_PROVIDER env var in .env to enable it.
"""

import httpx

from app.config import settings
from app.providers.base import AIImageProvider


class ExampleHttpImageProvider(AIImageProvider):
    def __init__(self):
        self.api_key = settings.image_api_key
        self.base_url = "https://api.example-image-provider.com/v1"

    async def generate_image(self, prompt: str, aspect_ratio: str, style: str, variations: int):
        async with httpx.AsyncClient(timeout=60) as client:
            response = await client.post(
                f"{self.base_url}/generate",
                headers={"Authorization": f"Bearer {self.api_key}"},
                json={
                    "prompt": prompt,
                    "aspect_ratio": aspect_ratio,
                    "style": style,
                    "n": variations,
                },
            )
            response.raise_for_status()
            data = response.json()
            return [item["url"] for item in data["images"]]

    async def edit_image(self, image_base64: str, instruction: str) -> str:
        async with httpx.AsyncClient(timeout=60) as client:
            response = await client.post(
                f"{self.base_url}/edit",
                headers={"Authorization": f"Bearer {self.api_key}"},
                json={"image_base64": image_base64, "instruction": instruction},
            )
            response.raise_for_status()
            return response.json()["url"]
