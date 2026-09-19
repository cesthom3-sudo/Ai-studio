"""
Resolves the configured provider name (from .env) to a concrete implementation.
Only 'demo' ships fully wired. Add real providers here as needed — see
providers/example_http_provider.py for a template calling a real HTTP AI API.
"""

from app.config import settings
from app.providers.base import (
    AIAudioProvider,
    AIImageProvider,
    AITextProvider,
    AITTSProvider,
    AIVideoProvider,
)
from app.providers.demo import (
    DemoAudioProvider,
    DemoImageProvider,
    DemoTextProvider,
    DemoTTSProvider,
    DemoVideoProvider,
)


def get_text_provider() -> AITextProvider:
    return DemoTextProvider()


def get_image_provider() -> AIImageProvider:
    return DemoImageProvider()


def get_video_provider() -> AIVideoProvider:
    return DemoVideoProvider()


def get_tts_provider() -> AITTSProvider:
    return DemoTTSProvider()


def get_audio_provider() -> AIAudioProvider:
    return DemoAudioProvider()


def provider_flags() -> dict:
    return {
        "text": settings.text_provider != "demo",
        "image": settings.image_provider != "demo",
        "video": settings.video_provider != "demo",
        "audio": settings.audio_provider != "demo",
        "tts": settings.tts_provider != "demo",
        "demoMode": settings.demo_mode,
    }
