"""
Simple provider abstraction so the backend is not hard-coded to one AI vendor.

To add a real provider:
  1. Create a class implementing the relevant interface below.
  2. Register it in providers/factory.py against a provider name.
  3. Set the matching *_PROVIDER env var in .env to that name.
"""

from abc import ABC, abstractmethod
from typing import List, Optional


class AITextProvider(ABC):
    @abstractmethod
    async def generate_text(self, prompt: str) -> str:
        ...


class AIImageProvider(ABC):
    @abstractmethod
    async def generate_image(self, prompt: str, aspect_ratio: str, style: str, variations: int) -> List[str]:
        """Returns a list of URLs/paths to generated images."""
        ...

    @abstractmethod
    async def edit_image(self, image_base64: str, instruction: str) -> str:
        """Returns a URL/path to the edited image."""
        ...


class AIVideoProvider(ABC):
    @abstractmethod
    async def generate_video(self, prompt: str, duration_seconds: int, aspect_ratio: str, style: str) -> str:
        ...

    @abstractmethod
    async def video_from_image(self, image_base64: str, motion_prompt: str, duration_seconds: int, aspect_ratio: str) -> str:
        ...


class AITTSProvider(ABC):
    @abstractmethod
    async def synthesize_speech(self, text: str, voice: str) -> str:
        """Returns a URL/path to the generated audio file."""
        ...


class AIAudioProvider(ABC):
    @abstractmethod
    async def generate_audio(self, prompt: str, duration_seconds: int) -> str:
        """Returns a URL/path to generated sound effects / background audio."""
        ...
